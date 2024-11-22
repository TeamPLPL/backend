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
    private RewardInfoDTO rewardInfo;

    private FileDTO thumbnail;
    private FileDTO detailImage;

    private int subCategoryId;
    private int mainCategoryId;

    private String subCategoryName;
    private String mainCategoryName;

    public boolean isIntroCompleted() {
        return fundingTitle != null && !fundingTitle.equals("")
                && subCategoryId > -1
                && mainCategoryId > -1
                && targetAmount > -1;
    }

    public boolean isScheduleComplete() {
        return fundingStartDate != null && fundingEndDate != null;
    }

    public boolean isInfoComplete() {
        if ("personal".equals(makerType)) { // String 비교 안전하게 처리
            return repName != null && !repName.equals("")
                    && repEmail != null && !repEmail.equals("")
                    && personalMaker != null
                    && personalMaker.getIdentityCard() != null && !personalMaker.getIdentityCard().equals("")
                    && thumbnail != null
                    && detailImage != null
                    && fundingExplanation != null && !fundingExplanation.equals("");
        } else {
            return repName != null && !repName.equals("")
                    && repEmail != null && !repEmail.equals("")
                    && businessMaker != null
                    && businessMaker.getBusinessRegistNum() != null && !businessMaker.getBusinessRegistNum().equals("")
                    && businessMaker.getCompanyName() != null && !businessMaker.getCompanyName().equals("")
                    && thumbnail != null
                    && detailImage != null
                    && fundingExplanation != null && !fundingExplanation.equals("");
        }
    }


    public boolean isRewardComplete() {
        return rewards != null && rewards.size() > 0;
    }

    public boolean isRewardInfoComplete() {
        return rewardInfo != null
                && rewardInfo.getModelName() != null && !rewardInfo.getModelName().equals("")
                && rewardInfo.getProductMaterial() != null && !rewardInfo.getProductMaterial().equals("")
                && rewardInfo.getColor() != null && !rewardInfo.getColor().equals("")
                && rewardInfo.getField() != null && !rewardInfo.getField().equals("")
                && rewardInfo.getManufacturer() != null && !rewardInfo.getManufacturer().equals("")
                && rewardInfo.getManufacturingCountry() != null && !rewardInfo.getManufacturingCountry().equals("")
                && rewardInfo.getManufactureDate() != null && !rewardInfo.getManufactureDate().equals("");
    }
}
