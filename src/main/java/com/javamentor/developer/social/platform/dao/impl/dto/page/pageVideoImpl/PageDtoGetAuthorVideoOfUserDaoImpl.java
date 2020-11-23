package com.javamentor.developer.social.platform.dao.impl.dto.page.pageVideoImpl;

import com.javamentor.developer.social.platform.dao.abstracts.dto.VideoDtoDao;
import com.javamentor.developer.social.platform.dao.abstracts.dto.page.PageDtoDao;
import com.javamentor.developer.social.platform.models.dto.media.video.VideoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Component("getAuthorVideoOfUser")
public class PageDtoGetAuthorVideoOfUserDaoImpl implements PageDtoDao<VideoDto> {
    @PersistenceContext
    private EntityManager entityManager;
    private final VideoDtoDao videoDtoDao;

    @Autowired
    public PageDtoGetAuthorVideoOfUserDaoImpl(VideoDtoDao videoDtoDao) {
        this.videoDtoDao = videoDtoDao;
    }

    @Override
    public List<VideoDto> getItems(Map<String, Object> parameters) {
        return videoDtoDao.getAuthorVideoOfUser((Long) parameters.get("userId"),
                (String) parameters.get("author"),
                (int) parameters.get("currentPage"),
                (int) parameters.get("itemsOnPage"));
    }

    @Override
    public Long getCount(Map<String, Object> parameters) {
        return entityManager.createQuery(
                "select count (v) FROM User u " +
                        "join u.videos v where u.userId =:userId " +
                        "and v.author =:author",
                Long.class
        ).setParameter("userId", parameters.get("userId"))
                .setParameter("author", parameters.get("author"))
                .getSingleResult();
    }
}
