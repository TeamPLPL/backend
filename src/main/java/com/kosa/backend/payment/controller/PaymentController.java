package com.kosa.backend.payment.controller;

import com.kosa.backend.payment.dto.PaymentDTO;
import com.kosa.backend.payment.dto.PaymentDetailDTO;
import com.kosa.backend.payment.dto.RewardPurchaseRequest;
import com.kosa.backend.payment.service.PaymentService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    // 결제 이력 등록
    @PostMapping("/register")
    public ResponseEntity<PaymentDTO> registerPayment(@AuthenticationPrincipal CustomUserDetails cud, @RequestBody PaymentDTO paymentDTO) {
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PaymentDTO savedPaymentDTO = paymentService.createPayment(paymentDTO, user);
        return ResponseEntity.ok(savedPaymentDTO);
    }

    @PostMapping("/{fundingId}/update-current-amount")
    public ResponseEntity<String> updateCurrentAmount(
            @PathVariable("fundingId") int fundingId,
            @RequestBody Map<String, Integer> payload) {

        int calculatedAmount = payload.getOrDefault("calculatedAmount", 0); // 배송비 제외한 금액
        paymentService.updateFundingCurrentAmount(fundingId, calculatedAmount);

        return ResponseEntity.ok("Funding current amount updated successfully.");
    }

    // 남은 리워드 수량 조회
    @GetMapping("/{rewardId}/remaining")
    public ResponseEntity<Map<String, Object>> getRemainingQuantity(
            @PathVariable int rewardId,
            @RequestParam int fundingId) {
        int remainingQuantity = paymentService.getRemainingQuantity(rewardId, fundingId);

        Map<String, Object> response = new HashMap<>();
        response.put("rewardId", rewardId);
        response.put("fundingId", fundingId);
        response.put("remainingQuantity", remainingQuantity);

        return ResponseEntity.ok(response);
    }

    // 구매 검증
    @PostMapping("/validate-purchase")
    public ResponseEntity<Map<String, Object>> validatePurchase(@RequestBody List<RewardPurchaseRequest> purchaseRequests) {
        System.out.println("받은 purchaseRequests 데이터: " + purchaseRequests);
        Map<String, Object> response = new HashMap<>();
//        List<Map<String, Object>> details = new ArrayList<>();
        boolean allValid = true; // 모든 리워드 검증이 통과되었는지 여부

        for (RewardPurchaseRequest purchaseRequest : purchaseRequests) {
            System.out.println("개별 요청 데이터: " + purchaseRequest);
            int rewardId = purchaseRequest.getRewardId();
            int fundingId = purchaseRequest.getFundingId();
            int purchaseQuantity = purchaseRequest.getPurchaseQuantity();

            try {
                // 리워드 남은 수량 계산
                int remainingQuantity = paymentService.getRemainingQuantity(rewardId, fundingId);

                if (remainingQuantity < purchaseQuantity) {
                    allValid = false;
                    response.put("valid", false);
                    response.put("rewardId", rewardId);
                    response.put("remainingQuantity", remainingQuantity);
                    response.put("message", String.format("리워드 %d의 남은 수량이 부족합니다. (남은 수량: %d)", rewardId, remainingQuantity));

                    // 검증 실패 시 즉시 반환
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (IllegalStateException e) {
                allValid = false;
                response.put("valid", false);
                response.put("rewardId", rewardId);
                response.put("message", e.getMessage());
                response.put("remainingQuantity", paymentService.getRemainingQuantity(rewardId, fundingId));

                // 검증 실패 시 즉시 반환
                return ResponseEntity.badRequest().body(response);
            }
        }

        response.put("valid", allValid);
        response.put("message", allValid ? "모든 리워드 구매가 가능합니다." : "리워드 구매에 문제가 있습니다.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancelPayment(
            @PathVariable("paymentId") int paymentId,
            @RequestBody Map<String, Integer> request // 프론트에서 계산된 값 전달
    ) {
        int calculatedAmount = request.get("amount");

        paymentService.cancelPaymentAndUpdateFunding(paymentId, calculatedAmount);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/complete")
    public ResponseEntity<?> handlePaymentCompletion(@RequestParam("id") String id, HttpServletResponse response) throws IOException {
        // NicePay의 POST 요청을 받았는지 확인하기 위한 로그
        System.out.println(response.toString());
        logger.info("Received POST request from NicePay for payment completion");

        // NicePay의 결제 결과와 상태를 확인하는 로직 (필요시 수정)
        String paymentStatus = "success";  // 성공 여부를 설정 (실제 NicePay의 응답 기반)

        // 프론트엔드 개발 서버로 리디렉션 (GET 요청으로 변경)
        String redirectUrl = String.format("http://localhost:3000/purchase/complete/%s?status=%s", id, paymentStatus);
        response.sendRedirect(redirectUrl);
//        response.sendRedirect("http://localhost:3000/purchase/complete/" + id);

        return ResponseEntity.status(HttpStatus.FOUND).build(); // 리디렉션 이후 반환
    }

    // 결제 이력 조회(User ID)
    @GetMapping("/user")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUser(@AuthenticationPrincipal CustomUserDetails cud) {
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PaymentDTO> payments = paymentService.getPaymentsByUserId(user.getId());
        return ResponseEntity.ok(payments);
    }

    // 각 결제별 세부사항 조회
    @GetMapping("/{paymentId}/details")
    public ResponseEntity<PaymentDetailDTO> getPaymentDetailsByPaymentId(@PathVariable("paymentId") int paymentId) {
        PaymentDetailDTO paymentDetails = paymentService.getPaymentDetailsByPaymentId(paymentId);
        return ResponseEntity.ok(paymentDetails);
    }


    // 결제 상태 업데이트
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable int paymentId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok().build();
    }

    // 결제 이력 삭제(Pay ID)
    @DeleteMapping("/user/{userId}/delete/{paymentId}")
    public ResponseEntity<Void> deletePaymentByUser(@PathVariable("userId") int userId, @PathVariable("paymentId") int paymentId) {
        paymentService.deletePaymentByUser(paymentId, userId);
        return ResponseEntity.noContent().build();
    }
}
