package com.javamentor.developer.social.platform.dao.impl.dto.page.pageAudioImpl;

import com.javamentor.developer.social.platform.dao.abstracts.dto.AudioDtoDao;
import com.javamentor.developer.social.platform.dao.abstracts.dto.page.PageDtoDao;
import com.javamentor.developer.social.platform.models.dto.media.music.AudioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Component("getAudioOfAlbum")
public class PageDtoGetAudioOfAlbumDaoImpl implements PageDtoDao<AudioDto> {
    @PersistenceContext
    private EntityManager entityManager;
    private final AudioDtoDao audioDtoDao;

    @Autowired
    public PageDtoGetAudioOfAlbumDaoImpl(AudioDtoDao audioDtoDao) {
        this.audioDtoDao = audioDtoDao;
    }

    @Override
    public List<AudioDto> getItems(Map<String, Object> parameters) {
        return audioDtoDao.getAudioOfAlbum((String) parameters.get("album"),
                (int) parameters.get("currentPage"),
                (int) parameters.get("itemsOnPage"));
    }

    @Override
    public Long getCount(Map<String, Object> parameters) {
        return entityManager.createQuery(
                "select count (c) from Audios as c where c.album = :album",
                Long.class
        ).setParameter("album", parameters.get("album"))
                .getSingleResult();
    }
}
