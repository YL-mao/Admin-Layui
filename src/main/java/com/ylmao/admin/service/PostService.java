package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.constant.DictTypeCode;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.dto.PostDto;
import com.ylmao.admin.entity.Post;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.PostMapper;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.PostVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final DictRuntimeService dictRuntimeService;

    public List<PostVo.PostOptionVo> listOptions() {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsEnabled, 1);
        wrapper.orderByAsc(Post::getOrderNum);
        return postMapper.selectList(wrapper).stream().map(PostVo.PostOptionVo::from).toList();
    }

    public IPage<PostVo.PostListVo> selectPage(PageQuery pageQuery, PostDto.PostList postList) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        Page<Post> postPage = pageQuery.toMpPage();
        postPage.addOrder(OrderItem.asc("order_num"));
        if (postList != null) {
            if (StrUtil.isNotBlank(postList.postCode())) {
                wrapper.like(Post::getPostCode, postList.postCode());
            }
            if (StrUtil.isNotBlank(postList.postName())) {
                wrapper.like(Post::getPostName, postList.postName());
            }
        }
        return postMapper.selectPage(postPage, wrapper).convert(post ->
                PostVo.PostListVo.from(post, dictRuntimeService.getLabel(DictTypeCode.SYS_POST_TYPE, post.getPostType())));
    }

    @Transactional
    public void insert(PostDto.PostInsert postInsert) {
        validatePostType(postInsert.postType());
        if (checkPostCodeUnique(postInsert.postCode()) != null) {
            throw new BusinessException("岗位编码已存在");
        }
        if (checkPostNameUnique(postInsert.postName()) != null) {
            throw new BusinessException("岗位名称已存在");
        }
        Post post = new Post(postInsert);
        int rows = postMapper.insert(post);
        if (rows <= 0) {
            throw new BusinessException("新增岗位失败");
        }
    }

    @Transactional
    public void updateById(PostDto.PostUpdate postUpdate) {
        validatePostType(postUpdate.postType());
        Post oldCodePost = checkPostCodeUnique(postUpdate.postCode());
        if (oldCodePost != null && !oldCodePost.getPostId().equals(postUpdate.postId())) {
            throw new BusinessException("岗位编码已存在");
        }
        Post oldNamePost = checkPostNameUnique(postUpdate.postName());
        if (oldNamePost != null && !oldNamePost.getPostId().equals(postUpdate.postId())) {
            throw new BusinessException("岗位名称已存在");
        }
        int rows = postMapper.updateById(new Post(postUpdate));
        if (rows <= 0) {
            throw new BusinessException("岗位不存在或修改失败");
        }
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的岗位");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>().in(User::getPostId, idList));
        if (userCount != null && userCount > 0) {
            throw new BusinessException("岗位已分配给用户，不能删除");
        }
        int rows = postMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("岗位不存在或删除失败");
        }
    }

    public Post selectById(String postId) {
        return postMapper.selectById(postId);
    }

    public Post checkPostCodeUnique(String postCode) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getPostCode, postCode);
        return postMapper.selectOne(wrapper);
    }

    public Post checkPostNameUnique(String postName) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getPostName, postName);
        return postMapper.selectOne(wrapper);
    }

    @Transactional
    public void updatePostEnabled(PostDto.UpdateEnabled updateEnabled) {
        Post oldPost = postMapper.selectById(updateEnabled.postId());
        if (oldPost == null) {
            throw new BusinessException("岗位不存在");
        }
        oldPost.setIsEnabled(updateEnabled.isEnabled());
        int rows = postMapper.updateById(oldPost);
        if (rows <= 0) {
            throw new BusinessException("修改岗位状态失败");
        }
    }

    private void validatePostType(Integer postType) {
        // 岗位类型需为字典合法值。
        dictRuntimeService.validateValue(DictTypeCode.SYS_POST_TYPE, postType, "岗位类型");
    }
}
