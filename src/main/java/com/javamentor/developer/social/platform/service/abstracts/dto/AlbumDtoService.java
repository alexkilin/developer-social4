package com.javamentor.developer.social.platform.service.abstracts.dto;

import com.javamentor.developer.social.platform.models.dto.AlbumCreateDto;
import com.javamentor.developer.social.platform.models.dto.AlbumDto;
import com.javamentor.developer.social.platform.models.entity.media.MediaType;

import java.util.List;
import java.util.Optional;

public interface AlbumDtoService {

    List<AlbumDto> getAllByUserId(Long id);
    List<AlbumDto> getAllByTypeAndUserId(MediaType type, Long userId);
    AlbumDto createAlbumImage(AlbumCreateDto albumCreateDto);
    Optional<AlbumDto> getById(Long id);
}
