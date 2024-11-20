package com.kosa.backend.funding.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingNoticeDTO {
    private int fundingId;
    private int noticeId;
    private int noticeCategoryId;
    private String noticeTitle;
    private String noticeContent;
    private LocalDateTime noticeDate;
    private LocalDateTime updateDate;
}
