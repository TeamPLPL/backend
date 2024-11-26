package com.kosa.backend.funding.project.service;

import com.kosa.backend.common.entity.Const;
import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.funding.project.dto.*;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.MainCategoryRepository;
import com.kosa.backend.funding.project.repository.SubCategoryRepository;
import com.kosa.backend.funding.support.entity.FundingNotice;
import com.kosa.backend.funding.support.repository.FollowRepository;
import com.kosa.backend.funding.support.repository.FundingNoticeRepository;
import com.kosa.backend.funding.support.repository.FundingSupportRepository;
import com.kosa.backend.funding.support.repository.WishlistRepository;
import com.kosa.backend.user.dto.FundingMakerDTO;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FundingService {
    private final S3Service s3Service;

    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final FundingRepository fundingRepository;
    private final FundingSupportRepository fundingSupportRepository;
    private final WishlistRepository wishlistRepository;
    private final FollowRepository followRepository;
    private final FundingNoticeRepository fundingNoticeRepository;

    // 메인 카테고리 리스트 조회
    public List<MainCategoryDTO> getMainCategories() {
        List<MainCategory> mainCategories = mainCategoryRepository.findAll();
        List<MainCategoryDTO> mainCategoryDTOList = new ArrayList<>();
        for(MainCategory mainCategory : mainCategories) {
            MainCategoryDTO mcDTO = MainCategoryDTO.builder()
                    .mainCategoryId(mainCategory.getId())
                    .mainCategoryName(mainCategory.getMainCategoryName())
                    .build();
            mainCategoryDTOList.add(mcDTO);
        }
        return mainCategoryDTOList;
    }

    // 메인 카테고리 id별 서브 카테고리 리스트 조회
    public List<SubCategoryDTO> getSubCategoriesById(int parentId) {
        List<SubCategory> subCategoryList = subCategoryRepository.findAllByMainCategory_Id(parentId);
        List<SubCategoryDTO> subCategoryDTOList = new ArrayList<>();
        for(SubCategory subCategory : subCategoryList) {
         SubCategoryDTO scDTO = SubCategoryDTO.builder()
                 .subCategoryId(subCategory.getId())
                 .subCategoryName(subCategory.getSubCategoryName())
                 .build();
         subCategoryDTOList.add(scDTO);
        }
        return subCategoryDTOList;
    }

    // 최신순 펀딩 리스트 조회 (8개)
    public ResponseEntity<List<FundingDTO>> getNewFundingList() {
        PageRequest pageRequest = PageRequest.of(0, Const.NEW_FUNDINGLIST_CNT, Sort.by(Sort.Direction.DESC, "publishDate"));
//        List<Funding> newFundingList = fundingRepository.findAllByOrderByPublishDateDesc(pageRequest);
        List<Funding> newFundingList = fundingRepository.findAllByIsPublishedTrueOrderByPublishDateDesc(pageRequest);
        return convertToFundingDTOList(newFundingList);
    }

    // 인기순 펀딩 리스트 조회 (5개)
    public ResponseEntity<List<FundingDTO>> getTopFundingList() {
        LocalDateTime currentDate = LocalDateTime.now();
        // 현재 펀딩 진행중인 모든 펀딩 찾기
        List<Integer> currentFundingIdList = fundingRepository.findAllCurrentFundingIds(currentDate);

        // fundingId별 supporter count 정리
        Map<Integer, Integer> supporterCntByFundingId = new HashMap<>();
        for(int fundingId : currentFundingIdList) {
            int supporterCnt = getFundingSupportUserCounts(fundingId);
            supporterCntByFundingId.put(fundingId, supporterCnt);
        }

        // 정렬 로직
        List<Map.Entry<Integer, Integer>> sortedList = new ArrayList<>(supporterCntByFundingId.entrySet());
        sortedList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        Map<Integer, Integer> sortedSupporterCntByFundingId = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : sortedList) {
            sortedSupporterCntByFundingId.put(entry.getKey(), entry.getValue());
        }

        List<Integer> topNFundingIdList = new ArrayList<>();

        // sortedSupporterCntByFundingId에서 앞에서부터 n개 항목 추출
        int count = 0;

        for (Map.Entry<Integer, Integer> entry : sortedSupporterCntByFundingId.entrySet()) {
            if (count >= Const.TOP_FUNDINGLIST_CNT) {
                break; // n개를 추출했으면 반복 중단
            }
            topNFundingIdList.add(entry.getKey());
            count++;
        }

        List<Funding> topNFundingList = new ArrayList<>();
        // 인기순 5개 리스트에 담아서 보내기
        for(int fundingId : topNFundingIdList) {
            topNFundingList.add(fundingRepository.findById(fundingId).get());
        }

        return convertToFundingDTOList(topNFundingList);
    }

    // Funding -> FundingDTO 변환 메소드
    public FundingDTO convertToFundingDTO(Funding funding) {
        String thumbnailImgUrl = null;
        try {
            thumbnailImgUrl = s3Service.getThumbnailByFundingId(funding.getId()).getSignedUrl();
        } catch(Exception e) {
            System.out.println("썸네일 없음 fundingId: " + funding.getId());
        }

        double achieveRate = CommonUtils.calculateAchievementRate(funding.getCurrentAmount(), funding.getTargetAmount());

        int supportCnt = getFundingSupportUserCounts(funding.getId());

        return FundingDTO.builder()
                .id(funding.getId())
                .fundingTitle(funding.getFundingTitle())
                .makerNick(funding.getMaker().getUser().getUserNick())
                .supportCnt(supportCnt)
                .achievementRate(achieveRate)
                .wishlistCnt(wishlistRepository.countByFunding(funding))
                .thumbnailImgUrl(thumbnailImgUrl)
                .build();
    }

    // FundingList -> FundingDTOList 변환 메소드
    public ResponseEntity<List<FundingDTO>> convertToFundingDTOList(List<Funding> fundingList) {
        if(fundingList == null || fundingList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        List<FundingDTO> fundingDTOList = new ArrayList<>();
        for(Funding funding : fundingList) {
            FundingDTO fDTO = convertToFundingDTO(funding);
            fDTO.setSupportCnt(getFundingSupportUserCounts(funding.getId()));
            fundingDTOList.add(fDTO);
        }
        return ResponseEntity.ok(fundingDTOList);
    }

    // 펀딩 참여자 수 계산 메소드
    public int getFundingSupportUserCounts(int fundingId) {
        if (fundingId < 1) {
            return Const.FAIL;
        }
        return fundingSupportRepository.countDistinctUsersByFundingId(fundingId);
    }


    public FundingDataDTO getFundingData(int userId, int fundingId) {
        Optional<Funding> optFunding = fundingRepository.findById(fundingId);
        if(optFunding.isEmpty()) { return null; }

        Funding funding = optFunding.get();

        double achievementRate = CommonUtils.calculateAchievementRate(funding.getCurrentAmount(), funding.getTargetAmount());

        int subCategoryId = funding.getSubCategory().getId();
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(() ->
                new IllegalArgumentException("서브 카테고리를 찾을 수 없습니다. ID: " + subCategoryId));

        Maker maker = funding.getMaker();
        String makerProfileImgUrl = null;
        try {
            makerProfileImgUrl = s3Service.getProfileImgByUserId(maker.getUser().getId()).getSignedUrl();
        } catch(Exception e) { e.printStackTrace(); }

        boolean isFollowing;
        if(userId < 1) { isFollowing = false; }
        else { isFollowing = followRepository.existsByFollowedUserIdAndFollowingUserId(maker.getId(), userId); }

        FundingMakerDTO fmDTO = FundingMakerDTO.builder()
                .makerId(maker.getId())
                .userId(maker.getUser().getId())
                .userContent(maker.getUserContent())
                .userNick(maker.getUser().getUserNick())
                .profileImgUrl(makerProfileImgUrl)
                .isFollowing(isFollowing)
                .makerEmail(maker.getUser().getEmail())
                .build();

        boolean isWishlist;
        if(userId < 1) { isWishlist = false; }
        else { isWishlist = wishlistRepository.existsByUserIdAndFundingId(userId, funding.getId()); }

        FundingDataDTO fdDTO = FundingDataDTO.builder()
                .fundingId(funding.getId())
                .fundingTitle(funding.getFundingTitle())
                .currentAmount(funding.getCurrentAmount())
                .targetAmount(funding.getTargetAmount())
                .achievementRate(achievementRate)
                .supportCnt(getFundingSupportUserCounts(funding.getId()))
                .fundingTag(funding.getFundingTag())
                .fundingExplanation(funding.getFundingExplanation())
                .fundingStartDate(funding.getFundingStartDate())
                .fundingEndDate(funding.getFundingEndDate())
                .mainCategoryId(subCategory.getMainCategory().getId())
                .subCategoryId(subCategoryId)
                .mainCategoryNm(subCategory.getMainCategory().getMainCategoryName())
                .subCategoryNm(subCategory.getSubCategoryName())
                .isWishlist(isWishlist)
                .makerDTO(fmDTO)
                .build();

        return fdDTO;
    }

    // 펀딩id별 펀딩 디테일 페이지의 썸네일 및 펀딩 디테일 이미지 리스트 담은 FundingImgListDTO 반환 메소드
    public FundingImgListDTO getFundingImgList(int fundingId) {
        String thumbnailUrl = null;
        try {
            thumbnailUrl = s3Service.getThumbnailByFundingId(fundingId).getSignedUrl();
        } catch(Exception e) { e.printStackTrace(); }
        System.out.println("thumbnail: " + thumbnailUrl);
        String detailImgUrl = null;
        try {
            detailImgUrl = s3Service.getDetailImgByFundingId(fundingId).getSignedUrl();
        } catch(Exception e) { e.printStackTrace();}

        return FundingImgListDTO.builder()
                .thumbnailImgUrl(thumbnailUrl)
                .detailImgUrl(detailImgUrl)
                .build();
    }

//    public FundingImgListDTO getFundingImgList(int fundingId) {
//        String thumbnailUrl = null;
//        try {
//            thumbnailUrl = s3Service.getThumbnailByFundingId(fundingId).getSignedUrl();
//        } catch(Exception e) { e.printStackTrace(); }
//        System.out.println("thumbnail: " + thumbnailUrl);
//        FileDTO detailFileDTO = s3Service.getFilesById()
////        List<FileDTO> detailFileDTOList = s3Service.getDetailImgListByFundingId(fundingId);
////        List<String> detailImgUrlList = new ArrayList<>();
////        for(FileDTO fileDTO : detailFileDTOList) {
////            detailImgUrlList.add(fileDTO.getSignedUrl());
////        }
//        return FundingImgListDTO.builder()
//                .thumbnailImgUrl(thumbnailUrl)
//                .detailImgUrlList(detailImgUrlList)
//                .build();
//    }

    // mainCategoryId별 FundingDTO 리스트 반환 메소드
    public List<FundingDTO> getFundingDTOPageByMainCategoryId(int mainCategoryId) {
        // 1. MainCategory에 해당하는 SubCategory ID 리스트 가져오기
        List<SubCategory> subCategoryList = subCategoryRepository.findAllByMainCategory_Id(mainCategoryId);
        List<Integer> subCategoryIdList = subCategoryList.stream()
                .map(SubCategory::getId)
                .toList();

        // 2. SubCategory ID 리스트로 Funding 엔티티 페이징 조회
//        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
//        Page<Funding> fundingPage = fundingRepository.findAllBySubCategory_IdIn(subCategoryIdList, pageableWithoutSort);
        List<Funding> fundingPage = fundingRepository.findAllBySubCategory_IdIn(subCategoryIdList);

        // 3. Funding 엔티티를 FundingDTO로 변환 및 supportCnt 설정
        List<FundingDTO> fundingDTOs = fundingPage.stream()
                .map(this::convertToFundingDTO)
                .peek(dto -> dto.setSupportCnt(getFundingSupportUserCounts(dto.getId())))
                .collect(Collectors.toList());

        // 4. supportCnt 기준으로 정렬
        fundingDTOs.sort((a, b) -> Integer.compare(b.getSupportCnt(), a.getSupportCnt()));

        // 5. 정렬된 리스트를 Page로 변환
        return fundingDTOs;
    }

    // subCategoryId별 FundingDTO 리스트 반환 메소드
    public List<FundingDTO> getFundingDTOPageBySubCategoryId(int subCategoryId) {
        // 1. 정렬 없는 Pageable 생성 (supportCnt 정렬은 메모리에서 처리)
//        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        // 2. SubCategory ID로 Funding 엔티티 페이징 조회
        List<Funding> fundingPage = fundingRepository.findAllBySubCategory_Id(subCategoryId);

        // 3. Funding 엔티티를 FundingDTO로 변환 및 supportCnt 설정
        List<FundingDTO> fundingDTOs = fundingPage.stream()
                .map(this::convertToFundingDTO)
                .peek(dto -> dto.setSupportCnt(getFundingSupportUserCounts(dto.getId())))
                .collect(Collectors.toList());

        // 4. supportCnt 기준으로 정렬
        fundingDTOs.sort((a, b) -> Integer.compare(b.getSupportCnt(), a.getSupportCnt()));

        // 5. 정렬된 리스트를 Page로 변환
        return fundingDTOs;
    }

    // title String값을 포함한 FundingTitle값을 가지고 있는 펀딩 프로젝트 반환 메소드
    public List<FundingDTO> searchByTitle(String title) {
//        List<Funding> resultFunding = fundingRepository.findByFundingTitleContainingAndIsPublishedTrueOrderBySupportCount(title);
        List<Funding> resultFunding = fundingRepository.findByFundingTitleContainingAndIsPublishedTrue(title);
        List<FundingDTO> fDTOList = new ArrayList<>();
        for(Funding f: resultFunding) {
//            System.out.println("펀딩제목: "+f.getFundingTitle());
            fDTOList.add(convertToFundingDTO(f));
        }
        return fDTOList;


//        return resultPage.map(objects -> {
//            Funding funding = (Funding) objects[0];
//            Long supportCount = (Long) objects[1];
//
//            FundingDTO dto = convertToFundingDTO(funding);
//            dto.setSupportCnt(supportCount.intValue());
//            return dto;
//        });
    }

    public Page<FundingDTO> getFundingsOrderBySupporterCount(Pageable pageable) {
        LocalDateTime currentDate = LocalDateTime.now();
        Page<Object[]> fundingPage = fundingRepository.findAllPublishedWithSupporterCount(pageable, currentDate);

        return fundingPage.map(objects -> {
            Funding funding = (Funding) objects[0];
            Integer supportCount = (Integer) objects[1];

            FundingDTO dto = convertToFundingDTO(funding);
            dto.setSupportCnt(supportCount);
            return dto;
        });
    }

    // FundingList를 FundingDTO로 변환하고 참여자 수 계산하여 참여자 수를 기준으로 내림차순 정렬
//    private List<FundingDTO> getFundingDTOListOrderBySupportCnt(List<Funding> fundingList) {
//
//        return fundingList.stream()
//                .map(funding -> {
//                    FundingDTO fDto = convertToFundingDTO(funding);
//                    int supporterCnt = getFundingSupportUserCounts(funding.getId());
//                    fDto.setSupportCnt(supporterCnt);
//                    return fDto;
//                }).sorted((a, b) -> Integer.compare(b.getSupportCnt(), a.getSupportCnt())).collect(Collectors.toList());
//    }

    // 펀딩ID별 공지사항 페이지네이션 리스트 반환
//    public Page<FundingNoticeDTO> getFundingNoticeDTOList(int fundingId, Pageable pageable) {
//        Page<FundingNotice> noticePage = fundingNoticeRepository.findByFundingIdOrderByUpdatedAtDesc(fundingId, pageable);
//        return noticePage.map(this::convertToFundingNoticeDTO);
//    }
    public List<FundingNoticeDTO> getFundingNoticeDTOList(int fundingId) {
        List<FundingNotice> noticeList = fundingNoticeRepository.findByFundingIdOrderByUpdatedAtDesc(fundingId);
        return noticeList.stream()
                .map(this::convertToFundingNoticeDTO)
                .collect(Collectors.toList());
    }

    private FundingNoticeDTO convertToFundingNoticeDTO(FundingNotice notice) {
        return FundingNoticeDTO.builder()
                .noticeId(notice.getId())
                .noticeCategoryId(Const.SUCCESS)
                .noticeTitle(notice.getNoticeTitle())
                .noticeDate(notice.getNoticeDate())
                .noticeContent(notice.getNoticeContent())
                .fundingId(notice.getFunding().getId())
                .updateDate(notice.getUpdatedAt())
                .build();
    }

    // 펀딩ID에 따른 펀딩 변수 반환
    public Funding getFundingById(int fundingId) {
        return fundingRepository.findById(fundingId).orElse(null);
    }

    public void saveNewFundingNotice(Funding funding, FundingNoticeDTO fnDTO) {
        LocalDateTime now = LocalDateTime.now();

        FundingNotice fn = FundingNotice.builder()
                .funding(funding)
                .noticeCategoryId(Const.SUCCESS)
                .noticeTitle(fnDTO.getNoticeTitle())
                .noticeContent(fnDTO.getNoticeContent())
                .noticeDate(now)
                .build();

        fundingNoticeRepository.save(fn);
    }

    public FundingNoticeDTO getFundingnoticeDTO(int noticeId) {
        FundingNotice fn = fundingNoticeRepository.findById(noticeId).orElse(null);
        if(fn == null) { return null; }
        return convertToFundingNoticeDTO(fn);
    }

    public void updateFundingNotice(FundingNoticeDTO fnDTO) {
        FundingNotice fn = fundingNoticeRepository.findById(fnDTO.getNoticeId()).orElseThrow();
        fn.setNoticeTitle(fnDTO.getNoticeTitle());
        fn.setNoticeContent(fnDTO.getNoticeContent());
        fundingNoticeRepository.save(fn);
    }

    public void deleteFundingNotice(int noticeId) {
        fundingNoticeRepository.deleteById(noticeId);
    }
}
