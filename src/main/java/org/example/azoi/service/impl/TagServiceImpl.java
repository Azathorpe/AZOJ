package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.TagDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.TagVO;
import org.example.azoi.model.problem_model.Tag;
import org.example.azoi.service.TagService;
import org.example.azoi.utils.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Result<List<TagVO>> getAllTag() {
        return new Result<>(
                tagRepository.findAll().stream().map(TagVO::new).toList(),
                Result.SUCCESS,
                "ok");
    }

    @Override
    public Result<Void> addTag(TagDTO tagDTO) {
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        tag.setColor(tagDTO.getColor());
        tagRepository.save(tag);
        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Void> addTags(List<TagDTO> tagDTOs) {
        for(TagDTO tagDTO : tagDTOs)
            addTag(tagDTO);

        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Void> deleteTag(Long tagId) {
        tagRepository.deleteById(tagId);
        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Void> modifyTag(Tag tag) {
        tagRepository.findById(tag.getId())
                .ifPresent(tg -> {
                    tg.setName(tag.getName());
                    tg.setColor(tag.getColor());
                });
        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
