package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.responsedto.ResponseProjectInfoDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectIntroDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectScheduleDTO;
import com.kosa.backend.funding.project.service.ProjectService;
import com.kosa.backend.funding.project.service.RewardService;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.MakerService;
import com.kosa.backend.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/studio/{id}/info")
    public ResponseEntity<?> info(@RequestBody ResponseProjectInfoDTO projectInfoDTO
            , @PathVariable(name = "id") int projectId) {
        int updatedProjectId = projectService.updateInfo(projectInfoDTO, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);
    }

    // 컨트롤러 메서드 작성
    @GetMapping("/studio/{id}/info")
    public ResponseEntity<?> screening(@PathVariable(name = "id") int projectId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 서비스에서 프로젝트 정보를 가져옴
            ResponseProjectInfoDTO projectInfo = projectService.getInfo(projectId);

            // 가져온 데이터를 응답에 추가
            response.put("success", true);
            response.put("projectInfo", projectInfo);

            System.out.println(projectInfo.toString());

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            // 프로젝트 정보를 찾지 못한 경우
            response.put("success", false);
            response.put("message", "Project not found with id: " + projectId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            // 기타 예외 처리
            response.put("success", false);
            response.put("message", "An error occurred while retrieving the project information.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/studio/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int projectId) {
        projectService.delete(projectId);

        return ResponseEntity.ok()
                .build();
    }
}
