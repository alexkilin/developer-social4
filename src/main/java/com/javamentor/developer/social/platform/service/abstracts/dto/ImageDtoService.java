package com.javamentor.developer.social.platform.service.abstracts.dto;

import com.javamentor.developer.social.platform.models.dto.media.image.ImageDto;

import java.util.List;
import java.util.Optional;

public interface ImageDtoService {

    List<ImageDto> getAllByUserId(int offset, int limit, Long id);
    List<ImageDto> getAllByAlbumId(int offset, int limit, Long id);
    Optional<ImageDto> getById(Long id);


}
