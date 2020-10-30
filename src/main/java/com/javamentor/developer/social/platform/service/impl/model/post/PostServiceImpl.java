package com.javamentor.developer.social.platform.service.impl.model.post;

import com.javamentor.developer.social.platform.dao.abstracts.model.post.PostDao;
import com.javamentor.developer.social.platform.models.entity.media.Media;
import com.javamentor.developer.social.platform.models.entity.post.Post;
import com.javamentor.developer.social.platform.models.entity.post.Tag;
import com.javamentor.developer.social.platform.models.entity.user.User;
import com.javamentor.developer.social.platform.service.abstracts.model.media.MediaService;
import com.javamentor.developer.social.platform.service.abstracts.model.post.PostService;
import com.javamentor.developer.social.platform.service.abstracts.model.post.TagService;
import com.javamentor.developer.social.platform.service.abstracts.model.user.UserService;
import com.javamentor.developer.social.platform.service.impl.GenericServiceAbstract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl extends GenericServiceAbstract<Post, Long> implements PostService {


    private final UserService userService;
    private final MediaService mediaService;
    private final TagService tagService;


    @Autowired
    public PostServiceImpl(PostDao dao, UserService userService, MediaService mediaService, TagService tagService) {
        super(dao);
        this.userService = userService;
        this.mediaService = mediaService;
        this.tagService = tagService;
    }

    @Override
    public void create(Post entity) {
        Set<Media> mediaSet = new HashSet<>();

        if (entity.getMedia() != null) {
            for (Media media : entity.getMedia()) {

                Optional<User> userOptional = userService.getById(media.getUser().getUserId());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    media.setUser(user);
                    mediaSet.add(media);
                    mediaService.create(media);
                }
            }
        }

        if (entity.getTags() != null) {
            Set<Tag> tagSet = new HashSet<>();

            for (Tag tag : entity.getTags()) {
                tag.setText(tag.getText().toLowerCase());
                Optional<Tag> tagFromDB = tagService.getTagByText(tag.getText());

                if (!tagFromDB.isPresent()) {
                    tagService.create(tag);
                    tagSet.add(tag);
                } else {
                    tagSet.add(tagFromDB.get());
                }
            }

            entity.setTags(tagSet);
        }


        Optional<User> userOptional = userService.getById(entity.getUser().getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            entity.setUser(user);
            entity.setMedia(mediaSet);
            super.create(entity);
        }

        Optional<User> user = userService.getById(entity.getUser().getUserId());
        entity.setUser(user.get());
        entity.setMedia(mediaSet);
        super.create(entity);
    }


}
