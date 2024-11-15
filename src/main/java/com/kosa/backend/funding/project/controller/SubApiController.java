package com.kosa.backend.funding.project.controller;

import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.funding.project.dto.MainCategoryDTO;
import com.kosa.backend.funding.project.dto.SubCategoryDTO;
import com.kosa.backend.funding.project.service.CategoryService;
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
    private final S3Service s3Service;

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
    public ResponseEntity<?> uploadUserProfileImg(@AuthenticationPrincipal CustomUserDetails cud,
                                                  @PathVariable(name = "projectId") int projectId,
                                                  @RequestParam("file") MultipartFile file) throws IOException {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        s3Service.uploadImgFile(user, file, ImgType.THUMBNAIL, projectId);

        String thumbnail =  s3Service.getThumbnailByFundingId(projectId);

        return ResponseEntity.ok(thumbnail);
    }
}
