package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardInfoDTO;
import com.kosa.backend.funding.project.service.RewardSubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RewardSubApiController {
    // 작성자 : 신은호, 작성 내용 : 작성한 RewardApiController 분리
    private final RewardSubService rewardService;

    // 프로젝트 리워드 생성
    @PostMapping("/studio/{id}/reward")
    public ResponseEntity<?> reward(@RequestBody List<RequestRewardDTO> rewardsDTOList
            , @PathVariable(name = "id") int projectId) {
        // 리워드 여러개일 수 있음
        int projectIdFromReward = rewardService.save(rewardsDTOList, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectIdFromReward);

        return ResponseEntity.ok()
                .body(response);
    }

    // 프로젝트 정책 생성
    @PostMapping("/studio/{id}/policy")
    public ResponseEntity<?> policy(@RequestBody RequestRewardInfoDTO requestRewardInfoDTO
            , @PathVariable(name = "id") int projectId ) {

        int projectIdFromRewardInfo = rewardService.savePolicy(requestRewardInfoDTO, projectId);


        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectIdFromRewardInfo);

        return ResponseEntity.ok()
                .body(response);
    }

}
