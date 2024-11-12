package com.kosa.backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomKey {
    private int rewardId;
    private String uniqueIdentifier; // 추가 고유 식별자(예: timestamp 또는 userId와 조합)

    @Override
    public int hashCode() {
        return Objects.hash(rewardId, uniqueIdentifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomKey that = (CustomKey) o;
        return rewardId == that.rewardId && Objects.equals(uniqueIdentifier, that.uniqueIdentifier);
    }
}