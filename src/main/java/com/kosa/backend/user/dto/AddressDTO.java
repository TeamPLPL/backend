package com.kosa.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private int id;
    private String zonecode;
    private String addr;
    private String addrEng;
    private String detailAddr;
    private String extraAddr;

    @JsonProperty("isDefault")
    private boolean isDefault;

    private int userId; // User와 연결된 ID
}
