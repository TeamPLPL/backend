package com.kosa.backend.user.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private int id;
    private String zonecode;
    private String addr;
    private String addrEng;
    private String detailAddr;
    private boolean isDefault;
    private int userId; // User와 연결된 ID
}
