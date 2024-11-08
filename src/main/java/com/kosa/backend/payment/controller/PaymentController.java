package com.kosa.backend.payment.controller;

import com.kosa.backend.payment.dto.PaymentDTO;
import com.kosa.backend.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    // 결제 이력 등록
    @PostMapping("/register")
    public ResponseEntity<PaymentDTO> registerPayment(@RequestBody PaymentDTO paymentDTO) {
        PaymentDTO savedPaymentDTO = paymentService.createPayment(paymentDTO);
        return ResponseEntity.ok(savedPaymentDTO);
    }

    @PostMapping("/complete")
    public void handlePaymentCompletion(@RequestParam("id") String id, HttpServletResponse response) throws IOException {
        // NicePay의 POST 요청을 받았는지 확인하기 위한 로그
        logger.info("Received POST request from NicePay for payment completion");

        // 프론트엔드 개발 서버로 리디렉션 (GET 요청으로 변경)
        response.sendRedirect("http://localhost:3000/purchase/step30/" + id);
    }

    // 결제 이력 조회(User ID)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@PathVariable("userId") int userId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    // 결제 이력 삭제(Pay ID)
    @DeleteMapping("/user/{userId}/delete/{paymentId}")
    public ResponseEntity<Void> deletePaymentByUser(@PathVariable("userId") int userId, @PathVariable("paymentId") int paymentId) {
        paymentService.deletePaymentByUser(paymentId, userId);
        return ResponseEntity.noContent().build();
    }
}
