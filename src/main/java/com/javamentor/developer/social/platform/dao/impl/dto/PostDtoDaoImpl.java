package com.javamentor.developer.social.platform.dao.impl.dto;

import com.javamentor.developer.social.platform.dao.abstracts.dto.PostDtoDao;
import com.javamentor.developer.social.platform.models.dto.MediaPostDto;
import com.javamentor.developer.social.platform.models.dto.PostDto;
import com.javamentor.developer.social.platform.models.dto.TagDto;
import com.javamentor.developer.social.platform.models.dto.UserDto;
import com.javamentor.developer.social.platform.models.dto.comment.CommentDto;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Repository
public class PostDtoDaoImpl implements PostDtoDao {

    final
    EntityManager entityManager;

    @Autowired
    public PostDtoDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PostDto> getAllPosts() {
        List<PostDto> postDtoList = entityManager.createQuery("select " +
                "p.id, " +                  //0
                "p.title, " +               //1
                "p.text, " +                //2
                "p.persistDate, " +         //3
                "p.lastRedactionDate, " +   //4
                "u.userId, " +              //5
                "u.firstName, " +           //6
                "u.lastName, " +            //7
                "u.avatar, " +              //8
                "(select count(bm.id) from Bookmark as bm where bm.post.id = p.id), " +     //9
                "(select count(l.id) from PostLike as l where l.post.id = p.id), " +        //10
                "(select count(c.id) from PostComment as c where c.post.id = p.id), " +     //11
                "p.repostPerson.size " +    //12
                "from Post as p " +
                "join p.user as u ")
                .unwrap(Query.class)
                .setResultTransformer(new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] objects, String[] strings) {
                        return PostDto.builder()
                                .id((Long) objects[0])
                                .title((String) objects[1])
                                .text((String) objects[2])
                                .userId((Long) objects[5])
                                .firstName((String) objects[6])
                                .lastName((String) objects[7])
                                .avatar((String) objects[8])
                                .persistDate((LocalDateTime) objects[3])
                                .lastRedactionDate((LocalDateTime) objects[4])
                                .bookmarkAmount((Long) objects[9])
                                .likeAmount((Long) objects[10])
                                .commentAmount((Long) objects[11])
                                .shareAmount(Long.valueOf((Integer) objects[12]))
                                .build();
                    }

