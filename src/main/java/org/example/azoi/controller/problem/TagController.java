package org.example.azoi.controller.problem;


import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.TagDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.TagVO;
import org.example.azoi.model.problem_model.Tag;
import org.example.azoi.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/list")
    public ResponseEntity<Result<List<TagVO>>> listTags() {
        Result<List<TagVO>> result = tagService.getAllTag();
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @PostMapping("/add")
    public ResponseEntity<Result<Void>> addTag(@RequestBody TagDTO tagDTO) {
        Result<Void> result = tagService.addTag(tagDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @PostMapping("/adds")
    public ResponseEntity<Result<Void>> addTags(@RequestBody List<TagDTO> tagDTOs) {
        Result<Void> result = tagService.addTags(tagDTOs);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @PostMapping("/modify")
    public ResponseEntity<Result<Void>> modifyTag(@RequestBody Tag tag) {
        Result<Void> result = tagService.modifyTag(tag);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @DeleteMapping("/delete/{tagId}")
    public ResponseEntity<Result<Void>> removeTag(@PathVariable Long tagId) {
        Result<Void> result = tagService.deleteTag(tagId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
