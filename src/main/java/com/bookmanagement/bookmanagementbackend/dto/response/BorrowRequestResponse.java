// src/main/java/com/bookmanagement/bookmanagementbackend/dto/response/BorrowRequestResponse.java
package com.bookmanagement.bookmanagementbackend.dto.response;

import com.bookmanagement.bookmanagementbackend.entity.BorrowRequest;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class BorrowRequestResponse {

    private Long id;

    private Long userId;
    private String userFullName;
    private String userEmail;

    private String name;
    private String phone;
    private String address;
    private String note;

    private BorrowRequest.PaymentMethod paymentMethod;
    private Integer shippingFee;
    private BorrowRequest.Status status;

    private Instant createdAt;

    // Danh sách phiếu mượn trong đơn
    private List<BorrowingResponse> borrowings;
}
