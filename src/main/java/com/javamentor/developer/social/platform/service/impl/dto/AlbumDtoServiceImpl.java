package com.javamentor.developer.social.platform.service.impl.dto;

import com.javamentor.developer.social.platform.dao.abstracts.dto.AlbumDtoDao;
import com.javamentor.developer.social.platform.models.dto.media.AlbumDto;
import com.javamentor.developer.social.platform.models.entity.media.MediaType;
import com.javamentor.developer.social.platform.service.abstracts.dto.AlbumDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumDtoServiceImpl implements AlbumDtoService {

    private final AlbumDtoDao albumDtoDao;

    @Autowired
    public AlbumDtoServiceImpl(AlbumDtoDao albumDtoDao) {
        this.albumDtoDao = albumDtoDao;
    }

    @Override
    public List<AlbumDto> getAllByTypeAndUserId(MediaType type, Long userId) {
        return albumDtoDao.getAllByTypeAndUserId(type, userId);
    }

    @Override
    public Optional<AlbumDto> getById(Long id) {
        return albumDtoDao.getById(id);
    }


}
