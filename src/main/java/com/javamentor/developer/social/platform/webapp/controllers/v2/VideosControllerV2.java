package com.javamentor.developer.social.platform.webapp.controllers.v2;

import com.javamentor.developer.social.platform.models.dto.media.AlbumDto;
import com.javamentor.developer.social.platform.models.dto.media.video.AlbumVideoDto;
import com.javamentor.developer.social.platform.models.dto.media.video.VideoDto;
import com.javamentor.developer.social.platform.models.entity.album.AlbumVideo;
import com.javamentor.developer.social.platform.models.entity.media.MediaType;
import com.javamentor.developer.social.platform.models.entity.media.Videos;
import com.javamentor.developer.social.platform.models.entity.user.User;
import com.javamentor.developer.social.platform.models.util.OnCreate;
import com.javamentor.developer.social.platform.service.abstracts.dto.AlbumVideoDtoService;
import com.javamentor.developer.social.platform.service.abstracts.dto.VideoDtoService;
import com.javamentor.developer.social.platform.service.abstracts.model.album.AlbumService;
import com.javamentor.developer.social.platform.service.abstracts.model.album.AlbumVideoService;
import com.javamentor.developer.social.platform.service.abstracts.model.media.VideosService;
import com.javamentor.developer.social.platform.service.abstracts.model.user.UserService;
import com.javamentor.developer.social.platform.webapp.converters.AlbumConverter;
import com.javamentor.developer.social.platform.webapp.converters.AlbumVideoConverter;
import com.javamentor.developer.social.platform.webapp.converters.VideoConverter;
import io.swagger.annotations.*;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Validated
@RestController
@RequestMapping(value = "/api/v2/video", produces = "application/json")
@SuppressWarnings("deprecation")
@Api(value = "VideosApi-v2", description = "Операции над видеозаписями")
public class VideosControllerV2 {

    private final VideosService videosService;
    private final VideoConverter videoConverter;
    private final VideoDtoService videoDtoService;
    private final UserService userService;
    private final AlbumService albumService;
    private final AlbumVideoService albumVideoService;
    private final AlbumVideoConverter albumVideoConverter;
    private final AlbumVideoDtoService albumVideoDtoService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public VideosControllerV2(VideosService videosService, VideoConverter videoConverter, VideoDtoService videoDtoService,
                              UserService userService, AlbumService albumService, AlbumVideoService albumVideoService,
                              AlbumVideoConverter albumVideoConverter, AlbumVideoDtoService albumVideoDtoService) {
        this.videosService = videosService;
        this.videoConverter = videoConverter;
        this.videoDtoService = videoDtoService;
        this.userService = userService;
        this.albumService = albumService;
        this.albumVideoService = albumVideoService;
        this.albumVideoConverter = albumVideoConverter;
        this.albumVideoDtoService = albumVideoDtoService;
    }

    @ApiOperation(value = "Получение некоторого количества видео")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Несколько видео получено", responseContainer = "List", response = VideoDto.class)})
    @GetMapping(value = "", params = {"currentPage", "itemsOnPage"})
    public ResponseEntity<List<VideoDto>> getPartVideos(@ApiParam(value = "Текущая страница", example = "0") @RequestParam("currentPage") int currentPage,
                                                        @ApiParam(value = "Количество данных на страницу", example = "15") @RequestParam("itemsOnPage") int itemsOnPage) {
        logger.info(String.format("Видео начиная c объекта номер %s, в количестве %s отправлено", (currentPage - 1) * itemsOnPage + 1, itemsOnPage));
        return ResponseEntity.ok().body(videoDtoService.getPartVideo(currentPage, itemsOnPage));
    }

