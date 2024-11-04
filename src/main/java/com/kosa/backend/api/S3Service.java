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

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final FundingRepository fundingRepository;
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public String uploadFile(MultipartFile file, String path, ImgType imgType, int fundingId, int userId) throws IOException {
//        존재하지 않는 funding_id나 user_id가 전달될 경우 예외가 발생합니다. 이를 적절히 처리해야 합니다.
//        트랜잭션 관리에 주의해야 합니다. 파일 업로드와 데이터베이스 저장이 하나의 트랜잭션으로 처리되어야 합니다.
//        예외 처리를 보다 세밀하게 할 필요가 있습니다. 예를 들어, S3 업로드 실패 시 데이터베이스 저장을 롤백하는 등의 처리가 필요할 수 있습니다.

        String fileName = generateFileName(file);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // db에 저장
        saveFile(file, path, imgType, fundingId, userId, fileName);

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toString();
        return fileUrl;
    }

    @Transactional
    public void saveFile(MultipartFile file, String path, ImgType imgType, int fundingId, int userId, String fileName) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new RuntimeException("Funding not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Files newFile = Files.builder()
                .path(path)
                .originalNm(file.getOriginalFilename())
                .savedNm(fileName)
                .imgType(imgType)
                .funding(funding)
                .user(user)
                .build();

        filesRepository.save(newFile);
    }

    public InputStreamResource downloadThumbnailByFundingId(int fundingId) {
        Optional<Files> tmpFile = filesRepository.findByIdAndImgType(fundingId, ImgType.THUMBNAIL);

        if(tmpFile.isEmpty()) {
            return null;
        }

        Files file = tmpFile.get();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getSavedNm())
                .build();

        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(object);
    }

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
}
