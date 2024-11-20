package com.kosa.backend.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public String sendHtmlEmail(String email) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        Random random = new Random(); // 난수 생성을 위한 랜덤 클래스
        String key = ""; // 인증번호 담을 String key 변수 생성

        // 입력 키를 위한 난수 생성 코드
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(26) + 65;
            key += (char) index;
        }
        for (int i = 0; i < 6; i++) {
            int numIndex = random.nextInt(10);
            key += numIndex;
        }

        // HTML 내용 작성
        String htmlContent = """
                    <html>
                        <body style="font-family: Arial, sans-serif;">
                            <h2 style="color: #4CAF50;">Plantiful 회원가입 이메일 인증</h2>
                            <p>안녕하세요! 회원가입을 환영합니다. 아래 인증번호를 입력해 주세요:</p>
                            <div style="border: 1px solid #ddd; padding: 15px; font-size: 18px; background-color: #f9f9f9; width: fit-content;">
                                <strong>인증번호:</strong> <span style="color: #FF5722; font-weight: bold;">%s</span>
                            </div>
                            <br>
                            <p style="color: #888;">이메일 관련 문의사항은 <a href="support@kosa.com">support@kosa.com</a>으로 연락해주세요.</p>
                        </body>
                    </html>
                """.formatted(key);

        helper.setTo(email);
        helper.setSubject("회원가입을 위한 이메일 인증번호 메일입니다.");
        helper.setText(htmlContent, true); // HTML 형식으로 설정

        // 발신자 주소 설정
        helper.setFrom("no-reply@kosa.com", "Kosa Admin");

        javaMailSender.send(mimeMessage);

        return key;
    }
}
