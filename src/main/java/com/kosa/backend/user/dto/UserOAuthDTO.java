package com.kosa.backend.user.dto;

import com.kosa.backend.user.entity.enums.Authority;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOAuthDTO {
    private int id;
    private String provider;
    private String userName;
    private String email;
    private Authority authority;
}
