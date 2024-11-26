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
    private String userNick;
    private String password;
    private String userContent;
}
