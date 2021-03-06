package com.javamentor.developer.social.platform.service.impl.model.group;

import com.javamentor.developer.social.platform.dao.abstracts.model.group.GroupCategoryDao;
import com.javamentor.developer.social.platform.models.entity.group.GroupCategory;
import com.javamentor.developer.social.platform.service.abstracts.model.group.GroupCategoryService;
import com.javamentor.developer.social.platform.service.impl.GenericServiceAbstract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GroupCategoryServiceImpl extends GenericServiceAbstract<GroupCategory, Long> implements GroupCategoryService {

    private final GroupCategoryDao groupCategoryDao;

    @Autowired
    public GroupCategoryServiceImpl( GroupCategoryDao dao ) {
        super(dao);
        this.groupCategoryDao = dao;
    }

    @Override
    @Transactional
    public Optional<GroupCategory> getGroupCategoryByName( String category ) {
        return groupCategoryDao.getGroupCategoryByName(category);
    }

}
