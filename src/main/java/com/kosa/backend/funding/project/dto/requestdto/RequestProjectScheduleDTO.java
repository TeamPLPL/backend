package com.kosa.backend.funding.project.dto.requestdto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RequestProjectScheduleDTO {
    private LocalDateTime fundingStartDate;
    private LocalDateTime fundingEndDate;
}
