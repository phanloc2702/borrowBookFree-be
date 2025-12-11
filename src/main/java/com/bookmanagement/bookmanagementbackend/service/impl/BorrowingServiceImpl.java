package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.BorrowingCreateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBorrowingRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowingResponse;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import com.bookmanagement.bookmanagementbackend.entity.BorrowRequest;
import com.bookmanagement.bookmanagementbackend.entity.Borrowing;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.exception.BusinessException;
import com.bookmanagement.bookmanagementbackend.exception.ErrorCodeConstant;
import com.bookmanagement.bookmanagementbackend.repository.BookRepository;
import com.bookmanagement.bookmanagementbackend.repository.BorrowRequestRepository;
import com.bookmanagement.bookmanagementbackend.repository.BorrowingRepository;
import com.bookmanagement.bookmanagementbackend.repository.UserRepository;
import com.bookmanagement.bookmanagementbackend.repository.specification.BookSpecification;
import com.bookmanagement.bookmanagementbackend.repository.specification.BorrowingsSpecification;
import com.bookmanagement.bookmanagementbackend.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BorrowRequestRepository borrowRequestRepository; // repo cho bảng mới
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public List<BorrowingResponse> createBorrowings(BorrowingCreateRequest requestDto) {
        if (requestDto.getBookIds() == null || requestDto.getBookIds().isEmpty()) {
            throw new BusinessException("Danh sách sách mượn đang trống", ErrorCodeConstant.INVALID_REQUEST);
        }

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BusinessException("User not found", ErrorCodeConstant.USER_NOT_FOUND));

        // 1) Tạo BorrowRequest (đơn mượn)
        BorrowRequest borrowRequest = BorrowRequest.builder()
                .user(user)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .address(requestDto.getAddress())
                .note(requestDto.getNote())
                .paymentMethod(
                        "QR".equalsIgnoreCase(requestDto.getPaymentMethod())
                                ? BorrowRequest.PaymentMethod.QR
                                : BorrowRequest.PaymentMethod.COD
                )
                .shippingFee(15000) // phí ship cố định, giống trên FE
                .status(BorrowRequest.Status.PENDING)
                .build();

        borrowRequest = borrowRequestRepository.save(borrowRequest);

        // 2) Tạo nhiều Borrowing (mỗi sách 1 dòng) gắn với Request
        List<BorrowingResponse> responses = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(14);

        for (Long bookId : requestDto.getBookIds()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BusinessException("Book not found", ErrorCodeConstant.BOOK_NOT_FOUND));

            if (book.getAvailableQuantity() == null || book.getAvailableQuantity() <= 0) {
                throw new BusinessException("Sách đã hết: " + book.getTitle(),
                        ErrorCodeConstant.BOOK_OUT_OF_STOCK);
            }

            book.setAvailableQuantity(book.getAvailableQuantity() - 1);
            bookRepository.save(book);

            Borrowing borrowing = Borrowing.builder()
                    .user(user)
                    .book(book)
                    .borrowDate(today)
                    .dueDate(dueDate)
                    .status(Borrowing.Status.BORROWED)
                    .request(borrowRequest)    // 👈 GẮN VỚI ĐƠN
                    .build();

            Borrowing saved = borrowingRepository.save(borrowing);
            responses.add(toResponse(saved));
        }

        return responses;
    }

    private BorrowingResponse toResponse(Borrowing b) {
        User user = b.getUser();
        return BorrowingResponse.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .userFullName(user != null ? user.getFullName() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .borrowDate(b.getBorrowDate())
                .dueDate(b.getDueDate())
                .returnDate(b.getReturnDate())
                .status(b.getStatus())
                .book(
                        BookResponse.builder()
                                .id(b.getBook().getId())
                                .title(b.getBook().getTitle())
                                .author(b.getBook().getAuthor())
                                .build()
                )
                // nếu muốn biết đơn nào:
                // .requestId(b.getRequest() != null ? b.getRequest().getId() : null)
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<BorrowingResponse> getBorrowingsByUser(Long userId) {
        List<Borrowing> list = borrowingRepository.findByUserIdOrderByBorrowDateDesc(userId);
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public BorrowingResponse returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new BusinessException("Phiếu mượn không tồn tại",
                        ErrorCodeConstant.BORROWING_NOT_FOUND));

        if (borrowing.getStatus() == Borrowing.Status.RETURNED) {
            throw new BusinessException("Phiếu mượn đã được trả trước đó",
                    ErrorCodeConstant.INVALID_REQUEST);
        }

        // cập nhật trạng thái
        borrowing.setStatus(Borrowing.Status.RETURNED);
        borrowing.setReturnDate(LocalDate.now());

        // cộng lại số lượng sách
        Book book = borrowing.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        Borrowing saved = borrowingRepository.save(borrowing);
        return toResponse(saved);
    }

    public Page<BorrowingResponse> getBorrowings(FilterBorrowingRequest filterBorrowingRequest){
        Specification<Borrowing> spec = BorrowingsSpecification.keywordContains(filterBorrowingRequest.getKeyword());
        Pageable pageable  = PageRequest.of(filterBorrowingRequest.getPageNumber(), filterBorrowingRequest.getPageSize(), Sort.by("createdAt").descending());
        Page<Borrowing> borrowings = borrowingRepository.findAll(spec, pageable);
        return borrowings.map(this::toResponse);
    }
    @Override
    @Transactional
    public BorrowingResponse updateStatus(Long id, String newStatus) {
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Phiếu mượn không tồn tại",
                        ErrorCodeConstant.BORROWING_NOT_FOUND
                ));

        Borrowing.Status targetStatus;
        try {
            targetStatus = Borrowing.Status.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Trạng thái không hợp lệ",
                    ErrorCodeConstant.INVALID_REQUEST
            );
        }

        Borrowing.Status oldStatus = borrowing.getStatus();
        if (oldStatus == targetStatus) {
            // Không đổi gì
            return toResponse(borrowing);
        }

        // Chỉ xử lý khi chuyển sang RETURNED
        if (targetStatus == Borrowing.Status.RETURNED) {
            // Set ngày trả = hôm nay
            borrowing.setReturnDate(LocalDate.now());
            borrowing.setStatus(Borrowing.Status.RETURNED);

            // Cộng lại số lượng sách
            Book book = borrowing.getBook();
            if (book != null) {
                Integer available = book.getAvailableQuantity();
                if (available == null) available = 0;
                book.setAvailableQuantity(available + 1);
                bookRepository.save(book);
            }
        } else {
            // Các trạng thái khác nếu muốn cho phép (ví dụ BORROWED, OVERDUE)
            borrowing.setStatus(targetStatus);
        }

        Borrowing saved = borrowingRepository.save(borrowing);
        return toResponse(saved);
    }
}

