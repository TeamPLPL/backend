package com.kosa.backend.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/nicepay")
public class NicepayController {

    @Value("${nicepay.credentials}")
    private String credentials;  // application.yml에서 가져온 크레덴셜

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") String orderId) {
        String url = "https://sandbox-api.nicepay.co.kr/v1/payments/find/" + orderId;
        System.out.println(url);
        String encodedCredentials = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encodedCredentials);
        headers.set("Content-Type", "application/json;charset=utf-8");

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException ex) {
            // 401 에러 핸들링
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching order details: " + e.getMessage());
        }
    }

    @PostMapping("/order/{tid}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable("tid") String tid, @RequestBody Map<String, String> requestData) {
        String url = "https://sandbox-api.nicepay.co.kr/v1/payments/" + tid + "/cancel";
        String encodedCredentials = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encodedCredentials);
        headers.set("Content-Type", "application/json;charset=utf-8");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestData, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling transaction: " + e.getMessage());
        }
    }
}
