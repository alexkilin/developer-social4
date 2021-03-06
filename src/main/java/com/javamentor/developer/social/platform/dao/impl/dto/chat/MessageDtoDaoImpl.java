package com.javamentor.developer.social.platform.dao.impl.dto.chat;

import com.javamentor.developer.social.platform.dao.abstracts.dto.chat.MessageDtoDao;
import com.javamentor.developer.social.platform.models.dto.chat.MediaDto;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MessageDtoDaoImpl implements MessageDtoDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<MediaDto> getAllMediasOfMessage(List<Long> messageId){
        return em.createQuery("select " +
                "m.id, " +
                "m.url, " +
                "m.mediaType , " +
                "m.persistDateTime, " +
                "mes.id " +
                "from Message mes join mes.media as m " +
                "where mes.id in (:messageId) " +
                "ORDER BY m.id ASC, mes.id ASC")
                .setParameter("messageId", messageId)
                .unwrap(Query.class).setResultTransformer(
                        new ResultTransformer() {
                            @Override
                            public Object transformTuple(Object[] objects, String[] strings) {
                                if (objects[0] != null && objects[1] != null) {
                                    return MediaDto.builder()
                                            .id((Long) objects[0])
                                            .url((String) objects[1])
                                            .mediaType(String.valueOf(objects[2]))
                                            .persistDateTime((LocalDateTime) objects[3])
                                            .messageId((Long) objects[4])
                                            .build();
                                } else return null;
                            }

                            @Override
                            public List transformList(List list) {
                                return list;
                            }
                        }
                ).getResultList();
    }
}
