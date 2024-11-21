package com.kosa.backend.user.dto.requestDTO;

import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestUserDTO {
    private String userName;
    private String password;
    private String userContent;

    public static RequestUserDTO toEntity(User user, Maker maker) {
        return RequestUserDTO.builder()
                .userName(user.getUsername())
                .userContent(maker.getUserContent())
                .build();
    }
}
