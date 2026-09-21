package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.TagDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.TagVO;
import org.example.azoi.model.problem_model.Tag;
import org.example.azoi.model.team_model.Role;
import org.example.azoi.model.user_model.UserRole;
import org.example.azoi.service.TagService;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.repository.TagRepository;
import org.example.azoi.utils.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {
    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);
    private final TagRepository tagRepository;
    private final UserRoleRepository userRoleRepository;

    public TagServiceImpl(TagRepository tagRepository, UserRoleRepository userRoleRepository) {
        this.tagRepository = tagRepository;
        this.userRoleRepository = userRoleRepository;
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
    public Result<Void> addTags(List<TagDTO> tagDTOs, Long requesterId) {
        Result<Void> result = checkerAdmin(requesterId);
        if(result.getCode() == Result.FAIL)
            return new Result<>(null, Result.SUCCESS, result.getMsg());

        for(TagDTO tagDTO : tagDTOs)
            addTag(tagDTO);

        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Void> deleteTag(Long tagId, Long requesterId) {
        Result<Void> result = checkerAdmin(requesterId);
        if(result.getCode() == Result.FAIL)
            return new Result<>(null, Result.SUCCESS, result.getMsg());

        tagRepository.deleteById(tagId);
        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Void> modifyTag(Tag tag, Long requesterId) {
        Result<Void> result = checkerAdmin(requesterId);
        if(result.getCode() == Result.FAIL)
            return new Result<>(null, Result.SUCCESS, result.getMsg());

        Tag saver = tagRepository.findById(tag.getId())
                .orElseThrow(() -> new BusinessException("未找到Tag"));
        saver.setName(tag.getName());
        saver.setColor(tag.getColor());
        tagRepository.save(saver);

        return new Result<>(null, Result.SUCCESS, "ok");
    }

    /**
     * 检查一个角色是否是admin
     * @param requesterId 请求者Id
     * @return 实际上Result没有内容，直接查看code
     */
    private Result<Void> checkerAdmin(Long requesterId) {
        //只有管理员才能能添加新的角色
        Optional<UserRole> requester = userRoleRepository.findById_UserId((requesterId));
        if (requester.isEmpty())
            return new Result<>(null, Result.FAIL, "未找到您的信息: requester is empty");
        if (!requester.get().getRole().getName().equals(Role.ROLE_ADMIN)) {
            return new Result<>(null, Result.FAIL, "您不是管理员");
        }
        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
