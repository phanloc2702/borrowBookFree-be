package com.bookmanagement.bookmanagementbackend.dto.request;

import com.bookmanagement.bookmanagementbackend.entity.BorrowRequest;
import lombok.Data;

import java.util.List;

@Data
public class BorrowRequestCreateRequest {
    private Long userId;
    private List<Long> bookIds;
    private String name;
    private String phone;
    private String address;
    private String note;
    private BorrowRequest.PaymentMethod paymentMethod;
}

