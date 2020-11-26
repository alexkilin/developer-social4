package com.javamentor.developer.social.platform.dao.impl.dto.page.post;

import com.javamentor.developer.social.platform.dao.abstracts.dto.page.PaginationDao;
import com.javamentor.developer.social.platform.models.dto.PostDto;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component("getAllBookmarkedPosts")
public class PaginationGetAllBookmarkedPostsDaoImpl implements PaginationDao<PostDto> {
    @PersistenceContext
    private EntityManager entityManager;

    public PaginationGetAllBookmarkedPostsDaoImpl() {
    }

    @Override
    public List<PostDto> getItems(Map<String, Object> parameters) {
        int currentPage = (int) parameters.get("currentPage");
        int itemsOnPage = (int) parameters.get("itemsOnPage");
        List<PostDto> postDtoList = entityManager.createQuery(
                "select p.id, " +
                        "p.title, " +
                        "p.text, " +
                        "u.userId, " +
                        "u.firstName, " +
                        "u.lastName, " +
                        "u.avatar, " +
                        "p.lastRedactionDate, " +
                        "p.persistDate, " +
                        "(select count(b.id) from Bookmark as b where b.post.id = p.id), " +
                        "(select count(l.id) from PostLike as l where l.post.id = p.id), " +
                        "(select count(c.id) from PostComment as c where c.post.id = p.id), " +
                        "(select count(r.id) from Repost as r where r.post.id = p.id), " +
                        "case when exists " +
                        "(select pl from PostLike as pl where pl.like.user.userId = :userPrincipalId and pl.post.id = p.id)" +
                        "then true else false end," +
                        "case when exists " +
                        "(select bm from Bookmark as bm where bm.user.userId = :userPrincipalId and bm.post.id = p.id)" +
                        "then true else false end," +
                        "case when exists " +
                        "(select rp from Repost as rp where rp.user.userId = :userPrincipalId and rp.post.id = p.id)" +
                        "then true else false end " +
                        "from Bookmark as bm " +
                        "join bm.user as u " +
                        "join bm.post as p " +
                        "where u.userId = :userPrincipalId")
                .setParameter("userPrincipalId", parameters.get("userPrincipalId"))
                .setFirstResult((currentPage - 1) * itemsOnPage)
                .setMaxResults(itemsOnPage)
                .unwrap(Query.class)
                .setResultTransformer(
                        new ResultTransformer() {

                            @Override
                            public Object transformTuple(Object[] objects, String[] strings) {
                                return PostDto.builder()
                                        .id((Long) objects[0])
                                        .title((String) objects[1])
                                        .text((String) objects[2])
                                        .userId((Long) objects[3])
                                        .firstName((String) objects[4])
                                        .lastName((String) objects[5])
                                        .avatar((String) objects[6])
                                        .lastRedactionDate((LocalDateTime) objects[7])
                                        .persistDate((LocalDateTime) objects[8])
                                        .bookmarkAmount((Long) objects[9])
                                        .likeAmount((Long) objects[10])
                                        .commentAmount((Long) objects[11])
                                        .shareAmount((Long) objects[12])
                                        .isLiked((Boolean) objects[13])
                                        .isBookmarked((Boolean) objects[14])
                                        .isShared((Boolean) objects[15])
                                        .build();
                            }

                            @Override
                            public List transformList(List list) {
                                return list;
                            }
                        }).getResultList();
        return postDtoList;
    }

    @Override
    public Long getCount(Map<String, Object> parameters) {
        return entityManager.createQuery(
                "select count (bm) from Bookmark bm where bm.user.userId = :userPrincipalId",
                Long.class
        ).setParameter("userPrincipalId", parameters.get("userPrincipalId"))
                .getSingleResult();
    }
}
