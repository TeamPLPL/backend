package com.kosa.backend.common.controller;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.service.S3Service;
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

    @PutMapping("/{funding-id}/detail-img")
    public ResponseEntity<String> updateFundingDetailImgList(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable("funding-id") int fundingId, List<MultipartFile> filesList) {
        // 인증된 User 체크 메소드 따로 빼기
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        for(MultipartFile f : filesList) {
            String fileName = f.getOriginalFilename();
            // 파일이름으로 조회
            Files file = s3Service.getFilesByOriginalNm(fileName);
            if(file == null) {
                // 새 파일 업로드
                s3Service.uploadImgFile(user, f, ImgType.DETAIL_IMAGE, fundingId);
            } else {
                s3Service.updateFilesSequence(file);
            }
        }
        return ResponseEntity.ok().build();
    }

    //    @PostMapping
//    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
//        try {
//            System.out.println(file.getOriginalFilename());
//            String imageUrl = s3Service.uploadFile(file);
//            System.out.println(imageUrl);
//            return ResponseEntity.ok(imageUrl);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
//        }
//    }
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
}