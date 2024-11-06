package com.kosa.backend.funding.project.controller;

import com.kosa.backend.api.S3Service;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectInfoDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectIntroDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectScheduleDTO;
import com.kosa.backend.funding.project.service.ProjectService;
import com.kosa.backend.funding.project.service.RewardService;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.MakerService;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProjectApiController {
    private final UserService userService;
    private final MakerService makerService;
    private final ProjectService projectService;
    private final RewardService rewardService;

    // 프로젝트 생성 - 시작 컨트롤러
    @GetMapping("/studio/start")
    public ResponseEntity<?> start() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        Maker maker = makerService.findById(user.getId());

        // 게시글 생성
        int projectId = projectService.save(maker);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectId);

        return ResponseEntity.ok()
                .body(response);
    }

    // 프로젝트 생성 - 인트로 컨트롤러
    @PostMapping("/studio/{id}/intro")
    public ResponseEntity<?> intro(@RequestBody RequestProjectIntroDTO projectIntroDTO
                      ,@PathVariable(name = "id") int projectId) {
        // 게시글, 제목, 카테고리 작성
        int updatedProjectId = projectService.updateIntro(projectIntroDTO, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/studio/{id}/funding")
    public void main2() {

    }

    // 프로젝트 생성 - 스케쥴 컨트롤러
    @PostMapping("/studio/{id}/schedule")
    public ResponseEntity<?> schedule(@RequestBody RequestProjectScheduleDTO projectScheduleDTO
            ,@PathVariable(name = "id") int projectId) {
        // 게시글 펀딩 시작일, 펀딩 종료일
        int updatedProjectId = projectService.updateSchedule(projectScheduleDTO, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);
    }

    // 프로젝트 생성 - 정보 입력 컨트롤러
    @PostMapping("/studio/{id}/screening")
    public ResponseEntity<?> screening(@RequestBody RequestProjectInfoDTO projectInfoDTO
            , @PathVariable(name = "id") int projectId) {
        int updatedProjectId = projectService.updateInfo(projectInfoDTO, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);

    }

    @PostMapping("/studio/{id}/story")
    public void main5() {

    }
}
