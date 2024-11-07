package com.kosa.backend.common.service;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.repository.FilesRepository;
import com.kosa.backend.common.repository.FilesSequenceRepository;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FundingRepository fundingRepository;
    private final FilesRepository filesRepository;
    private final FilesSequenceRepository filesSequenceRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    // 이미지파일 업로드 메소드
    // 썸네일, 펀딩프로젝트상세이미지는 fundingId 자리에 fundingId값 넣기
    @Transactional
    public ResponseEntity<String> uploadImgFile(User user, MultipartFile file, ImgType imgType, int... fundingId) {
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
                yield String.format("%d/detail-img", fId); // /{fundingId}/detail-img/
            }
            case PROMOTION_IMAGE -> "promotion-img/"; // /promotion-img/
        };

        String fullPath = path + fileName;

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
            { return ResponseEntity.notFound().build(); }
        }

        // db에 파일 저장
        filesRepository.save(newFile);

        Files savedFile = filesRepository.findBySavedNm(fileName).orElseThrow(RuntimeException::new);

        // db에 파일 sequence 저장
        filesSequenceRepository.save(savedFile);

        // 파일 업로드 후 서명된 URL 반환
        try {
            uploadS3ImgFile(file, fullPath);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }

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
        s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fullPath));
    }

    //////////////////
    // 편딩ID별 펀딩디테일이미지리스트 조회 메소드
    public List<String> getDetailImgListByFundingId(int fundingId) {
        List<Files> detailImgList = filesRepository.findAllByFundingIdAndImgType(fundingId, ImgType.DETAIL_IMAGE);
        List<String> signedUrlList = new ArrayList<>();
        for (Files file : detailImgList) {
            String fullPath = file.getPath() + file.getSavedNm();
            signedUrlList.add(generateSignedUrl(fullPath));
        }
        return signedUrlList;
    }
    ///////////////////

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

    // 이미지 삭제 메소드
    @Transactional
    public void deleteImgFile(User user, Files file) {
        if(user == null) {
            System.out.println("user is null");
            return;
        } else if(file == null) {
            System.out.println("file is null");
            return;
        }

        // DB 삭제
        int filesId = file.getId();
        filesSequenceRepository.deleteByFilesId(filesId);
        filesRepository.deleteById(filesId);

        String fullPath = file.getPath() + file.getSavedNm();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fullPath)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

    }

    // 원래파일명으로 파일 조회
    public Files getFilesByOriginalNm(String fileName) {
        return filesRepository.findByOriginalNm(fileName).orElse(null);
    }

    // 파일시퀀스 업데이트
    public void updateFilesSequence(Files file) {
        // filesSequence에서 지우고 다시 sequence 생성
        filesSequenceRepository.deleteByFilesId(file.getId());
        filesSequenceRepository.save(file);
    }

    ////////////////////

    // S3 bucket에서 이미지 리소스 가져오는 메소드
//    public InputStreamResource downloadImgResource(Optional<Files> file) {
//        if(file.isEmpty()) { return null; }
//
//        String fullPath = file.get().getPath() + file.get().getSavedNm();
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fullPath)
//                .build();
//
//        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
//        return new InputStreamResource(object);
//    }
    // 이미지 변경(삭제 후 생성) 메소드
//    @Transactional
//    public ResponseEntity<String> updateImgFile(User user, Files oldFile, MultipartFile newFile) throws IOException {
//        // 기존 파일 삭제
//        deleteImgFile(user, oldFile);
//
//        // 새 파일 업로드
//        if(oldFile.getImgType() == ImgType.PROFILE_IMAGE) {
//            return uploadImgFile(user, newFile, oldFile.getImgType());
//        }
//        return uploadImgFile(user, newFile, oldFile.getImgType(), oldFile.getFunding().getId());
//    }
}
