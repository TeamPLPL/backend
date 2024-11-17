package com.kosa.backend.payment.service;

import com.kosa.backend.payment.dto.PaymentHistoryDTO;
import com.kosa.backend.payment.entity.PaymentHistory;
import com.kosa.backend.payment.repository.PaymentHistoryRepository;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;

    public List<PaymentHistoryDTO> getParticipationHistoryByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return paymentHistoryRepository.findByPayment_User_Id(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PaymentHistoryDTO mapToDTO(PaymentHistory paymentHistory) {
        return PaymentHistoryDTO.builder()
                .id(paymentHistory.getPayment().getId())
                .category(paymentHistory.getFunding().getSubCategory().getMainCategory().getMainCategoryName())
                .subcategory(paymentHistory.getFunding().getSubCategory().getSubCategoryName())
//                .status("진행중") // 혹은 Funding 상태를 기준으로 설정
                .title(paymentHistory.getFunding().getFundingTitle())
                .author(paymentHistory.getFunding().getMaker().getUser().getUserNick())
                .date(paymentHistory.getCreatedAt().toLocalDate().toString())
                .currentAmount(paymentHistory.getFunding().getCurrentAmount())
                .targetAmount(paymentHistory.getFunding().getTargetAmount())
                .fundingStartDate(paymentHistory.getFunding().getFundingStartDate())
                .fundingEndDate(paymentHistory.getFunding().getFundingEndDate())
                .build();
    }
}
