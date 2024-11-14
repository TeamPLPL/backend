package com.kosa.backend.funding.project.dto.requestdto;

import com.kosa.backend.funding.project.entity.BusinessMaker;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.PersonalMaker;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RequestProjectDTO {
    private int id;
    private String fundingTitle;
    private int targetAmount;
    private int currentAmount;
    private int complaintCount;
    private LocalDateTime fundingStartDate;
    private LocalDateTime fundingEndDate;
    private MakerType makerType;
    private String repName;
    private String repEmail;
    private String fundingExplanation;
    private String fundingTag;
    private boolean saveStatus;
    private boolean isPublished;
    private LocalDateTime publishDate;
    private Maker maker;
    private BusinessMaker businessMaker;
    private PersonalMaker personalMaker;
    private SubCategory subCategory;

    public static Funding toSaveEntity(Maker maker) {
        return Funding.builder()
                .targetAmount(0)
                .currentAmount(0)
                .complaintCount(0)
                .saveStatus(false)
                .isPublished(false)
                .maker(maker)
                .build();
    }
}
