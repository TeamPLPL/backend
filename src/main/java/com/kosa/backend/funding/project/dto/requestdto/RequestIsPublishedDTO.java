package com.kosa.backend.funding.project.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestIsPublishedDTO {
    private boolean isPublished;

    @JsonSetter("isPublished") // JSON에서 "isPublished" 키를 처리
    public void setIsPublished(String isPublished) {
        this.isPublished = Boolean.parseBoolean(isPublished); // 문자열 "true"/"false" 변환
    }
}

