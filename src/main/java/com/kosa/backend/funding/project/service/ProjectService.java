package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.dto.BusinessMakerDTO;
import com.kosa.backend.funding.project.dto.PersonalMakerDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectInfoDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectIntroDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectScheduleDTO;
import com.kosa.backend.funding.project.entity.*;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.funding.project.repository.*;
import com.kosa.backend.user.entity.Maker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final FundingRepository fundingRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PersoanlMakerRepository persoanlMakerRepository;
    private final BusinessMakerRepository businessMakerRepository;

    // 프로젝트 생성
    public int save(Maker maker) {
        return fundingRepository.save(RequestProjectDTO.toSaveEntity(maker)).getId();
    }

    // 프로젝트 입력(제목, 카테고리)
    @Transactional
    public int updateIntro(RequestProjectIntroDTO projectIntroDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. main category 저장
        String mainCategoryName = projectIntroDTO.getMainCategory();
        MainCategory mainCategory = MainCategory.of(mainCategoryName);
        mainCategory = mainCategoryRepository.save(mainCategory);

       // 3. sub cateogry 저장
        String subCategoryName = projectIntroDTO.getSubCategory();
        SubCategory subCategory = SubCategory.of(mainCategory, subCategoryName);
        subCategory = subCategoryRepository.save(subCategory);

        // 4. 필요한 필드(제목, 카테고리)만 업데이트
        funding.setFundingTitle(projectIntroDTO.getFundingTitle());
        funding.setSubCategory(subCategory);

        // 5. 저장
        return fundingRepository.save(funding).getId();
    }

    // 프로젝트 입력(펀딩 시작일, 펀딩 종료일)
    @Transactional
    public int updateSchedule(RequestProjectScheduleDTO projectScheduleDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. 필요한 필드(펀딩 시작일, 펀딩 종료일)만 업데이트
        funding.setFundingStartDate(projectScheduleDTO.getFundingStartDate());
        funding.setFundingEndDate(projectScheduleDTO.getFundingEndDate());

        // 5. 저장
        return fundingRepository.save(funding).getId();
    }

    // 프로젝트 정보 입력
    @Transactional
    public int updateInfo(RequestProjectInfoDTO projectInfoDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. 필요한 필드(대표자 이름, 대표자 이메일)만 업데이트, setter 리펙토링 요구
        if(projectInfoDTO.getRepName()!=null&&!projectInfoDTO.getRepName().equals("")){
            funding.setRepName(projectInfoDTO.getRepName());
        }
        if(projectInfoDTO.getRepEmail()!=null&&!projectInfoDTO.getRepEmail().equals("")){
            funding.setRepEmail(projectInfoDTO.getRepEmail());
        }

        // 3. 메이커 유형 선택
        if (projectInfoDTO.getMakerType() != null && !projectInfoDTO.getMakerType().equals("")) {
            MakerType makerType = null;
            if (projectInfoDTO.getMakerType().equals("personal")) {
                makerType = MakerType.personal;

                PersonalMakerDTO personalMakerDTO = new PersonalMakerDTO(projectInfoDTO.getIdentityCard());

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
                funding.setPersonalMaker(personalMaker);

            } else if (projectInfoDTO.getMakerType().equals("business")) {
                makerType = MakerType.business;

                BusinessMakerDTO businessMakerDTO = new BusinessMakerDTO(
                        projectInfoDTO.getBusinessRegistNum(),
                        projectInfoDTO.getBusinessRegistCertif(),
                        projectInfoDTO.getCompanyName()
                );

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
                funding.setBusinessMaker(businessMaker);
            }
            funding.setMakerType(makerType);
        }

        // 5. 저장
        return fundingRepository.save(funding).getId();
    }
}
