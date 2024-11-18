package com.kosa.backend.funding.project.dto.responsedto;

import com.kosa.backend.common.dto.FileDTO;
import com.kosa.backend.common.entity.Files;
import com.kosa.backend.funding.project.dto.*;
import com.kosa.backend.funding.project.entity.BusinessMaker;
import com.kosa.backend.funding.project.entity.PersonalMaker;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.funding.support.entity.FundingSupport;
import com.kosa.backend.user.dto.MakerDTO;
import com.kosa.backend.user.entity.Maker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseProjectDTO {
    private int id;
    private String fundingTitle;
    private int targetAmount;
    private int currentAmount;
    private int complaintCount;
    private LocalDateTime fundingStartDate;
    private LocalDateTime fundingEndDate;
    private String makerType;
    private String repName;
    private String repEmail;
    private String fundingExplanation;
    private String fundingTag;
    private boolean saveStatus;
    private boolean isPublished;
    private LocalDateTime publishDate;
    private SubCategoryDTO subCategory;
    private MakerDTO maker;
    private BusinessMakerDTO businessMaker;
    private PersonalMakerDTO personalMaker;
    List<RewardDTO> rewards;
    private List<RewardInfoDTO> rewardInfo;

//    List<FileDTO> imagesPath;
    private FileDTO thumbnail;
    private FileDTO detailImage;

    private int subCategoryId;
    private int mainCategoryId;

    private String subCategoryName;
    private String mainCategoryName;
}
