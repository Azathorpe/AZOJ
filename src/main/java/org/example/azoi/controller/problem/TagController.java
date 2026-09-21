package org.example.azoi.controller.problem;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.TagDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.TagVO;
import org.example.azoi.model.problem_model.Tag;
import org.example.azoi.service.TagService;
import org.example.azoi.utils.anno.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 列出所有Tag<br/>
     * 请求地址: /tag/list<br/>
     * 请求方法: /tag/list
     * @return TagVO{@link List} of {@link TagVO}
     */
    @GetMapping("/list")
    public ResponseEntity<Result<List<TagVO>>> listTags() {
        Result<List<TagVO>> result = tagService.getAllTag();
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 添加Tag(管理员接口)<br/>
     * 请求地址: /tag/add<br/>
     * 请求方法: /tag/add -> json {@link TagDTO}
     * @param tagDTO TagDTO{@link TagDTO}
     * @param requesterId 请求者Id
     * @return 无
     */
    @PostMapping("/add")
    public ResponseEntity<Result<Void>> addTag(
            @RequestBody TagDTO tagDTO,
            @CurrentUser Long requesterId) {
        Result<Void> result = tagService.addTags(Collections.singletonList(tagDTO), requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 批量添加Tag(管理员接口)<br/>
     * 请求地址: /tag/adds<br/>
     * 请求方法: /tag/adds -> json {@link List} of {@link TagDTO}
     * @param tagDTOs {@link List} of {@link TagDTO}
     * @param requesterId 请求者Id
     * @return 无
     */
    @PostMapping("/adds")
    public ResponseEntity<Result<Void>> addTags(
            @RequestBody List<TagDTO> tagDTOs,
            @CurrentUser Long requesterId) {
        Result<Void> result = tagService.addTags(tagDTOs, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 编辑一个Tag(管理员接口)<br/>
     * 请求地址: /tag/modify<br/>
     * 请求方法: /tag/modify -> json {@link Tag}
     * @param tag Tag{@link Tag}
     * @param requesterId 请求者Id
     * @return 无
     */
    @PostMapping("/modify")
    public ResponseEntity<Result<Void>> modifyTag(
            @RequestBody Tag tag,
            @CurrentUser Long requesterId) {
        Result<Void> result = tagService.modifyTag(tag, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 删除一个Tag(管理员接口)<br/>
     * 请求地址: /tag/delete/{tagId}<br/>
     * 请求方法: /tag/delete/{tagId}
     * @param tagId TagId
     * @param requesterId 请求者Id
     * @return 无
     */
    @DeleteMapping("/delete/{tagId}")
    public ResponseEntity<Result<Void>> removeTag(
            @PathVariable Long tagId,
            @CurrentUser Long requesterId) {
        Result<Void> result = tagService.deleteTag(tagId, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
