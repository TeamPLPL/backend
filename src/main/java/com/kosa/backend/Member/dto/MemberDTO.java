package com.kosa.backend.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberDTO {
    private String email;
    private String password;
    private String authority;
}
