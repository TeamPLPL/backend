package com.kosa.backend.user.dto.responsedto;

import com.kosa.backend.user.dto.UserDTO;
import com.kosa.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseUserDTO {
    String email;
    String userName;
    String userNick;
    String provider;

    public static ResponseUserDTO toEntity(User user) {
        return ResponseUserDTO.builder()
                .email(user.getEmail())
                .userNick(user.getUserNick())
                .provider(user.getProvider())
                .build();
    }

//    public static ResponseUserDTO toEntityByISMS(User user) {
//        String email = user.getEmail();
//        String userName = user.getUsername();
//        String userNick = user.getUserNick();
//        String provider = user.getProvider();
//
//        // email 보안처리
//        String temp1 = email.substring(0, email.indexOf("@"));
//        String temp2 = email.substring(email.indexOf("@"), email.length());
//        String temp3 = temp1.substring(0, 4);
//    }
}
