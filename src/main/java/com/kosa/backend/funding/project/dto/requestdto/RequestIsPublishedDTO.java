package com.kosa.backend.funding.project.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestIsPublishedDTO {
    private boolean isPublished;
    private LocalDateTime publishDate;
}
