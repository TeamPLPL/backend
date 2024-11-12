package com.kosa.backend.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kosa.backend.payment.dto.PaymentDTO.RewardInfo;

import java.io.IOException;
import java.util.Map;

public class RewardMapSerializer extends JsonSerializer<Map<Integer, RewardInfo>> {

    @Override
    public void serialize(Map<Integer, RewardInfo> rewards, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject(); // JSON 객체 시작
        for (Map.Entry<Integer, RewardInfo> entry : rewards.entrySet()) {
            gen.writeFieldName(String.valueOf(entry.getKey())); // rewardId를 키로 사용
            gen.writeStartObject();
            gen.writeNumberField("rewardId", entry.getValue().getRewardId());
            gen.writeStringField("rewardName", entry.getValue().getRewardName());
            gen.writeNumberField("quantity", entry.getValue().getQuantity());
            gen.writeEndObject();
        }
        gen.writeEndObject(); // JSON 객체 종료
    }
}
