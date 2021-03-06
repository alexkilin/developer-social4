package com.javamentor.developer.social.platform.service.abstracts.model.album;

import com.javamentor.developer.social.platform.models.entity.album.AlbumVideo;
import com.javamentor.developer.social.platform.service.abstracts.GenericService;

import java.util.Optional;

public interface AlbumVideoService extends GenericService<AlbumVideo, Long> {

    AlbumVideo createAlbumVideosWithOwner(AlbumVideo albumVideo);

    Optional<AlbumVideo> getByIdWithVideos(Long id);
}
