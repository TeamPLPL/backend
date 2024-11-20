package com.kosa.backend.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class PaymentHistoryDTO {
    private int id;
    private String category;
    private String subcategory;
    private String status;
    private String title;
    private String author;
    private String date;

    private int currentAmount;
    private int targetAmount;

    private LocalDateTime fundingStartDate;
    private LocalDateTime fundingEndDate;
}
