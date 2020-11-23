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

@Component("getVideoOfAuthor")
public class PageDtoGetVideoOfAuthorDaoImpl implements PageDtoDao<VideoDto> {
    @PersistenceContext
    private EntityManager entityManager;
    private final VideoDtoDao videoDtoDao;

    @Autowired
    public PageDtoGetVideoOfAuthorDaoImpl(VideoDtoDao videoDtoDao) {
        this.videoDtoDao = videoDtoDao;
    }

    @Override
    public List<VideoDto> getItems(Map<String, Object> parameters) {
        return videoDtoDao.getVideoOfAuthor((String) parameters.get("author"),
                (int) parameters.get("currentPage"),
                (int) parameters.get("itemsOnPage"));
    }

    @Override
    public Long getCount(Map<String, Object> parameters) {
        return entityManager.createQuery(
                "select count (v) FROM Videos as v " +
                        "JOIN Media as m ON v.media.id = m.id WHERE v.author = :author",
                Long.class
        ).setParameter("author", parameters.get("author"))
                .getSingleResult();
    }
}
