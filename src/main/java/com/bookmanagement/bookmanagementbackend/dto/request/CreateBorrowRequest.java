// src/main/java/com/bookmanagement/bookmanagementbackend/dto/request/CreateBorrowRequest.java
package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateBorrowRequest {
    private Long userId;
    private List<Long> bookIds;

    private String name;
    private String phone;
    private String address;
    private String note;

    private String paymentMethod; // "COD" | "QR"
    private Integer shippingFee;  // 15000
}
