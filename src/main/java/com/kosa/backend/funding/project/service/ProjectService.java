package com.kosa.backend.funding.project.service;

import com.kosa.backend.common.dto.FileDTO;
import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.repository.FilesRepository;
import com.kosa.backend.common.service.S3CustomService;
import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.funding.project.dto.*;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectInfoDTO;
import com.kosa.backend.funding.project.dto.responsedto.ResponseProjectDTO;
import com.kosa.backend.funding.project.dto.responsedto.ResponseProjectInfoDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectIntroDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectScheduleDTO;
import com.kosa.backend.funding.project.entity.*;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.funding.project.repository.*;
import com.kosa.backend.user.dto.MakerDTO;
import com.kosa.backend.user.entity.Maker;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final FundingRepository fundingRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PersoanlMakerRepository persoanlMakerRepository;
    private final BusinessMakerRepository businessMakerRepository;
    private final RewardInfoRepository rewardInfoRepository;
    private final FilesRepository filesRepository;
    private final S3Service s3Service;
    private final MainCategoryRepository mainCategoryRepository;
    private final S3CustomService s3CustomService;

    // 프로젝트 작성한 사용자 가져오기
    public int getProjectUser(int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. Funding에 존재하는 Maker 조회
        return funding.getMaker().getId();
    }

    // 프로젝트 생성
    public int save(Maker maker) {
        return fundingRepository.save(RequestProjectDTO.toSaveEntity(maker)).getId();
    }

    // 프로젝트 입력(제목, 카테고리, 목표 금액)
    @Transactional
    public int updateIntro(RequestProjectIntroDTO projectIntroDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. subcateogry 저장
        int subCategoryId = projectIntroDTO.getSubCategory();
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).get();

        // 4. 필요한 필드(제목, 카테고리)만 업데이트
        funding.updateFundingTitle(projectIntroDTO.getFundingTitle());
        funding.updateSubCategory(subCategory);
        funding.updateTargetAmount(projectIntroDTO.getTargetAmount());

        // 5. 저장
        return fundingRepository.save(funding).getId();
    }

    @Transactional
    public ResponseProjectDTO getProject(int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        ResponseProjectDTO responseDTO = new ResponseProjectDTO();

        responseDTO.setId(funding.getId());
        responseDTO.setFundingTitle(funding.getFundingTitle());
        responseDTO.setTargetAmount(funding.getTargetAmount());
        responseDTO.setFundingStartDate(funding.getFundingStartDate());
        responseDTO.setFundingEndDate(funding.getFundingEndDate());
        responseDTO.setMakerType(funding.getMakerType() != null ? funding.getMakerType().toString() : null);
        responseDTO.setRepName(funding.getRepName());
        responseDTO.setRepEmail(funding.getRepEmail());
        responseDTO.setFundingExplanation(funding.getFundingExplanation());
        responseDTO.setFundingTag(funding.getFundingTag());
        responseDTO.setSaveStatus(funding.getSaveStatus());
        responseDTO.setPublished(funding.isPublished());
        responseDTO.setPublishDate(funding.getPublishDate());

        responseDTO.setSubCategory(funding.getSubCategory() != null ? SubCategoryDTO.fromSubCategory(funding.getSubCategory()) : null);
        responseDTO.setMaker(funding.getMaker() != null ? MakerDTO.fromEntity(funding.getMaker()) : null);
        responseDTO.setBusinessMaker(funding.getBusinessMaker() != null ? BusinessMakerDTO.fromEntity(funding.getBusinessMaker()) : null);
        responseDTO.setPersonalMaker(funding.getPersonalMaker() != null ? PersonalMakerDTO.fromEntity(funding.getPersonalMaker()) : null);

        responseDTO.setSubCategoryId(
                Optional.ofNullable(funding.getSubCategory())
                        .map(SubCategory::getId)
                        .orElse(0) // 기본값을 0으로 설정
        );

        responseDTO.setMainCategoryId(
                Optional.ofNullable(funding.getSubCategory())
                        .map(SubCategory::getMainCategory)
                        .map(MainCategory::getId)
                        .orElse(0) // 기본값을 0으로 설정
        );


        responseDTO.setSubCategoryName(
                funding.getSubCategory() != null && funding.getSubCategory().getSubCategoryName() != null
                        ? funding.getSubCategory().getSubCategoryName()
                        : null
        );

        responseDTO.setMainCategoryName(
                funding.getSubCategory() != null
                        && funding.getSubCategory().getMainCategory() != null
                        && funding.getSubCategory().getMainCategory().getMainCategoryName() != null
                        ? funding.getSubCategory().getMainCategory().getMainCategoryName()
                        : null
        );

        // rewardList 불러오기
        List<Reward> rewardsList = funding.getRewards();
        List<RewardDTO> rewardDTOList = new ArrayList<>();
        if (rewardsList != null) {
            for (Reward reward : rewardsList) {
                rewardDTOList.add(RewardDTO.fromEntity(reward));
            }
        }
        responseDTO.setRewards(rewardDTOList);

        // rewardInfoList 불러오기
        List<RewardInfo> rewardInfoList = rewardInfoRepository.findByFunding(funding);
        List<RewardInfoDTO> rewardInfoDTOList = new ArrayList<>();
        if (rewardInfoList != null) {
            for (RewardInfo rewardInfo : rewardInfoList) {
                rewardInfoDTOList.add(RewardInfoDTO.fromEntity(rewardInfo));
            }
        }
        responseDTO.setRewardInfo(rewardInfoDTOList);

        // Files 불러오기
        FileDTO thumbnail = s3CustomService.getThumbnailByFundingId(funding.getId());
        if (thumbnail != null) {
            responseDTO.setThumbnail(thumbnail);
        }

        FileDTO imagesPathList = s3CustomService.getDetailByFundingId(funding.getId());
        if (imagesPathList != null) {
            responseDTO.setDetailImage(imagesPathList);
        }

        return responseDTO;
    }


    @Transactional
    public List<ResponseProjectDTO> getAllProjects(Maker maker) {
        // 1. 모든 Funding 객체 조회
        List<Funding> fundingList = fundingRepository.findAllByMaker(maker);

        // 2. 각 Funding 객체를 ResponseProjectDTO로 변환
        List<ResponseProjectDTO> responseDTOList = new ArrayList<>();
        for (Funding funding : fundingList) {
            ResponseProjectDTO responseDTO = new ResponseProjectDTO();

            responseDTO.setId(funding.getId());
            responseDTO.setFundingTitle(funding.getFundingTitle());
            responseDTO.setTargetAmount(funding.getTargetAmount());
            responseDTO.setFundingStartDate(funding.getFundingStartDate());
            responseDTO.setFundingEndDate(funding.getFundingEndDate());
            responseDTO.setMakerType(funding.getMakerType() != null ? funding.getMakerType().toString() : null);
            responseDTO.setRepName(funding.getRepName());
            responseDTO.setRepEmail(funding.getRepEmail());
            responseDTO.setFundingExplanation(funding.getFundingExplanation());
            responseDTO.setFundingTag(funding.getFundingTag());
            responseDTO.setSaveStatus(funding.getSaveStatus());
            responseDTO.setPublished(funding.isPublished());
            responseDTO.setPublishDate(funding.getPublishDate());

            responseDTO.setSubCategory(funding.getSubCategory() != null ? SubCategoryDTO.fromSubCategory(funding.getSubCategory()) : null);
            responseDTO.setMaker(funding.getMaker() != null ? MakerDTO.fromEntity(funding.getMaker()) : null);
            responseDTO.setBusinessMaker(funding.getBusinessMaker() != null ? BusinessMakerDTO.fromEntity(funding.getBusinessMaker()) : null);
            responseDTO.setPersonalMaker(funding.getPersonalMaker() != null ? PersonalMakerDTO.fromEntity(funding.getPersonalMaker()) : null);

            // rewardList 불러오기
            List<Reward> rewardsList = funding.getRewards();
            List<RewardDTO> rewardDTOList = new ArrayList<>();
            if (rewardsList != null) {
                for (Reward reward : rewardsList) {
                    if (reward != null) {
                        rewardDTOList.add(RewardDTO.fromEntity(reward));
                    }
                }
            }
            responseDTO.setRewards(rewardDTOList);

            // rewardInfoList 불러오기
            List<RewardInfo> rewardInfoList = rewardInfoRepository.findByFunding(funding);
            List<RewardInfoDTO> rewardInfoDTOList = new ArrayList<>();
            if (rewardInfoList != null) {
                for (RewardInfo rewardInfo : rewardInfoList) {
                    if (rewardInfo != null) {
                        rewardInfoDTOList.add(RewardInfoDTO.fromEntity(rewardInfo));
                    }
                }
            }
            responseDTO.setRewardInfo(rewardInfoDTOList);

            // Files 불러오기
            FileDTO thumbnail = s3CustomService.getThumbnailByFundingId(funding.getId());
            if (thumbnail != null) {
                responseDTO.setThumbnail(thumbnail);
            }

            // 3. 변환된 DTO를 리스트에 추가
            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }

    @Transactional
    public ResponseProjectInfoDTO getAllProjectInfo(int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. ResponseProjectInfoDTO 생성 및 초기화
        ResponseProjectInfoDTO responseDTO = new ResponseProjectInfoDTO();

        // 3. 메이커 유형
        responseDTO.setMakerType(funding.getMakerType() != null ? funding.getMakerType().toString() : null);

        Optional<BusinessMaker> businessMakerOptional = businessMakerRepository.findByFunding(funding);
        Optional<PersonalMaker> personalMakerOptional = persoanlMakerRepository.findByFunding(funding);

        // 4. 대표자 이름 및 이메일
        if (businessMakerOptional.isPresent()) {
            BusinessMaker businessMaker = businessMakerOptional.get();
            responseDTO.setRepName(funding.getRepName() != null ? funding.getRepName() : null);
            responseDTO.setRepEmail(funding.getRepEmail() != null ? funding.getRepEmail() : null);

            // 6. 사업자 정보
            responseDTO.setBusinessRegistNum(businessMaker.getBusinessRegistNum() != null ? businessMaker.getBusinessRegistNum() : null);
            responseDTO.setBusinessRegistCertif(businessMaker.getBusinessRegistCertif() != null ? businessMaker.getBusinessRegistCertif() : null);
            responseDTO.setCompanyName(businessMaker.getCompanyName() != null ? businessMaker.getCompanyName() : null);
        } else if (personalMakerOptional.isPresent()) {
            PersonalMaker personalMaker = personalMakerOptional.get();
            responseDTO.setRepName(funding.getRepName() != null ? funding.getRepName() : null);
            responseDTO.setRepEmail(funding.getRepEmail() != null ? funding.getRepEmail() : null);

            // 5. 개인 신분증
            responseDTO.setIdentityCard(personalMaker.getIdentityCard() != null ? personalMaker.getIdentityCard() : null);
        } else {
            // 둘 다 없는 경우 기본값 처리
            responseDTO.setRepName(null);
            responseDTO.setRepEmail(null);
            responseDTO.setIdentityCard(null);
            responseDTO.setBusinessRegistNum(null);
            responseDTO.setBusinessRegistCertif(null);
            responseDTO.setCompanyName(null);
        }

        // 7. 펀딩 설명
        responseDTO.setFundingExplanation(funding.getFundingExplanation() != null ? funding.getFundingExplanation() : null);

        // 8. 펀딩 태그
        responseDTO.setFundingTag(funding.getFundingTag() != null ? funding.getFundingTag() : null);

        return responseDTO;
    }



    // 프로젝트 입력(펀딩 시작일, 펀딩 종료일)
    @Transactional
    public int updateSchedule(RequestProjectScheduleDTO projectScheduleDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. 필요한 필드(펀딩 시작일, 펀딩 종료일)만 업데이트
        funding.updateFundingStartDate(projectScheduleDTO.getFundingStartDate());
        funding.updateFundingEndDate(projectScheduleDTO.getFundingEndDate());

        // 5. 저장
        return fundingRepository.save(funding).getId();
    }

    // 프로젝트 정보 입력
    @Transactional
    public int updateInfo(RequestProjectInfoDTO projectInfoDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. 필요한 필드(대표자 이름, 대표자 이메일)만 업데이트, setter 리펙토링 완료(setter 사용 안함)
        if (projectInfoDTO.getRepName() != null && !projectInfoDTO.getRepName().equals("")) {
            funding.updateRepName(projectInfoDTO.getRepName());
        }
        if (projectInfoDTO.getRepEmail() != null && !projectInfoDTO.getRepEmail().equals("")) {
            funding.updateRepEmail(projectInfoDTO.getRepEmail());
        }

        // 4. 필요한 필드(펀딩 설명)만 업데이트
        if (projectInfoDTO.getFundingExplanation() != null && !projectInfoDTO.getFundingExplanation().equals("")) {
            funding.updateFundingExplanation(projectInfoDTO.getFundingExplanation());
        }

        // 6. 메이커 유형 선택
        makerType(projectInfoDTO, funding);

        // 5. 필요한 필드(펀딩 태그)만 업데이트
        if (projectInfoDTO.getFundingTag() != null && !projectInfoDTO.getFundingTag().equals("")) {
            funding.updateFundingTag(projectInfoDTO.getFundingTag());
        }

        // 7. 사진 저장

        // 8. 저장
        return fundingRepository.save(funding).getId();
    }

    // 프로젝트 삭제
    public void delete(int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. Funding 삭제
        fundingRepository.delete(funding);
    }

    // 6. 메이커 유형 선택, 메소드 분리
    public void makerType(RequestProjectInfoDTO projectInfoDTO, Funding funding) {
        if (projectInfoDTO.getMakerType() != null && !projectInfoDTO.getMakerType().equals("")) {
            MakerType makerType = null;
            if (projectInfoDTO.getMakerType().equals("personal")) {
                makerType = MakerType.personal;

                PersonalMakerDTO personalMakerDTO = PersonalMakerDTO.builder()
                        .identityCard(projectInfoDTO.getIdentityCard())
                        .build();


                PersonalMaker personalMaker = funding.getPersonalMaker();
                if (personalMaker == null) {
                    // 메이커 타입 존재하지 않을 경우 생성자로 새로운 객체 생성
                    personalMaker = PersonalMaker.builder()
                            .identityCard(projectInfoDTO.getIdentityCard())
                            .build();
                } else {
                    // 메이커 타입 존재할 경우 update
                    personalMaker.updateFromDTO(personalMakerDTO);
                }

                persoanlMakerRepository.save(personalMaker);
                funding.updatePersoanlMaker(personalMaker);

            } else if (projectInfoDTO.getMakerType().equals("business")) {
                makerType = MakerType.business;

                BusinessMakerDTO businessMakerDTO = BusinessMakerDTO.builder()
                        .businessRegistNum(projectInfoDTO.getBusinessRegistNum())
//                        .businessRegistCertif(projectInfoDTO.getBusinessRegistCertif())
                        .companyName(projectInfoDTO.getCompanyName())
                        .build();

                BusinessMaker businessMaker = funding.getBusinessMaker();
                if (businessMaker == null) {
                    // 메이커 타입 존재하지 않을 경우 생성자로 새로운 객체 생성
                    businessMaker = BusinessMaker.builder()
                            .businessRegistNum(businessMakerDTO.getBusinessRegistNum())
                            .businessRegistCertif(businessMakerDTO.getBusinessRegistCertif())
                            .companyName(businessMakerDTO.getCompanyName())
                            .build();
                } else {
                    // 메이커 타입 존재할 경우 update
                    businessMaker.updateFromDTO(businessMakerDTO);
                }
                businessMakerRepository.save(businessMaker);
                funding.updateBusinessMaker(businessMaker);
            }
            funding.updateMakerType(makerType);
        }
    }
}
