package com.javamentor.developer.social.platform.service.abstracts.model.media;

import com.javamentor.developer.social.platform.models.entity.media.Playlist;
import com.javamentor.developer.social.platform.service.abstracts.GenericService;

import java.util.List;
import java.util.Optional;

public interface PlaylistService extends GenericService<Playlist, Long> {
    boolean userHasPlaylist(Long userId, Long playlistId);
    Optional<Playlist> getOptionalById(Long id);
    Optional<Playlist> getPlaylistByNameAndUserId (long userID, String playlistName);
}
