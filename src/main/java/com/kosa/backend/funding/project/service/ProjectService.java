package com.kosa.backend.funding.project.service;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.repository.FilesRepository;
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

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final FundingRepository fundingRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PersoanlMakerRepository persoanlMakerRepository;
    private final BusinessMakerRepository businessMakerRepository;
    private final RewardInfoRepository rewardInfoRepository;
    private final FilesRepository filesRepository;

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

        responseDTO.setId(funding.getMaker().getId());
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

        responseDTO.setSubCategory(SubCategoryDTO.fromSubCategory(funding.getSubCategory()));
        responseDTO.setMaker(MakerDTO.fromEntity(funding.getMaker()));
        responseDTO.setBusinessMaker(BusinessMakerDTO.fromEntity(funding.getBusinessMaker()));
        responseDTO.setPersonalMaker(PersonalMakerDTO.fromEntity(funding.getPersonalMaker()));

        // rewardList 불러오기
        List<Reward> rewardsList = funding.getRewards();
        List<RewardDTO> rewardDTOList = new ArrayList<>();
        for(Reward reward : rewardsList) {
            rewardDTOList.add(RewardDTO.fromEntity(reward));
        }
        responseDTO.setRewards(rewardDTOList);

        // rewardInfoList 불러오기
        List<RewardInfo> rewardInfoList = rewardInfoRepository.findByFunding(funding);
        List<RewardInfoDTO> rewardInfoDTOList = new ArrayList<>();
        for(RewardInfo rewardInfo : rewardInfoList) {
            rewardInfoDTOList.add(RewardInfoDTO.fromEntity(rewardInfo));
        }
        responseDTO.setRewardInfo(rewardInfoDTOList);

        // Files 불러오기
        Files thumbnail = filesRepository.findByFundingIdAndImgType(funding.getId(), ImgType.THUMBNAIL).orElse(null);
        List<Files> filesList = filesRepository.findAllByFundingIdAndImgTypeOrderBySequence(funding.getId(), ImgType.DETAIL_IMAGE);
        List<FilesDTO> filesDTOList = new ArrayList<>();
        filesDTOList.add(FilesDTO.fromEntity(thumbnail));
        for(Files file : filesList) {
            filesDTOList.add(FilesDTO.fromEntity(file));
        }
        responseDTO.setFiles(filesDTOList);

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
        if(projectInfoDTO.getRepName()!=null&&!projectInfoDTO.getRepName().equals("")){
            funding.updateRepName(projectInfoDTO.getRepName());
        }
        if(projectInfoDTO.getRepEmail()!=null&&!projectInfoDTO.getRepEmail().equals("")){
            funding.updateRepEmail(projectInfoDTO.getRepEmail());
        }

        // 4. 필요한 필드(펀딩 설명)만 업데이트
        if(projectInfoDTO.getFundingExplanation()!=null&&!projectInfoDTO.getFundingExplanation().equals("")){
            funding.updateFundingExplanation(projectInfoDTO.getFundingExplanation());
        }

        // 6. 메이커 유형 선택
        makerType(projectInfoDTO, funding);

        // 5. 필요한 필드(펀딩 태그)만 업데이트
        if(projectInfoDTO.getFundingTag()!=null&&!projectInfoDTO.getFundingTag().equals("")){
            funding.updateFundingTag(projectInfoDTO.getFundingTag());
        }

        // 7. 사진 저장

        // 8. 저장
        return fundingRepository.save(funding).getId();
    }

    // 서비스 클래스에 작성
    public ResponseProjectInfoDTO getInfo(int projectId) {
        // 프로젝트를 데이터베이스에서 찾아오기
        Funding funding = fundingRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Funding not found with id: " + projectId));

        // DTO에 엔터티 데이터 매핑
        ResponseProjectInfoDTO responseDTO = new ResponseProjectInfoDTO();

        // 기본 필드 매핑
        responseDTO.setMakerType(funding.getMakerType() != null ? funding.getMakerType().toString() : null);
        responseDTO.setRepName(funding.getRepName());
        responseDTO.setRepEmail(funding.getRepEmail());
        responseDTO.setTargetAmount(funding.getTargetAmount());
        responseDTO.setFundingExplanation(funding.getFundingExplanation());
        responseDTO.setFundingTag(funding.getFundingTag());

//        // 신분증 정보는 Maker의 타입에 따라 매핑
//        if (funding.getMakerType() == MakerType.business && funding.getBusinessMaker() != null) {
//            responseDTO.setBusinessRegistNum(funding.getBusinessMaker().getBusinessRegistNum());
//            responseDTO.setBusinessRegistCertif(funding.getBusinessMaker().getBusinessRegistCertif());
//            responseDTO.setCompanyName(funding.getBusinessMaker().getCompanyName());
//        } else if (funding.getMakerType() == MakerType.personal && funding.getPersonalMaker() != null) {
//            responseDTO.setIdentityCard(funding.getPersonalMaker().getIdentityCard());
//        }
//
//        // 필요한 경우 추가 데이터를 가져와서 DTO에 설정 (예: 사진)
//        if (funding.getFiles() != null && !funding.getFiles().isEmpty()) {
//            // 펀딩 사진 정보 설정 (여러 파일 처리 가능, 예시로 첫 번째 파일 사용)
//            // responseDTO.setFundingPhotos(funding.getFiles().stream().map(file -> file.getUrl()).collect(Collectors.toList()));
//        }
        return responseDTO;
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
    public void makerType(RequestProjectInfoDTO projectInfoDTO, Funding funding){
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
