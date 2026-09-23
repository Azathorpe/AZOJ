package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.TagDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.TagVO;
import org.example.azoi.model.problem_model.Tag;
import org.example.azoi.model.user_model.Role;
import org.example.azoi.model.user_model.User;
import org.example.azoi.service.TagService;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.repository.RoleRepository;
import org.example.azoi.utils.repository.TagRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);
    private final TagRepository tagRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public TagServiceImpl(TagRepository tagRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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
        if (result.getCode() == Result.FAIL)
            return new Result<>(null, Result.SUCCESS, result.getMsg());

        for (TagDTO tagDTO : tagDTOs)
            addTag(tagDTO);

        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Void> deleteTag(Long tagId, Long requesterId) {
        Result<Void> result = checkerAdmin(requesterId);
        if (result.getCode() == Result.FAIL)
            return new Result<>(null, Result.SUCCESS, result.getMsg());

        tagRepository.deleteById(tagId);
        return new Result<>(null, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Void> modifyTag(Tag tag, Long requesterId) {
        Result<Void> result = checkerAdmin(requesterId);
        if (result.getCode() == Result.FAIL)
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
     *
     * @param requesterId 请求者Id
     * @return 实际上Result没有内容，直接查看code
     */
    private Result<Void> checkerAdmin(Long requesterId) {
        //只有管理员才能能添加新的角色
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new BusinessException("未找到您的信息: requesterId not found: " + requesterId));

        Role role = roleRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException("未找到该角色: roleId not found:"));

        if (!role.getName().equals(Role.ROLE_ADMIN))
            return new Result<>(null, Result.FAIL, "您不是管理员");
        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
