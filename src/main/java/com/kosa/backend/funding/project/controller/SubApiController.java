package com.kosa.backend.funding.project.controller;

import com.kosa.backend.common.dto.FileDTO;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.service.S3CustomService;
import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.funding.project.dto.MainCategoryDTO;
import com.kosa.backend.funding.project.dto.RewardDTO;
import com.kosa.backend.funding.project.dto.RewardInfoDTO;
import com.kosa.backend.funding.project.dto.SubCategoryDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestIsPublishedDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectInfoDTO;
import com.kosa.backend.funding.project.dto.responsedto.ResponseProjectDTO;
import com.kosa.backend.funding.project.service.CategoryService;
import com.kosa.backend.funding.project.service.ProjectService;
import com.kosa.backend.funding.project.service.RewardService;
import com.kosa.backend.funding.project.service.RewardSubService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SubApiController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final RewardSubService rewardService;
    private final S3Service s3Service;
    private final S3CustomService s3CustomService;
    private final ProjectService projectService;

    @GetMapping("/maincategory")
    public ResponseEntity<?> getMainCategory() {
        List<MainCategoryDTO> mainCategoryDTOList = categoryService.findMainCategoryAll();

        Map<String, Object> response = new HashMap<>();
        response.put("mainCategoryList", mainCategoryDTOList);

        return ResponseEntity.ok(mainCategoryDTOList);
    }

    @GetMapping("/subcategory")
    public ResponseEntity<?> getSubCategory() {
        List<SubCategoryDTO> subCategoryDTOList = categoryService.findSubCategoryAll();

        Map<String, Object> response = new HashMap<>();
        response.put("subCategoryList", subCategoryDTOList);

        return ResponseEntity.ok(subCategoryDTOList);
    }

    @PostMapping("/{projectId}/thumbnail")
    public ResponseEntity<?> uploadThubnail(@AuthenticationPrincipal CustomUserDetails cud,
                                                  @PathVariable(name = "projectId") int projectId,
                                                  @RequestParam("file") MultipartFile file) throws IOException {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        s3Service.uploadImgFile(user, file, ImgType.THUMBNAIL, projectId);

        FileDTO thumbnail =  s3Service.getThumbnailByFundingId(projectId);

        return ResponseEntity.ok(thumbnail);
    }

    @GetMapping("/{projectId}/thumbnail")
    public ResponseEntity<?> getThubnail(@AuthenticationPrincipal CustomUserDetails cud,
                                         @PathVariable(name = "projectId") int projectId) throws IOException {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        FileDTO thumbnail =  s3CustomService.getThumbnailByFundingId(projectId);

        return ResponseEntity.ok(thumbnail);
    }

    // 상세 이미지 입력 컨트롤러
    @PostMapping("/{projectId}/detailiamge")
    public ResponseEntity<?> uploadImages(@AuthenticationPrincipal CustomUserDetails cud,
                                                  @PathVariable(name = "projectId") int projectId,
                                                  @RequestParam("file") MultipartFile file) throws IOException {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        s3Service.uploadImgFile(user, file, ImgType.DETAIL_IMAGE, projectId);

        FileDTO detailiamge =  s3CustomService.getDetailByFundingId(projectId);

        return ResponseEntity.ok(detailiamge);
    }

    // 상세 이미지 출력 컨트롤러
    @GetMapping("/{projectId}/detailiamge")
    public ResponseEntity<?> getDetailImage(@AuthenticationPrincipal CustomUserDetails cud,
                                            @PathVariable(name = "projectId") int projectId) throws IOException {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        FileDTO detailiamge =  s3CustomService.getDetailByFundingId(projectId);

        return ResponseEntity.ok(detailiamge);
    }

    // 이미지 삭제 컨트롤러
    @GetMapping("/{fileId}/deleteimage")
    public ResponseEntity<?> deleteImage(@AuthenticationPrincipal CustomUserDetails cud,
                                         @PathVariable(name = "fileId") int fileId) {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        s3Service.deleteImgFile(user, fileId);

        return ResponseEntity.ok().build();
    }

    // 리워드 리스트 가져오는 컨트롤러
    @GetMapping("/{projectId}/getrewards")
    public ResponseEntity<?> getRewardsList(@AuthenticationPrincipal CustomUserDetails cud,
                                            @PathVariable(name = "projectId") int projectId) {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<RewardDTO> rewardDTOList = rewardService.findAll(projectId);

        return ResponseEntity.ok(rewardDTOList);
    }

    // 리워드 정책 가져오는 컨트롤러
    @GetMapping("/{projectId}/getpolicy")
    public ResponseEntity<?> getPolicy(@AuthenticationPrincipal CustomUserDetails cud,
                                       @PathVariable(name = "projectId") int projectId) {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RewardInfoDTO rewardInfoDTO = rewardService.findRewardInfo(projectId);

        return ResponseEntity.ok(rewardInfoDTO);
    }

    @GetMapping("/{rewardId}/deletereward")
    public ResponseEntity<String> deleteReward(@AuthenticationPrincipal CustomUserDetails cud,
                                               @PathVariable(name = "rewardId") int rewardId) {
        try {
            // 사용자 인증 확인
            String userEmail = cud.getUsername();
            User user = userService.getUser(userEmail);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
            }

            // 리워드 삭제 로직
            boolean isDeleted = rewardService.delete(rewardId);
            if (!isDeleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not_found");
            }

            // 성공적으로 삭제
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            // 예외 발생 시 내부 서버 오류 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

    @GetMapping("/{projectId}/iscomplete")
    public ResponseEntity<?> isComplete(@AuthenticationPrincipal CustomUserDetails customUser,
                                        @PathVariable(name = "projectId") int projectId) {
        ResponseProjectDTO responseProjectDTO = projectService.getProject(projectId);

        // 상태 값을 Map으로 정리
        Map<String, Boolean> completionStatus = Map.of(
                "isIntroCompleted", responseProjectDTO.isIntroCompleted(),
                "isScheduleComplete", responseProjectDTO.isScheduleComplete(),
                "isInfoComplete", responseProjectDTO.isInfoComplete(),
                "isRewardComplete", responseProjectDTO.isRewardComplete(),
                "isRewardInfoComplete", responseProjectDTO.isRewardInfoComplete(),
                "isComplete", responseProjectDTO.isIntroCompleted() && responseProjectDTO.isScheduleComplete()
                        && responseProjectDTO.isInfoComplete() && responseProjectDTO.isRewardComplete()
                        && responseProjectDTO.isRewardInfoComplete()
        );
        return ResponseEntity.ok(completionStatus);
    }

    @PostMapping("/{projectId}/ispublished")
    public ResponseEntity<?> isPublish(@AuthenticationPrincipal CustomUserDetails customUser,
                                       @RequestBody RequestIsPublishedDTO  isPublishedDTO,
                                       @PathVariable(name = "projectId") int projectId) {
        ResponseProjectDTO responseProjectDTO = projectService.getProject(projectId);


    }
}
