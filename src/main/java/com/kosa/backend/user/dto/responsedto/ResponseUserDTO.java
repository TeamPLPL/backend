package com.kosa.backend.user.dto.responsedto;

import com.kosa.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseUserDTO {
    String email;
    String userName;
    String userNick;
    String provider;
    String userContent;

    public static ResponseUserDTO toEntity(User user) {
        return ResponseUserDTO.builder()
                .email(user.getEmail())
                .userNick(user.getUserNick())
                .provider(user.getProvider())
                .build();
    }

    public void updateUserContent(String userContent) {this.userContent = userContent;}

    public static ResponseUserDTO toEntityByISMS(User user) {
        String email = user.getEmail();
        String userName = user.getUserName();
        String userNick = user.getUserNick();
        String provider = user.getProvider();

        // email 보안처리
        String temp1 = email.substring(0, email.indexOf("@"));
        String temp2 = email.substring(email.indexOf("@"), email.length());
        String temp3 = temp1.substring(0, 1);
        String emailISMS = temp3 + "******" + temp2;

        String providerISMS;
        if(provider != null) {
            providerISMS = provider.split("_")[0];
        } else {
            providerISMS = null;
        }

        return ResponseUserDTO.builder()
                .userName(userName)
                .email(emailISMS)
                .userNick(userNick)
                .provider(providerISMS)
                .build();
    }
}