                    @Override
                    public List transformList(List list) {
                        return list;
                    }
                })
                .getResultList();
        return postDtoList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PostDto> getPostsByTag(String text) {
        List<PostDto> postDtoList = new ArrayList<>();

        try {
            postDtoList = entityManager.createQuery("select " +
                    "p.id, " +
                    "p.title, " +
                    "p.text, " +
                    "p.persistDate, " +
                    "p.lastRedactionDate, " +
                    "u.userId, " +
                    "u.firstName, " +
                    "u.lastName, " +
                    "u.avatar, " +
                    "m.mediaType, " +
                    "m.url, " +
                    "t.id," +
                    "t.text " +
                    "from Post as p " +
                    "join p.user as u " +
                    "join p.media as m " +
                    "left join p.tags as t " +
                    "where t.text = :text")
                    .setParameter("text", text)
                    .unwrap(Query.class)
                    .setResultTransformer(new ResultTransformer() {
                        @Override
                        public Object transformTuple(Object[] objects, String[] strings) {
                            MediaPostDto mediaPostDto = MediaPostDto.builder()
                                    .userId((Long) objects[5])
                                    .mediaType(objects[9] == null ? "null" : objects[9].toString())
                                    .url((String) objects[10])
                                    .build();
                            List<MediaPostDto> mediaPostDtoList = new ArrayList<>();
                            mediaPostDtoList.add(mediaPostDto);
                            TagDto tagDto = TagDto.builder()
                                    .id(objects[11] == null ? 0 : (Long) objects[11])
                                    .text(objects[12] == null ? "null" : (String) objects[12])
                                    .build();
                            List<TagDto> tagDtoList = new ArrayList<>();
                            tagDtoList.add(tagDto);
                            return PostDto.builder()
                                    .id((Long) objects[0])
                                    .title((String) objects[1])
                                    .text((String) objects[2])
                                    .userId((Long) objects[5])
                                    .firstName((String) objects[6])
                                    .lastName((String) objects[7])
                                    .avatar((String) objects[8])
                                    .media(mediaPostDtoList)
                                    .tags(tagDtoList)
                                    .persistDate((LocalDateTime) objects[3])
                                    .lastRedactionDate((LocalDateTime) objects[4])
                                    .build();
                        }

                        @Override
                        public List transformList(List list) {
                            Map<Long, PostDto> result = new TreeMap<>(Comparator.reverseOrder());
                            for (Object obj : list) {
                                PostDto postDto = (PostDto) obj;
                                if (result.containsKey(postDto.getId())) {
                                    result.get(postDto.getId()).getMedia().addAll(postDto.getMedia());
                                } else {
                                    result.put(postDto.getId(), postDto);
                                }
                            }
                            return new ArrayList<>(result.values());
                        }
                    })
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postDtoList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PostDto> getPostsByUserId(Long id) {
        List<PostDto> postDtoList = entityManager.createQuery("select " +
                "p.id, " +                      //0
                "p.title, " +                   //1
                "p.text, " +                    //2
                "p.persistDate, " +             //3
                "p.lastRedactionDate, " +       //4
                "u.userId, " +                  //5
                "u.firstName, " +               //6
                "u.lastName, " +                //7
                "u.avatar, " +                  //8
                "m.mediaType, " +               //9
                "m.url, " +                     //10
                "m.user.userId," +              //11
                "t.id," +                       //12
                "t.text, " +                    //13
                "(select count(bm.id) from Bookmark as bm where bm.post.id = p.id), " +     //14
                "(select count(l.id) from PostLike as l where l.post.id = p.id), " +        //15
                "(select count(c.id) from PostComment as c where c.post.id = p.id), " +     //16
                "p.repostPerson.size " +        //17
                "from Post as p " +
                "join p.user as u " +
                "left join p.media as m " +
                "left join p.tags as t " +
                "where u.userId = :userId")
                .setParameter("userId", id)
                .unwrap(Query.class)
                .setResultTransformer(new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] objects, String[] strings) {
                        List<MediaPostDto> mediaPostDtoList = new ArrayList<>();
                        MediaPostDto mediaPostDto = null;

                        if (objects[9] != null || objects[10] != null || objects[11] != null) {
                            mediaPostDto = MediaPostDto.builder()
                                    .userId(objects[11] == null ? 0 : (Long) objects[11])
                                    .mediaType(objects[9] == null ? "null" : objects[9].toString())
                                    .url(objects[10] == null ? "null" : objects[10].toString())
                                    .build();
                        }
                        mediaPostDtoList.add(mediaPostDto);

                        List<TagDto> tagDtoList = new ArrayList<>();

                        if (objects[12] != null || objects[13] != null) {
                            TagDto tagDto = TagDto.builder()
                                    .id(objects[12] == null ? 0 : (Long) objects[12])
                                    .text(objects[13] == null ? "null" : (String) objects[13])
                                    .build();
                            tagDtoList.add(tagDto);
                        }

                        return PostDto.builder()
                                .id((Long) objects[0])
                                .title((String) objects[1])
                                .text((String) objects[2])
                                .userId((Long) objects[5])
                                .firstName((String) objects[6])
                                .lastName((String) objects[7])
                                .avatar((String) objects[8])
                                .media(mediaPostDtoList)
                                .tags(tagDtoList)
                                .persistDate((LocalDateTime) objects[3])
                                .lastRedactionDate((LocalDateTime) objects[4])
                                .bookmarkAmount((Long) objects[14])
                                .likeAmount((Long) objects[15])
                                .commentAmount((Long) objects[16])
                                .shareAmount(Long.valueOf((Integer) objects[17]))
                                .build();
                    }

                    @Override
                    public List transformList(List list) {
                        Map<Long, PostDto> result = new TreeMap<>(Comparator.reverseOrder());
                        for (Object obj : list) {
                            PostDto postDto = (PostDto) obj;
                            if (result.containsKey(postDto.getId())) {
                                List <MediaPostDto> mediaDtoList = result.get(postDto.getId()).getMedia();
                                mediaDtoList.removeAll(postDto.getMedia());
                                mediaDtoList.addAll(postDto.getMedia());

                                List <TagDto> tagDtoList = result.get(postDto.getId()).getTags();
                                tagDtoList.removeAll(postDto.getTags());
                                tagDtoList.addAll(postDto.getTags());
                            } else {
                                result.put(postDto.getId(), postDto);
                            }
                        }
                        return new ArrayList<>(result.values());
                    }
                })
                .getResultList();

        return postDtoList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CommentDto> getCommentsByPostId(Long id) {
        Query queryCommentsForPost = (Query) entityManager.createQuery(
                "SELECT " +
                        "c.id, " +
                        "c.persistDate, " +
                        "c.lastRedactionDate, " +
                        "c.comment, " +//3
                        "(SELECT u.lastName FROM User u WHERE c.user.userId = u.userId), " +
                        "(SELECT u.firstName FROM User u WHERE c.user.userId = u.userId), " +
                        "(SELECT u.userId FROM User u WHERE c.user.userId = u.userId)," +
                        "(SELECT u.avatar FROM User u WHERE c.user.userId = u.userId)" +
                        "FROM Post p " +
                        "LEFT JOIN PostComment pc on p.id = pc.post.id " +
                        "LEFT JOIN Comment c on pc.comment.id = c.id " +
                        "WHERE p.id = :paramId")
                .setParameter("paramId", id);
        return queryCommentsForPost.unwrap(Query.class).setResultTransformer(new ResultTransformer() {

            @Override
            public Object transformTuple(Object[] objects, String[] strings) {
                UserDto userDto = UserDto.builder()
                        .userId((Long) objects[6])
                        .firstName((String) objects[5])
                        .lastName((String) objects[4])
                        .avatar((String) objects[7])
                        .build();
                return CommentDto.builder()
                        .userDto(userDto)
                        .persistDate((LocalDateTime) objects[1])
                        .lastRedactionDate((LocalDateTime) objects[2])
                        .id((Long) objects[0])
                        .comment((String) objects[3])
                        .build();
            }

            @Override
            public List transformList(List list) {
                Map<Long, CommentDto> result = new TreeMap<>();
                for (Object obj : list) {
                    CommentDto commentDto = (CommentDto) obj;
                    if (commentDto.getId() != null) {
                        result.put(commentDto.getId(), commentDto);
                    }
                }
                return new ArrayList<>(result.values());
            }
        }).getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MediaPostDto> getMediasByPostId(Long id) {
        Query queryMediasForPost = (Query) entityManager.createQuery(
                "SELECT " +
                        "m.mediaType, " +
                        "m.url, " +
                        "m.user.userId " +
                        "FROM Post p " +
                        "LEFT JOIN p.media m " +
                        "WHERE p.id = :paramId")
                .setParameter("paramId", id);
        return queryMediasForPost.unwrap(Query.class).setResultTransformer(new ResultTransformer() {

            @Override
            public Object transformTuple(Object[] objects, String[] strings) {
                return MediaPostDto.builder()
                        .url((String) objects[1])
                        .mediaType(objects[0] == null ? null : objects[0].toString())
                        .userId((Long) objects[2])
                        .build();
            }

            @Override
            public List transformList(List list) {
                return list;
            }
        }).getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TagDto> getTagsByPostId(Long id) {
        Query queryTagsForPost = (Query) entityManager.createQuery(
                "SELECT " +
                        "t.text, " +
                        "t.id " +
                        "FROM Post p " +
                        "LEFT JOIN p.tags t " +
                        "WHERE p.id = :paramId")
                .setParameter("paramId", id);
        return queryTagsForPost.unwrap(Query.class).setResultTransformer(new ResultTransformer() {

            @Override
            public Object transformTuple(Object[] objects, String[] strings) {
                return TagDto.builder()
                        .id((Long) objects[1])
                        .text((String) objects[0])
                        .build();
            }

            @Override
            public List transformList(List list) {
                return list;
            }
        }).getResultList();
    }
}
