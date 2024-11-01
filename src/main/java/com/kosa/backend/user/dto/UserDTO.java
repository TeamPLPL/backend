package com.kosa.backend.user.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String userNick;
    private String password;
}
