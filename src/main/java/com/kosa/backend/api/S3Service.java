package com.kosa.backend.api;

import com.kosa.backend.common.entity.Const;
import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.repository.FilesRepository;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.user.entity.User;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FundingRepository fundingRepository;
    private final FilesRepository filesRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    // 이미지파일 업로드 메소드
    // 썸네일, 펀딩프로젝트상세이미지는 fundingId 자리에 fundingId값 넣기
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
        System.out.println(fullPath);

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

        // 파일 업로드 후 서명된 URL 반환
        uploadS3ImgFile(file, fullPath);
        String signedUrl = generateSignedUrl(fullPath);

        return ResponseEntity.ok(signedUrl);
    }

    // 서명된 URL 생성 메소드
    public String generateSignedUrl(String objectKey) {
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(builder -> builder.bucket(bucketName).key(objectKey))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    // 파일 이름 생성 메소드
    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }

    // bucket에 파일 업로드
    public void uploadS3ImgFile(MultipartFile file, String fullPath) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullPath)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fullPath)).toString();
    }

    // 편딩ID별 펀딩디테일이미지리스트 조회 메소드
    public List<String> getDetailImgListByFundingId(int fundingId) {
        List<Files> detailImgList = filesRepository.findAllByFundingIdAndImgType(fundingId, ImgType.DETAIL_IMAGE);
        List<String> signedUrls = new ArrayList<>();
        for (Files file : detailImgList) {
            String fullPath = file.getPath() + file.getSavedNm();
            signedUrls.add(generateSignedUrl(fullPath));
        }
        return signedUrls;
    }

    // 펀딩ID별 썸네일 조회 메소드
    public String getThumbnailByFundingId(int fundingId) {
        Optional<Files> file = filesRepository.findByFundingIdAndImgType(fundingId, ImgType.THUMBNAIL);
        return file.map(f -> generateSignedUrl(f.getPath() + f.getSavedNm())).orElse(null);
    }

    // 사용자ID별 프로필이미지 조회 메소드
    public String getProfileImgByUserId(int userId) {
        Optional<Files> file = filesRepository.findByUserIdAndImgType(userId, ImgType.PROFILE_IMAGE);
        return file.map(f -> generateSignedUrl(f.getPath() + f.getSavedNm())).orElse(null);
    }

    // S3 bucket에서 이미지 가져오는 메소드
    public InputStreamResource downloadImgResource(Optional<Files> file) {
        if(file.isEmpty()) { return null; }

        String fullPath = file.get().getPath() + file.get().getSavedNm();
        System.out.println(fullPath);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fullPath)
                .build();

        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(object);
    }

    // 이미지 삭제 메소드
    @Transactional
    public int deleteImgFile(User user, Files file) throws IOException {
        if(user == null) {
            System.out.println("user is null");
            return Const.FAIL;
        } else if(file == null) {
            System.out.println("file is null");
            return Const.FAIL;
        }

        // DB 삭제
        filesRepository.deleteById(file.getId());

        String fullPath = file.getPath() + file.getSavedNm();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fullPath)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        return Const.SUCCESS;
    }

    // 이미지 변경(삭제 후 생성) 메소드
    @Transactional
    public ResponseEntity<String> updateImgFile(User user, Files oldFile, MultipartFile newFile) throws IOException {
        // 기존 파일 삭제
        int deleteResult = deleteImgFile(user, oldFile);

        if(deleteResult == Const.FAIL) { return ResponseEntity.status(INTERNAL_SERVER_ERROR).build(); }

        // 새 파일 업로드
        if(oldFile.getImgType() == ImgType.PROFILE_IMAGE) {
            return uploadImgFile(user, newFile, oldFile.getImgType());
        }
        return uploadImgFile(user, newFile, oldFile.getImgType(), oldFile.getFunding().getId());
    }




////////////////////////////

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


//    public InputStreamResource downloadFile(String savedNm) throws IOException {
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(savedNm)
//                .build();
//
//        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
//        return new InputStreamResource(object);
//    }

//    public void deleteFile(String fileName) {
//        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .build();
//
//        s3Client.deleteObject(deleteObjectRequest);
//    }

//    private static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
//    private static boolean isImageFile(String fileName) {
//        for (String extension : IMAGE_EXTENSIONS) {
//            if (fileName.endsWith(extension)) {
//                return true;
//            }
//        }
//        return false;
//    }


}
