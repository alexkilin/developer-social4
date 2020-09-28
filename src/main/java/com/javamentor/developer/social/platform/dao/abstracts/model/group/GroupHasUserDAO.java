package com.javamentor.developer.social.platform.dao.abstracts.model.group;

import com.javamentor.developer.social.platform.dao.abstracts.GenericDao;
import com.javamentor.developer.social.platform.models.entity.group.GroupHasUser;

public interface GroupHasUserDAO extends GenericDao<GroupHasUser, Long> {

    boolean verificationUserInGroup(Long groupId, Long userId);

     int deleteUserById(Long groupId, Long id);
}
