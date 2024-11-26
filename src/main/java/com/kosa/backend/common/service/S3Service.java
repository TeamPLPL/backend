package com.kosa.backend.common.service;

import com.kosa.backend.common.dto.FileDTO;
import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.repository.FilesRepository;
import com.kosa.backend.common.repository.FilesSequenceRepository;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.*;

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
    public List<FileDTO> getDetailImgListByFundingId(int fundingId) {
        List<Files> detailImgList = filesRepository.findAllByFundingIdAndImgTypeOrderBySequence(fundingId, ImgType.DETAIL_IMAGE);

        if (detailImgList == null || detailImgList.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환
        }

        List<FileDTO> signedUrlList = new ArrayList<>();
        for (Files file : detailImgList) {
            String fullPath = file.getPath() + file.getSavedNm();
            FileDTO fDto = FileDTO.builder()
                    .fileId(file.getId())
                    .signedUrl(generateSignedUrl(fullPath))
                    .build();
            signedUrlList.add(fDto);
        }
        return signedUrlList;
    }
    ///////////////////


    // 펀딩ID별 이미지 1장 조회
    public FileDTO getImgByFundingId(int fundingId, ImgType imgType) {
        Optional<Files> file = filesRepository.findByFundingIdAndImgType(fundingId, imgType);
        if(file.isEmpty()) { return FileDTO.builder().build(); }
        String signedUrl = generateSignedUrl(file.get().getPath() + file.get().getSavedNm());
        return FileDTO.builder()
                .fileId(file.get().getId())
                .signedUrl(signedUrl)
                .build();
    }

    // 펀딩ID별 디테일이미지(1장) 조회 메소드
    public FileDTO getDetailImgByFundingId(int fundingId) {
        return getImgByFundingId(fundingId, ImgType.DETAIL_IMAGE);
    }

    // 펀딩ID별 썸네일 조회 메소드
    public FileDTO getThumbnailByFundingId(int fundingId) {
        return getImgByFundingId(fundingId, ImgType.THUMBNAIL);
    }

    // 사용자ID별 프로필이미지 조회 메소드
    public FileDTO getProfileImgByUserId(int userId) {
        return getImgByFundingId(userId, ImgType.PROFILE_IMAGE);
    }

    // 이미지 삭제 메소드
    @Transactional
    public void deleteImgFile(User user, int fileId) {
        if(user == null) {
            throw new RuntimeException("user is null");
        }
        Files file = filesRepository.findById(fileId).orElse(null);
        if(file == null) {
            throw new RuntimeException("file is null");
        }

        // DB 삭제
        filesSequenceRepository.deleteByFilesId(fileId);
        filesRepository.deleteById(fileId);

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

    // 파일id로 파일 조회
    public Files getFilesById(int fileId) {
        return filesRepository.findById(fileId).orElse(null);
    }

    // 파일시퀀스 업데이트
    public void updateFilesSequence(Files file) {
        // filesSequence에서 지우고 다시 sequence 생성
        filesSequenceRepository.deleteByFilesId(file.getId());
        filesSequenceRepository.save(file);
    }


//    // 이미지 리스트 수정
//    @Transactional
//    public void updateImages(User user, int fundingId, List<MultipartFile> files, List<Integer> fileIds, ImgType imgType) throws IOException {
//        // 기존 이미지 삭제
//        List<Files> existingFiles = filesRepository.findAllByFundingIdAndImgType(fundingId, imgType);
//        for (Files file : existingFiles) {
//            if (!fileIds.contains(file.getId())) {
//                deleteImgFile(user, file);
//            }
//        }
//
//        // 새 이미지 업로드 및 기존 이미지 순서 업데이트
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
//            if (i < fileIds.size()) {
//                // 기존 이미지 업데이트
//                int fileId = fileIds.get(i);
//                Files existingFile = getFilesById(fileId);
//                if (existingFile != null) {
//                    updateFilesSequence(existingFile);
//                } else {
//                    // 파일이 없으면 새로 업로드
//                    uploadImgFile(user, file, imgType, fundingId);
//                }
//            } else {
//                // 새 이미지 업로드
//                uploadImgFile(user, file, imgType, fundingId);
//            }
//        }
//    }


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
