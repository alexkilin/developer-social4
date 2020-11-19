package com.javamentor.developer.social.platform.service.impl.dto;

import com.javamentor.developer.social.platform.dao.abstracts.dto.AudioDtoDao;
import com.javamentor.developer.social.platform.dao.abstracts.dto.page.PageDtoDao;
import com.javamentor.developer.social.platform.models.dto.media.music.AudioDto;
import com.javamentor.developer.social.platform.service.abstracts.dto.AudioDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AudioDtoServiceImpl implements AudioDtoService {

    private final AudioDtoDao audioDtoDao;

    @Autowired
    public AudioDtoServiceImpl(AudioDtoDao audioDtoDao) {
        this.audioDtoDao = audioDtoDao;
    }

    @Override
    public List<AudioDto> getAudioOfAuthor(String author, int currentPage, int itemsOnPage) {

        return audioDtoDao.getAudioOfAuthor(author, currentPage, itemsOnPage);
    }

    @Override
    public List<AudioDto> getAudioOfName(String name) {
        return audioDtoDao.getAudioOfName(name);
    }

    @Override
    public List<AudioDto> getAudioOfAlbum(String album) {
        return audioDtoDao.getAudioOfAlbum(album);
    }

    @Override
    public List<AudioDto> getAudioOfUser(Long userId) {
        return audioDtoDao.getAudioOfUser(userId);
    }

    @Override
    public List<AudioDto> getPartAudioOfUser(Long userId, int currentPage, int itemsOnPage) {
        return audioDtoDao.getPartAudioOfUser(userId,currentPage,itemsOnPage);
    }

    @Override
    public List<AudioDto> getAuthorAudioOfUser(Long userId, String author) {
        return audioDtoDao.getAuthorAudioOfUser(userId, author);
    }

    @Override
    public List<AudioDto> getAlbumAudioOfUser(Long userId, String album) {
        return audioDtoDao.getAlbumAudioOfUser(userId, album);
    }

    @Override
    public List<AudioDto> getAudioFromAlbumOfUser(Long albumId) {
        return audioDtoDao.getAudioFromAlbumOfUser(albumId);
    }

    @Override
    public List<AudioDto> getAudioFromPlaylist(Long playlistId, int offset, int limit) {
        return audioDtoDao.getAudioFromPlaylist(playlistId, offset, limit);
    }

    @Override
    public List<AudioDto> getPartAudio(int currentPage, int itemsOnPage) {
        return audioDtoDao.getPartAudio(currentPage, itemsOnPage);
    }

}
