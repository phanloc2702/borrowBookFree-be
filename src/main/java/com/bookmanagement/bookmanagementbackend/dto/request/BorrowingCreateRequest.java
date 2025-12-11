package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BorrowingCreateRequest {
    private Long userId;
    private List<Long> bookIds;
    private String name;
    private String phone;
    private String address;
    private String note;
    private String paymentMethod; // "COD" / "QR"
}
