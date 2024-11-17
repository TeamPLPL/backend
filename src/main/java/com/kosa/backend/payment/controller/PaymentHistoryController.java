package com.kosa.backend.payment.controller;

import com.kosa.backend.payment.dto.PaymentHistoryDTO;
import com.kosa.backend.payment.service.PaymentHistoryService;
import com.kosa.backend.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment-history")
@RequiredArgsConstructor
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    // 사용자 참여 내역 조회
    @GetMapping("/user")
    public ResponseEntity<List<PaymentHistoryDTO>> getUserParticipationHistory(@AuthenticationPrincipal CustomUserDetails cud) {
        String userEmail = cud.getUsername();
        List<PaymentHistoryDTO> participationHistory = paymentHistoryService.getParticipationHistoryByUserEmail(userEmail);
        return ResponseEntity.ok(participationHistory);
    }
}
