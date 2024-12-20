package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.requestdto.RequestProjectInfoDTO;
import com.kosa.backend.funding.project.dto.responsedto.ResponseProjectDTO;
import com.kosa.backend.funding.project.dto.responsedto.ResponseProjectInfoDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectIntroDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestProjectScheduleDTO;
import com.kosa.backend.funding.project.service.ProjectService;
import com.kosa.backend.funding.project.service.RewardService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.MakerService;
import com.kosa.backend.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProjectApiController {
    private final UserService userService;
    private final MakerService makerService;
    private final ProjectService projectService;

    // 프로젝트 생성 - 시작 컨트롤러
    @GetMapping("/studio/start")
    public ResponseEntity<?> start(@AuthenticationPrincipal CustomUserDetails customUser) {
        try {
            User user = userService.findByEmail(customUser.getUsername());
            Maker maker = makerService.findById(user.getId());

            int projectId = projectService.save(maker);

            Map<String, Object> response = new HashMap<>();
            response.put("projectId", projectId);

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("프로젝트를 생성할 수 없습니다.");
        }
    }

    // 프로젝트 생성 - 인트로 컨트롤러
    @PostMapping("/studio/{projectId}/intro")
    public ResponseEntity<?> intro(@AuthenticationPrincipal CustomUserDetails customUser,
                                   @RequestBody RequestProjectIntroDTO projectIntroDTO,
                                   @PathVariable(name = "projectId") int projectId) {
        // 로그인한 사용자 id
        User user = userService.findByEmail(customUser.getUsername());
        int makerId = makerService.findById(user.getId()).getId();

        // 게시글 작성한 사용자 id
        int projectMakerId = projectService.getProjectUser(projectId);

        // makerId와 projectMakerId가 다르면 잘못된 접근 메시지 반환
        if (makerId != projectMakerId) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "잘못된 접근입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // 게시글, 제목, 카테고리 작성
        int updatedProjectId = projectService.updateIntro(projectIntroDTO, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);
    }

    // 프로젝트 가져오기 컨트롤러
    @GetMapping("/studio/{projectId}/project")
    public ResponseEntity<?> getProject(@AuthenticationPrincipal CustomUserDetails customUser,
                                      @PathVariable(name = "projectId") int projectId) {
        // 로그인한 사용자 id
        User user = userService.findByEmail(customUser.getUsername());
        int makerId = makerService.findById(user.getId()).getId();

        // 게시글 작성한 사용자 id
        int projectMakerId = projectService.getProjectUser(projectId);

        // makerId와 projectMakerId가 다르면 잘못된 접근 메시지 반환
        if (makerId != projectMakerId) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "잘못된 접근입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        Map<String, Object> response = new HashMap<>();

        // 서비스에서 프로젝트 정보를 가져옴
        ResponseProjectDTO projectInfo = projectService.getProject(projectId);

        // 가져온 데이터를 응답에 추가
        response.put("success", true);
        response.put("projectInfo", projectInfo);

        System.out.println(projectInfo.toString());

        return ResponseEntity.ok(response);
    }

    // 프로젝트 정보 가져오기 컨트롤러
    @GetMapping("/studio/{projectId}/info")
    public ResponseEntity<?> getProjectinfo(@AuthenticationPrincipal CustomUserDetails customUser,
                                        @PathVariable(name = "projectId") int projectId) {
        // 로그인한 사용자 id
        User user = userService.findByEmail(customUser.getUsername());
        int makerId = makerService.findById(user.getId()).getId();

        // 게시글 작성한 사용자 id
        int projectMakerId = projectService.getProjectUser(projectId);

        // makerId와 projectMakerId가 다르면 잘못된 접근 메시지 반환
        if (makerId != projectMakerId) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "잘못된 접근입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        Map<String, Object> response = new HashMap<>();

        // 서비스에서 프로젝트 정보를 가져옴
        ResponseProjectInfoDTO projectInfo = projectService.getAllProjectInfo(projectId);

        // 가져온 데이터를 응답에 추가
        response.put("success", true);
        response.put("projectInfo", projectInfo);

        System.out.println(projectInfo.toString());

        return ResponseEntity.ok(response);
    }

    // 사용자별 프로젝트 전부 가져오기 컨트롤러
    @GetMapping("/studio/projectslist")
    public ResponseEntity<List<ResponseProjectDTO>> getProjectsList(@AuthenticationPrincipal CustomUserDetails customUser) {
        // 로그인한 사용자 id
        User user = userService.findByEmail(customUser.getUsername());
        Maker maker = makerService.findById(user.getId());

        // Maker에 해당하는 프로젝트 리스트 조회
        List<ResponseProjectDTO> responseProjectDTOS = projectService.getAllProjects(maker);

        // 리스트를 ResponseEntity로 반환
        return ResponseEntity.ok(responseProjectDTOS);
    }

    // 프로젝트 생성 - 스케쥴 컨트롤러
    @PostMapping("/studio/{projectId}/schedule")
    public ResponseEntity<?> schedule(@AuthenticationPrincipal CustomUserDetails customUser,
                                      @RequestBody RequestProjectScheduleDTO projectScheduleDTO,
                                      @PathVariable(name = "projectId") int projectId) {
        // 로그인한 사용자 id
        User user = userService.findByEmail(customUser.getUsername());
        int makerId = makerService.findById(user.getId()).getId();

        // 게시글 작성한 사용자 id
        int projectMakerId = projectService.getProjectUser(projectId);

        // makerId와 projectMakerId가 다르면 잘못된 접근 메시지 반환
        if (makerId != projectMakerId) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "잘못된 접근입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // 게시글 펀딩 시작일, 펀딩 종료일
        int updatedProjectId = projectService.updateSchedule(projectScheduleDTO, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);
    }

    // 프로젝트 생성 - 정보 입력 컨트롤러
    @PostMapping("/studio/{projectId}/info")
    public ResponseEntity<?> info(@AuthenticationPrincipal CustomUserDetails customUser,
                                  @RequestBody RequestProjectInfoDTO projectInfoDTO,
                                  @PathVariable(name = "projectId") int projectId) {

        // 로그인한 사용자 id
        User user = userService.findByEmail(customUser.getUsername());
        int makerId = makerService.findById(user.getId()).getId();

        // 게시글 작성한 사용자 id
        int projectMakerId = projectService.getProjectUser(projectId);

        // makerId와 projectMakerId가 다르면 잘못된 접근 메시지 반환
        if (makerId != projectMakerId) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "잘못된 접근입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        int updatedProjectId = projectService.updateInfo(projectInfoDTO, projectId);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", updatedProjectId);

        return ResponseEntity.ok()
                .body(response);
    }

    // 프로젝트 삭제 컨드롤러
    @GetMapping("/studio/{projectId}/delete")
    public ResponseEntity<?> delete(@PathVariable(name = "projectId") int projectId) {
        projectService.delete(projectId);

        return ResponseEntity.ok()
                .build();
    }
}
