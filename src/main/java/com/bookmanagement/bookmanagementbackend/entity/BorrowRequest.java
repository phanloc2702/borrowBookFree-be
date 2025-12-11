package com.bookmanagement.bookmanagementbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrow_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Thông tin giao nhận
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "shipping_fee")
    private Integer shippingFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    @ElementCollection
    @CollectionTable(
            name = "borrow_request_books",
            joinColumns = @JoinColumn(name = "request_id")
    )
    @Column(name = "book_id")
    private List<Long> bookIds = new ArrayList<>();


    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Borrowing> borrowings = new ArrayList<>();
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) this.status = Status.PENDING;
        if (this.paymentMethod == null) this.paymentMethod = PaymentMethod.COD;
        if (this.shippingFee == null) this.shippingFee = 0;
    }

    public enum PaymentMethod {
        COD, QR
    }

    public enum Status {
        PENDING, APPROVED, REJECTED, CANCELED
    }
}
