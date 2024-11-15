package com.kosa.backend.funding.project.dto.requestdto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestProjectScheduleDTO {
    private LocalDateTime fundingStartDate;
    private LocalDateTime fundingEndDate;
}
