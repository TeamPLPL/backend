package com.kosa.backend.api;

import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final S3Service s3Service;
    private final UserService userService;

//    private final S3Service s3Service;
//
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

    @PostMapping("/userProfile")
    public ResponseEntity<String> uploadUserProfileImg(@AuthenticationPrincipal CustomUserDetails cud, @RequestPart("file") MultipartFile file) throws IOException {
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return s3Service.uploadImgFile(user, file, ImgType.PROFILE_IMAGE);
    }
}