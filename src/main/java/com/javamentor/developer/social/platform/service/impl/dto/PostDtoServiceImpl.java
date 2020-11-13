package com.javamentor.developer.social.platform.service.impl.dto;

import com.javamentor.developer.social.platform.dao.abstracts.dto.PostDtoDao;
import com.javamentor.developer.social.platform.models.dto.PostDto;
import com.javamentor.developer.social.platform.models.dto.TagDto;
import com.javamentor.developer.social.platform.models.dto.comment.CommentDto;
import com.javamentor.developer.social.platform.service.abstracts.dto.PostDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostDtoServiceImpl implements PostDtoService {

    private final PostDtoDao postDtoDao;

    @Autowired
    public PostDtoServiceImpl(PostDtoDao postDtoDao) {
        this.postDtoDao = postDtoDao;
    }

    @Override
    public List<PostDto> getPostsByTag(String text) {
        return postDtoDao.getPostsByTag(text);
    }

    @Override
    public List<PostDto> getPostsByUserId(Long id) {
        return postDtoDao.getPostsByUserId(id);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long id) {
        return postDtoDao.getCommentsByPostId(id);
    }

    @Override
    public List<TagDto> getAllTags() {
        return postDtoDao.getAllTags();
    }


    @Override
    public List<PostDto> getAllPosts() {
        return postDtoDao.getAllPosts();
    }

    @Override
    public List<PostDto> getPostById(Long postId) {
        return postDtoDao.getPostById(postId);
    }
}
