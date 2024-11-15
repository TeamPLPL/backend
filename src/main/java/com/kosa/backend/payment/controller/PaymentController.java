package com.kosa.backend.payment.controller;

import com.kosa.backend.payment.dto.PaymentDTO;
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable("userId") int userId) {
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
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
