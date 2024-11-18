package com.kosa.backend.common.service;

import com.kosa.backend.common.dto.FileDTO;
import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.common.repository.FilesRepository;
import com.kosa.backend.common.repository.FilesSequenceRepository;
import com.kosa.backend.funding.project.repository.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class S3CustomService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FundingRepository fundingRepository;
    private final FilesRepository filesRepository;
    private final FilesSequenceRepository filesSequenceRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    // 서명된 URL 생성 메소드
    public String generateSignedUrl(String objectKey) {
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(builder -> builder.bucket(bucketName).key(objectKey))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    public FileDTO getThumbnailByFundingId(int fundingId) {
        Optional<Files> fileOptional = filesRepository.findByFundingIdAndImgType(fundingId, ImgType.THUMBNAIL);

        // 값이 없으면 null 반환
        Files file = fileOptional.orElse(null);

        if (file == null) {
            return null; // null 반환
        }

        String signedUrl = generateSignedUrl(file.getPath() + file.getSavedNm());

        return FileDTO.builder()
                .fileId(file.getId())
                .signedUrl(signedUrl)
                .build();
    }

    // 펀딩ID별 디테일 이미지 조회 메소드
    public FileDTO getDetailByFundingId(int fundingId) {
        Optional<Files> fileOptional = filesRepository.findByFundingIdAndImgType(fundingId, ImgType.DETAIL_IMAGE);

        // Optional이 비어있으면 null 반환
        if (fileOptional.isEmpty()) {
            return null;
        }

        Files file = fileOptional.get();
        String signedUrl = generateSignedUrl(file.getPath() + file.getSavedNm());

        return FileDTO.builder()
                .fileId(file.getId())
                .signedUrl(signedUrl)
                .build();
    }
}
