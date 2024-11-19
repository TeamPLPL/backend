package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.RewardDTO;
import com.kosa.backend.funding.project.dto.RewardInfoDTO;
import com.kosa.backend.funding.project.dto.RewardResponseDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardInfoDTO;
import com.kosa.backend.funding.project.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RewardApiController {
    private final RewardService rewardService;

    // 후원할 리워드 리스트 반환
    // 매개변수: rewardDTO 안에 rewardId, count 담긴 List
    // rewardDTOList와 deliveryFee 담긴 RewardResponseDTO 담긴 ResponseEntity 반환
    @PostMapping("/reward-list")
    public ResponseEntity<RewardResponseDTO> getRewardDTOList(@RequestBody List<RewardDTO> rewardDTOList) {

        return rewardService.getRewardDTOList(rewardDTOList);
    }

    @GetMapping("/reward-list/all/{id}")
    public ResponseEntity<List<RewardDTO>> getAllRewardDTOList(@PathVariable(name = "id") int fundingId) {

        return rewardService.getAllRewardDTOList(fundingId);
    }

    @GetMapping("/reward-policy/{id}")
    public ResponseEntity<RewardInfoDTO> getRewardPolicy(@PathVariable(name = "id") int fundingId) {
        RewardInfoDTO rewardInfoDTO = rewardService.getRewardInfoDTO(fundingId);
        if(rewardInfoDTO == null) { return ResponseEntity.notFound().build(); }
        return ResponseEntity.ok().body(rewardInfoDTO);
    }
}
