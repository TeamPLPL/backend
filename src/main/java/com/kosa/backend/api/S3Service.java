package com.kosa.backend.api;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.repository.FilesRepository;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final FundingRepository fundingRepository;
    private final FilesRepository filesRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

//    @Transactional
//    public String uploadFile(MultipartFile file, String path, ImgType imgType, int fundingId, int userId) throws IOException {
//
//        String fileName = generateFileName(file);
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName) // key 매개변수로 fullpath 필요
//                .build();
//
//        // db에 저장
//        saveFile(file, path, imgType, fundingId, userId, fileName);
//
//        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//        String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toString();
//        return fileUrl;
//    }
//
//    @Transactional
//    public void saveFile(MultipartFile file, String path, ImgType imgType, int fundingId, int userId, String fileName) {
//        Funding funding = fundingRepository.findById(fundingId)
//                .orElseThrow(() -> new RuntimeException("Funding not found"));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Files newFile = Files.builder()
//                .path(path)
//                .originalNm(file.getOriginalFilename())
//                .savedNm(fileName)
//                .imgType(imgType)
//                .funding(funding)
//                .user(user)
//                .build();
//
//        filesRepository.save(newFile);
//    }

    public InputStreamResource getThumbnailByFundingId(int fundingId) {
        Optional<Files> file = filesRepository.findByFundingIdAndImgType(fundingId, ImgType.THUMBNAIL);

        return downloadImgResource(file);
    }

    public InputStreamResource getProfileImgByUserId(int userId) {
        Optional<Files> file = filesRepository.findByUserIdAndImgType(userId, ImgType.PROFILE_IMAGE);

        return downloadImgResource(file);
    }

    public InputStreamResource downloadImgResource(Optional<Files> file) {
        if(file.isEmpty()) { return null; }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(file.get().getSavedNm())
                .build();

        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(object);
    }

    public InputStreamResource downloadFile(String savedNm) throws IOException {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(savedNm)
                .build();

        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(object);
    }

//    public String updateFile(String oldFileName, MultipartFile newFile) throws IOException {
//        // 기존 파일 삭제
//        deleteFile(oldFileName);
//
//        // 새 파일 업로드
//        return uploadFile(newFile);
//    }

    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }

////////////////////////////

    // 이미지파일 업로드 시 호출하는 메소드
    // User는 불러올 곳에서 @AuthenticationPrincipal User user를 매개변수 맨 앞에 추가해주면 됨
    // 썸네일, 펀딩디테일이미지는 fundingId 자리에 fundingId

    @Transactional
    public ResponseEntity<String> uploadImgFile(User user, MultipartFile file, ImgType imgType, int... fundingId) throws IOException {
        if(user == null) {
            System.out.println("user is null");
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        String fileName = generateFileName(file);
        int fId = -1;

        String path = switch (imgType) {
            case PROFILE_IMAGE -> String.format("profile-img/%d/", user.getId());   // /profile-img/{user-id}/
            case THUMBNAIL -> {
                fId = fundingId[0];
                yield String.format("%d/thumbnail/", fId);   // {fundingId}/thumbnail/
            }
            case DETAIL_IMAGE -> {
                fId = fundingId[0];
                int count = filesRepository.countAllByFundingIdAndImgType(fId, imgType); // {funding-id}/detail-img/{++count}/
                yield String.format("%d/detail-img/%d", fId, ++count);
            }
            default -> null;
        };

        String fullPath = path + fileName;
        String fileUrl;

        Files newFile = Files.builder()
                .path(path)
                .originalNm(file.getOriginalFilename())
                .savedNm(fileName)
                .imgType(imgType)
                .user(user)
                .build();

        switch(imgType) {
            case PROFILE_IMAGE:
                break;
            case THUMBNAIL, DETAIL_IMAGE:
                Optional<Funding> funding = fundingRepository.findById(fId);
                if(funding.isEmpty()) { return ResponseEntity.notFound().build(); }
                newFile.setFunding(funding.get());
                break;
            default:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }

        // db에 파일 저장
        filesRepository.save(newFile);

        fileUrl = uploadS3ImgFile(file, fullPath);

        return ResponseEntity.ok(fileUrl);
    }

    // bucket에 파일 업로드
    public String uploadS3ImgFile(MultipartFile file, String fullPath) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullPath)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fullPath)).toString();
        return fileUrl;
    }

}
