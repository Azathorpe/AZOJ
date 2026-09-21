package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.TagDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.TagVO;
import org.example.azoi.model.problem_model.Tag;

import java.util.List;

public interface TagService {
    Result<List<TagVO>> getAllTag();

    Result<Void> addTag(TagDTO tagDTO);

    Result<Void> addTags(List<TagDTO> tagDTOs, Long requesterId);

    Result<Void> deleteTag(Long tagId, Long requesterId);

    Result<Void> modifyTag(Tag tag, Long requesterId);
}
