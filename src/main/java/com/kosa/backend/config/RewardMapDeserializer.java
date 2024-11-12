package com.kosa.backend.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.kosa.backend.payment.dto.PaymentDTO.RewardInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RewardMapDeserializer extends JsonDeserializer<Map<Integer, RewardInfo>> {

    @Override
    public Map<Integer, RewardInfo> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<Integer, RewardInfo> rewards = new HashMap<>();

        while (p.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = p.getCurrentName(); // rewardId as string
            p.nextToken();

            RewardInfo rewardInfo = new RewardInfo();
            while (p.nextToken() != JsonToken.END_OBJECT) {
                String key = p.getCurrentName();
                p.nextToken();

                switch (key) {
                    case "rewardId":
                        rewardInfo.setRewardId(p.getIntValue());
                        break;
                    case "rewardName":
                        rewardInfo.setRewardName(p.getText());
                        break;
                    case "quantity":
                        rewardInfo.setQuantity(p.getIntValue());
                        break;
                }
            }

            rewards.put(Integer.valueOf(fieldName), rewardInfo);
        }

        return rewards;
    }
}
