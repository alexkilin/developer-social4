package com.javamentor.developer.social.platform.dao.impl.dto.page.playlist;

import com.javamentor.developer.social.platform.dao.abstracts.dto.PlaylistDtoDao;
import com.javamentor.developer.social.platform.dao.abstracts.dto.page.PaginationDao;
import com.javamentor.developer.social.platform.models.dto.media.music.AudioDto;
import com.javamentor.developer.social.platform.models.dto.media.music.PlaylistGetDto;
import lombok.SneakyThrows;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("getPlaylistsOfUser")

public class PaginationGetByUserIdDaoImpl implements PaginationDao<PlaylistGetDto> {
    @PersistenceContext
    private EntityManager entityManager;

    public PaginationGetByUserIdDaoImpl() {}

    @Override
    @SuppressWarnings("unchecked")
    public List<PlaylistGetDto> getItems(Map<String, Object> parameters) {
        Long userId = (Long) parameters.get("userId");
        int currentPage = (int) parameters.get("currentPage");
        int itemsOnPage = (int) parameters.get("itemsOnPage");

        List<PlaylistGetDto> list = entityManager.createQuery("SELECT " +
                "p.id," +
                "p.name, " +
                "p.image," +
                "p.ownerUser.userId," +
                "p.persistDateTime " +
                "FROM Playlist as p " +
                "WHERE p.ownerUser.userId = :userId")
                .setParameter("userId", userId)
                .setFirstResult((currentPage - 1) * itemsOnPage)
                .setMaxResults(itemsOnPage)
                .unwrap(Query.class)
                .setResultTransformer(new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] objects, String[] strings) {

                        return PlaylistGetDto.builder()
                                .id((Long) objects[0])
                                .name((String) objects[1])
                                .image((String) objects[2])
                                .ownerUserId((Long) objects[3])
                                .persistDateTime((LocalDateTime) objects[4])
                                .build();
                    }

                    @Override
                    public List transformList(List list) {
                        return list;
                    }
                })
                .getResultList();
        return list;
    }

    @Override
    public Long getCount(Map<String, Object> parameters) {
        return entityManager.createQuery(
                "select count (p) from Playlist p where p.ownerUser.userId = :userId ",
                Long.class
        ).setParameter("userId", parameters.get("userId"))
                .getSingleResult();
    }
}