    @ApiOperation(value = "Получение видео по названию")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "видео по названию получено", response = VideoDto.class)})
    @GetMapping(value = "/name", params = {"name"})
    public ResponseEntity<VideoDto> getVideoOfName(@ApiParam(value = "Название видео", example = "Test video 3") @RequestParam @NotNull String name) {
        logger.info(String.format("Отправка видео c названием %s", name));
        return ResponseEntity.ok().body(videoDtoService.getVideoOfName(name));
    }


    @ApiOperation(value = "Получение видео из коллекции пользователя по частям")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Видео из коллекции пользователя по частям", responseContainer = "List", response = VideoDto.class)})
    @GetMapping(value = "/user/{userId}/video", params = {"currentPage", "itemsOnPage"})
    public ResponseEntity<List<VideoDto>> getPartVideoOfUser(
            @ApiParam(value = "Текущая страница", example = "0") @RequestParam("currentPage") int currentPage,
            @ApiParam(value = "Количество данных на страницу", example = "15") @RequestParam("itemsOnPage") int itemsOnPage,
            @ApiParam(value = "Id юзера", example = "60") @PathVariable("userId") @NonNull Long userId) {
        logger.info(String.format("Видео пользователя %s начиная c объекта номер %s, в количестве %s отправлено ", userId, (currentPage - 1) * itemsOnPage + 1, itemsOnPage));
        return ResponseEntity.ok().body(videoDtoService.getPartVideoOfUser(userId, currentPage, itemsOnPage));
    }

    @ApiOperation(value = "Добавление видео для пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Видео успешно добавлено", response = VideoDto.class)})
    @PostMapping(value = "/user/{userId}/video")
    public ResponseEntity<?> addVideo(@ApiParam(value = "Объект добавляемого видео") @RequestBody @Valid @NonNull VideoDto videoDto,
                                      @ApiParam(value = "Id юзера", example = "60") @PathVariable("userId") @NonNull Long userId) {
        Optional<User> userOptional = userService.getById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Пользователь с %d id не найден", userId));
        }
        User user = userOptional.get();
        Videos videos = videoConverter.toVideo(videoDto, MediaType.VIDEO, user);
        videosService.create(videos);
        logger.info(String.format("Добавление видео с id %s в бд", videoDto.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(videoDto);
    }

    @ApiOperation(value = "Создание видео альбома пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Видео альбом успешно создан", response = AlbumDto.class),
            @ApiResponse(code = 400, message = "Неверные параметры", response = String.class)})
    @Validated(OnCreate.class)
    @PostMapping(value = "/user/{userId}/album")
    public ResponseEntity<?> createVideoAlbum(@ApiParam(value = "объект создаваемого альбома") @RequestBody @Valid @NotNull AlbumVideoDto albumDto,
                                              @ApiParam(value = "Id юзера", example = "60") @PathVariable("userId") @NonNull Long userId) {
        if (albumService.existsByNameAndMediaType(albumDto.getName(), MediaType.VIDEO)) {
            return ResponseEntity.badRequest()
                    .body(String.format("Video album with name '%s' already exists", albumDto.getName()));
        }
        Optional<User> userOptional = userService.getById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Пользователь с %d id не найден", userId));
        }
        AlbumVideo albumVideo = albumVideoService.createAlbumVideosWithOwner(
                albumVideoConverter.toAlbumVideo(albumDto, userOptional.get()));
        logger.info(String.format("Альбом с именем  %s создан", albumDto.getName()));
        return ResponseEntity.ok().body(albumVideoConverter.toAlbumVideoDto(albumVideo));
    }

    @ApiOperation(value = "Добавить видео в альбом")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "видео в альбом успешно добавлено", response = String.class)})
    @PutMapping(value = "/album/video", params = {"albumId", "videoId"})
    public ResponseEntity<?> addInAlbums(@ApiParam(value = "Id альбома", example = "100") @RequestParam @Valid @NotNull Long albumId,
                                         @ApiParam(value = "Id видео", example = "1") @RequestParam @NotNull Long videoId) {
        Optional<AlbumVideo> albumVideoOptional = albumVideoService.getById(albumId);
        if (!albumVideoOptional.isPresent()) {
            logger.info(String.format("Видеоальбом с id  %s не найден", albumId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Видеоальбом с id  %s не найден", albumId));
        }
        Optional<Videos> videosOptional = videosService.getById(videoId);
        if (!videosOptional.isPresent()) {
            logger.info(String.format("Видео с id  %s не найдено", videoId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Видео с id  %s не найдено", videoId));
        }
        AlbumVideo albumVideo = albumVideoOptional.get();
        Set<Videos> videosSet = albumVideo.getVideos();
        videosSet.add(videosOptional.get());
        albumVideo.setVideos(videosSet);
        albumVideoService.create(albumVideo);
        logger.info(String.format("Видео с id  %s добавлено в альбом с id %s", videoId, albumId));
        return ResponseEntity.ok().body(String.format("Video id %s added to album id %s", videoId, albumId));
    }

    @ApiOperation(value = "Получение всех альбомов пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Альбомы успешно получены", response = AlbumDto.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Альбомы не найдены")
    })
    @GetMapping(value = "/user/{userId}/album")
    public ResponseEntity<List<AlbumVideoDto>> getAllAlbums(@ApiParam(value = "Id юзера", example = "60") @PathVariable("userId") @NonNull Long userId) {
        logger.info(String.format("Получение всех альбомов пользователя с id %s", userId));
        return ResponseEntity.ok().body(albumVideoDtoService.getAllByUserId(userId));
    }

    @ApiOperation(value = "Получение всех видео из альбома ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "видео из альбома пользователя успешно получено", response = VideoDto.class, responseContainer = "List")})
    @GetMapping(value = "/album/{albumId}/video")
    public ResponseEntity<?> getFromAlbumOfUser(@ApiParam(value = "Id альбома", example = "7") @PathVariable @NotNull Long albumId) {
        logger.info(String.format("Все видео из альбома с id:%s отправлено", albumId));
        return ResponseEntity.ok().body(videoDtoService.getVideoFromAlbumOfUser(albumId));
    }

    @ApiOperation(value = "Получение видео пользователя по альбому")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Видео из коллекции пользователя по альбому", response = VideoDto.class, responseContainer = "List")})
    @GetMapping(value = "/user/{userId}/video", params = {"album"})
    public ResponseEntity<List<VideoDto>> getAlbumVideoOfUser(@ApiParam(value = "Название альбома", example = "My Album") @RequestParam("album") String album,
                                                              @ApiParam(value = "Id юзера", example = "60") @PathVariable("userId") @NonNull Long userId) {
        logger.info(String.format("Отправка избранного видео пользователя c id %s альбома %s", userId, album));
        return ResponseEntity.ok().body(videoDtoService.getAlbumVideoOfUser(userId, album));
    }
}
