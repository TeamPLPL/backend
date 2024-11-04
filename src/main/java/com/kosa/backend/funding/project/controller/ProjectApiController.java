package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.requestdto.RequestProjectIntroDTO;
import com.kosa.backend.funding.project.service.ProjectService;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ProjectApiController {
    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping("/studio/start")
    public void start(){

    }

    @PostMapping("/studio/intro")
    public void intro(@RequestBody RequestProjectIntroDTO requestProjectDTO) {

    }

    @PostMapping("/studio/{id}/funding")
    public void main2() {

    }

    @PostMapping("/studio/{id}/schedule")
    public void main3() {

    }

    @PostMapping("/studio/{id}/screening")
    public void main4() {

    }@PostMapping("/studio/{id}/story")
    public void main5() {

    }
}
