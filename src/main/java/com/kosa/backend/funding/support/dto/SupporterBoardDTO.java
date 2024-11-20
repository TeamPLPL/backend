package com.kosa.backend.funding.support.dto;

import com.kosa.backend.funding.support.entity.SupporterBoard;
import com.kosa.backend.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupporterBoardDTO {
    private int id;
    private SupporterBoard.BoardCategory boardCategory = SupporterBoard.BoardCategory.REVIEW; // Enum 타입으로 변경
    private String boardContent;
    private LocalDateTime boardDate;
    private Integer userId; // 필요한 필드만 추가
    private String userNick;
    private int fundingId;
}
