package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.BorrowRequestCreateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBorrowRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowRequestResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowingResponse;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import com.bookmanagement.bookmanagementbackend.entity.BorrowRequest;
import com.bookmanagement.bookmanagementbackend.entity.Borrowing;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.exception.BusinessException;
import com.bookmanagement.bookmanagementbackend.exception.ErrorCodeConstant;
import com.bookmanagement.bookmanagementbackend.mapper.BookMapper;
import com.bookmanagement.bookmanagementbackend.repository.BookRepository;
import com.bookmanagement.bookmanagementbackend.repository.BorrowRequestRepository;
import com.bookmanagement.bookmanagementbackend.repository.BorrowingRepository;
import com.bookmanagement.bookmanagementbackend.repository.UserRepository;
import com.bookmanagement.bookmanagementbackend.repository.specification.BorrowRequestSpecification;
import com.bookmanagement.bookmanagementbackend.service.BorrowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BorrowRequestServiceImpl implements BorrowRequestService {

    private final BorrowRequestRepository borrowRequestRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BorrowingRepository borrowingRepository;

    // ======================= GET ALL / BY STATUS =======================
    @Override
    @Transactional(readOnly = true)
    public List<BorrowRequestResponse> getAll(String status) {
        List<BorrowRequest> list;
        if (status != null && !status.isBlank()) {
            BorrowRequest.Status st;
            try {
                st = BorrowRequest.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(
                        "Trạng thái không hợp lệ",
                        ErrorCodeConstant.INVALID_REQUEST
                );
            }
            list = borrowRequestRepository.findByStatusOrderByCreatedAtDesc(st);
        } else {
            list = borrowRequestRepository.findAllByOrderByCreatedAtDesc();
        }
        return list.stream().map(this::toResponse).toList();
    }

    // ======================= GET BY ID =======================
    @Override
    @Transactional(readOnly = true)
    public BorrowRequestResponse getById(Long id) {
        BorrowRequest r = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Đơn mượn không tồn tại",
                        ErrorCodeConstant.BORROWING_NOT_FOUND
                ));
        return toResponse(r);
    }

    // ======================= GET BY USER =======================
    @Override
    @Transactional(readOnly = true)
    public List<BorrowRequestResponse> getByUser(Long userId) {
        List<BorrowRequest> list =
                borrowRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return list.stream().map(this::toResponse).toList();
    }

    // ======================= UPDATE STATUS (ADMIN) =======================
    @Override
    @Transactional
    public BorrowRequestResponse updateStatus(Long id, String newStatus) {
        BorrowRequest request = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Đơn mượn không tồn tại",
                        ErrorCodeConstant.BORROWING_NOT_FOUND
                ));

        BorrowRequest.Status targetStatus;
        try {
            targetStatus = BorrowRequest.Status.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Trạng thái không hợp lệ",
                    ErrorCodeConstant.INVALID_REQUEST
            );
        }

        BorrowRequest.Status oldStatus = request.getStatus();
        if (oldStatus == targetStatus) {
            return toResponse(request);
        }

        // Chỉ xử lý logic tạo Borrowing khi PENDING -> APPROVED
        if (oldStatus == BorrowRequest.Status.PENDING
                && targetStatus == BorrowRequest.Status.APPROVED) {

            List<Long> bookIds = request.getBookIds();
            if (bookIds == null || bookIds.isEmpty()) {
                throw new BusinessException(
                        "Đơn mượn không có sách",
                        ErrorCodeConstant.INVALID_REQUEST
                );
            }

            LocalDate today = LocalDate.now();
            LocalDate dueDate = today.plusDays(14); // hạn trả 14 ngày

            List<Borrowing> newBorrowings = new ArrayList<>();

            for (Long bookId : bookIds) {
                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new BusinessException(
                                "Sách không tồn tại: " + bookId,
                                ErrorCodeConstant.BOOK_NOT_FOUND
                        ));

                if (book.getAvailableQuantity() == null
                        || book.getAvailableQuantity() <= 0) {
                    throw new BusinessException(
                            "Sách '" + book.getTitle() + "' đã hết số lượng cho mượn",
                            ErrorCodeConstant.BOOK_OUT_OF_STOCK
                    );
                }

                Borrowing borrowing = Borrowing.builder()
                        .user(request.getUser())
                        .book(book)
                        .borrowDate(today)
                        .dueDate(dueDate)
                        .status(Borrowing.Status.BORROWED)
                        .request(request)
                        .build();

                newBorrowings.add(borrowing);

                // Giảm số lượng sách
                book.setAvailableQuantity(book.getAvailableQuantity() - 1);
            }

            borrowingRepository.saveAll(newBorrowings);
            bookRepository.saveAll(
                    newBorrowings.stream()
                            .map(Borrowing::getBook)
                            .toList()
            );

            // Gắn vào request để quan hệ 2 chiều đồng bộ
            if (request.getBorrowings() == null) {
                request.setBorrowings(new ArrayList<>());
            }
            request.getBorrowings().addAll(newBorrowings);
        }

        request.setStatus(targetStatus);
        BorrowRequest saved = borrowRequestRepository.save(request);
        return toResponse(saved);
    }

    // ======================= CREATE REQUEST (USER GỬI ĐƠN) =======================
    @Override
    @Transactional
    public BorrowRequestResponse create(BorrowRequestCreateRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new BusinessException(
                        "User không tồn tại",
                        ErrorCodeConstant.USER_NOT_FOUND
                ));

        if (req.getBookIds() == null || req.getBookIds().isEmpty()) {
            throw new BusinessException(
                    "Đơn mượn không có sách",
                    ErrorCodeConstant.INVALID_REQUEST
            );
        }

        BorrowRequest entity = BorrowRequest.builder()
                .user(user)
                .name(req.getName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .note(req.getNote())
                .paymentMethod(
                        req.getPaymentMethod() != null
                                ? req.getPaymentMethod()
                                : BorrowRequest.PaymentMethod.COD
                )
                .shippingFee(15000) // tuỳ bạn, hoặc lấy từ config
                .status(BorrowRequest.Status.PENDING)
                .bookIds(new ArrayList<>(req.getBookIds()))
                .build();

        BorrowRequest saved = borrowRequestRepository.save(entity);
        return toResponse(saved);
    }

    // ======================= FILTER (PHÂN TRANG + TÌM KIẾM) =======================
    @Override
    @Transactional(readOnly = true)
    public Page<BorrowRequestResponse> getBorrowRequests(FilterBorrowRequest filter) {
        Specification<BorrowRequest> spec =
                BorrowRequestSpecification.keywordContains(filter.getKeyword());

        Pageable pageable = PageRequest.of(
                filter.getPageNumber(),
                filter.getPageSize(),
                Sort.by("createdAt").descending()
        );

        Page<BorrowRequest> page = borrowRequestRepository.findAll(spec, pageable);
        return page.map(this::toResponse);
    }

    // ======================= MAPPER =======================
    private BorrowRequestResponse toResponse(BorrowRequest r) {
        // 1. Lấy list borrowings thực tế (sau khi APPROVED)
        List<Borrowing> borrowingList =
                (r.getBorrowings() != null) ? r.getBorrowings() : Collections.emptyList();

        List<BorrowingResponse> borrowingResponses;

        if (!borrowingList.isEmpty()) {
            // Đơn đã được duyệt, có bản ghi Borrowing thật
            borrowingResponses = borrowingList.stream()
                    .map(this::mapBorrowing)
                    .toList();
        } else if (r.getBookIds() != null && !r.getBookIds().isEmpty()) {
            // Đơn còn PENDING => chưa có Borrowing
            // => dùng bookIds để lấy danh sách Book, rồi map sang BorrowingResponse "giả"
            List<Book> books = bookRepository.findAllById(r.getBookIds());

            borrowingResponses = books.stream()
                    .map(book -> {
                        BookResponse bookDto = bookMapper.toBookResponse(book);
                        return BorrowingResponse.builder()
                                .id(null) // chưa có phiếu thật
                                .userId(r.getUser().getId())
                                .userFullName(r.getUser().getFullName())
                                .userEmail(r.getUser().getEmail())
                                .book(bookDto)
                                .borrowDate(null)   // chưa mượn thực tế
                                .dueDate(null)
                                .returnDate(null)
                                .status(null)       // hoặc để BORROWED nếu bạn muốn
                                .build();
                    })
                    .toList();
        } else {
            // Không có borrowings, cũng không có bookIds
            borrowingResponses = Collections.emptyList();
        }



        return BorrowRequestResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userFullName(r.getUser().getFullName())
                .userEmail(r.getUser().getEmail())
                .name(r.getName())
                .phone(r.getPhone())
                .address(r.getAddress())
                .note(r.getNote())
                .paymentMethod(r.getPaymentMethod())
                .shippingFee(r.getShippingFee())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .borrowings(borrowingResponses)
                 // nếu DTO có field này
                .build();
    }


    private BorrowingResponse mapBorrowing(Borrowing b) {
        BookResponse bookDto = bookMapper.toBookResponse(b.getBook());
        return BorrowingResponse.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .userFullName(b.getUser().getFullName())
                .userEmail(b.getUser().getEmail())
                .book(bookDto)
                .borrowDate(b.getBorrowDate())
                .dueDate(b.getDueDate())
                .returnDate(b.getReturnDate())
                .status(b.getStatus())
                .build();
    }
}
