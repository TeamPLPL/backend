package com.kosa.backend.common.controller;

import com.kosa.backend.common.dto.FileDTO;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import com.kosa.backend.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final UserService userService;
    private final S3Service s3Service;

    @PostMapping("/userProfile")
    public ResponseEntity<String> uploadUserProfileImg(@AuthenticationPrincipal CustomUserDetails cud, @RequestPart("file") MultipartFile file) throws IOException {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return s3Service.uploadImgFile(user, file, ImgType.PROFILE_IMAGE);
    }

    // 디테일 이미지 리스트 업데이트
//    @PutMapping("/{funding-id}/detail-img")
//    public ResponseEntity<String> updateFundingDetailImgList(
//            @AuthenticationPrincipal CustomUserDetails cud,
//            @PathVariable("funding-id") int fundingId,
//            @RequestPart("files") List<MultipartFile> files,
//            @RequestPart("fileIds") List<Integer> fileIds) {
//
//        String userEmail = cud.getUsername();
//        User user = userService.getUser(userEmail);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        try {
//            s3Service.updateImages(user, fundingId, files, fileIds, ImgType.DETAIL_IMAGE);
//            return ResponseEntity.ok("Images updated successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating images: " + e.getMessage());
//        }
//    }


    @DeleteMapping("/delete/{file-id}")
    public ResponseEntity<String> deleteFundingDetailImg(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable("file-id") int fileId) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        try {
            s3Service.deleteImgFile(user, fileId);
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating images: " + e.getMessage());
        }
    }

//    @PutMapping("/{funding-id}/detail-img")
//    public ResponseEntity<String> updateFundingDetailImgList(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable("funding-id") int fundingId, List<Integer> filesIdList) {
//        // 인증된 User 체크 메소드 따로 빼기
//        String userEmail = cud.getUsername();
//        User user = userService.getUser(userEmail);
//        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        for(int fileId : filesIdList) {
//            // 파일이름으로 조회
//            Files file = s3Service.getFilesById(fileId);
//            if(file == null) {
//                // 새 파일 업로드
//                s3Service.uploadImgFile(user, , ImgType.DETAIL_IMAGE, fundingId);
//            } else {
//                s3Service.updateFilesSequence(file);
//            }
//        }
//        return ResponseEntity.ok().build();
//    }

        @PostMapping("/test")
    public ResponseEntity<String> uploadImage(@AuthenticationPrincipal CustomUserDetails cud, @RequestParam("file") MultipartFile file) {
        try {
//            System.out.println(file.getOriginalFilename());
            User user = CommonUtils.getCurrentUser(cud, userService);
            return s3Service.uploadImgFile(user, file, ImgType.DETAIL_IMAGE, 1);
//            System.out.println(imageUrl);
//            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
//
//    @GetMapping("/{imageName}")
//    public ResponseEntity<InputStreamResource> getImage(@PathVariable("imageName") String imageName) {
//        try {
//            InputStreamResource resource = s3Service.downloadFile(imageName);
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_JPEG) // 또는 적절한 이미지 타입
//                    .body(resource);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @PostMapping("/detail/list/{id}")
    public ResponseEntity<List<FileDTO>> getDetailImgList(@PathVariable("id") int fundingId) {
        List<FileDTO> fileDTOList = s3Service.getDetailImgListByFundingId(fundingId);

        return ResponseEntity.ok(fileDTOList);
    }
}