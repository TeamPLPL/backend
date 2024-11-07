package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.requestdto.RequestProjectIntroDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.service.ProjectService;
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

    // 프로젝트 리워드 생성
    @PostMapping("/studio/{id}/reward")
    public ResponseEntity<?> reward(@RequestBody List<RequestRewardDTO> rewardsDTOList
            , @PathVariable(name = "id") int projectId) {
        // 리워드 여러개일 수 있음
        rewardService.save(rewardsDTOList, projectId);

        Map<String, Object> response = new HashMap<>();
//        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);
    }

//    @PostMapping("/studio/{id}/policy")
//    public ResponseEntity<?> policy(@RequestBody RequestRewardDTO rewardDTO
//            , @PathVariable(name = "id") int projectId ) {
//
//        int updatedProjectId = projectService.updateIntro(rewardDTO, projectId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("projectId", updatedProjectId);
//
//        return ResponseEntity.ok()
//                .body(response);
//    }
}
