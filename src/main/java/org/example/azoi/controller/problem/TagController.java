package org.example.azoi.controller.problem;


import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.TagVO;
import org.example.azoi.model.problem_model.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    @GetMapping("/list")
    public ResponseEntity<Result<List<TagVO>>> listTags() {

    }
}
