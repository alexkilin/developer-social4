package com.javamentor.developer.social.platform.webapp.controllers.v2;

import com.javamentor.developer.social.platform.models.dto.*;
import com.javamentor.developer.social.platform.models.dto.page.PageDto;
import com.javamentor.developer.social.platform.models.dto.users.*;
import com.javamentor.developer.social.platform.models.entity.user.User;
import com.javamentor.developer.social.platform.models.util.OnCreate;
import com.javamentor.developer.social.platform.models.util.OnUpdate;
import com.javamentor.developer.social.platform.service.abstracts.dto.UserDtoService;
import com.javamentor.developer.social.platform.service.abstracts.model.user.UserService;
import com.javamentor.developer.social.platform.webapp.converters.UserConverter;
import io.swagger.annotations.*;
import lombok.NonNull;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ToString
@Validated
@RestController
@RequestMapping(value = "/api/v2/users", produces = "application/json")
@SuppressWarnings("deprecation")
@Api(value = "UserApi-v2", description = "Опирации над пользователем")
public class UserControllerV2 {

    private final UserDtoService userDtoService;
    private final UserService userService;
    private final UserConverter userConverter;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserControllerV2(UserDtoService userDtoService, UserService userService,
                            UserConverter userConverter) {
        this.userDtoService = userDtoService;
        this.userService = userService;
        this.userConverter = userConverter;
    }


    @ApiOperation(value = "Получение пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь получен", response = UserDto.class),
            @ApiResponse(code = 404, message = "Пользователь не найден", response = String.class)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getUserById(@ApiParam(value = "Идентификатор пользователя", example = "10") @PathVariable @Valid @NonNull Long id) {
        Optional<UserDto> optionalUserDto = userDtoService.getUserDtoById(id);
        if (optionalUserDto.isPresent()) {
            UserDto userDto = optionalUserDto.get();
            logger.info(String.format("Пользователь с ID: %d получен!", id));
            return ResponseEntity.ok(userDto);
        }
        logger.info(String.format("Пользователь с указанным ID: %d не найден!", id));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User with ID: %d does not exist.", id));
    }

    @ApiOperation(value = "Получение списка пользователей")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список пользователей получен", responseContainer = "List", response = UserDto.class)
    })
    @GetMapping(params = {"currentPage", "itemsOnPage"})
    public ResponseEntity<PageDto<Object, Object>> getAllUsers(@ApiParam(value = "Текущая страница", example = "1") @RequestParam("currentPage") int currentPage,
                                                               @ApiParam(value = "Количество данных на страницу", example = "15") @RequestParam("itemsOnPage") int itemsOnPage) {
        logger.info("Получен список пользователей");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("currentPage", currentPage);
        parameters.put("itemsOnPage", itemsOnPage);
        return ResponseEntity.ok(userDtoService.getAllUserDto(parameters));
    }

    @ApiOperation(value = "Создание пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь создан", response = UserRegisterDto.class),
            @ApiResponse(code = 400, message = "Пользователь с данным email существует. Email должен быть уникальным", response = String.class)
    })
    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<?> createUser(@ApiParam(value = "Объект создаваемого пользователя") @RequestBody @Valid @NotNull UserRegisterDto userRegisterDto) {
        if (userService.existByEmail(userRegisterDto.getEmail())) {
            logger.info(String.format("Пользователь с email: %s уже существует", userRegisterDto.getEmail()));
            return ResponseEntity.badRequest().body(String.format("User with email: %s already exist. Email should be unique", userRegisterDto.getEmail()));
        }
        User user = userConverter.toEntity(userRegisterDto);
        userService.create(user);
        logger.info(String.format("Пользователь с email: %s добавлен в базу данных", userRegisterDto.getEmail()));
        return ResponseEntity.ok(userConverter.toDto(user));
    }

    @ApiOperation(value = "Обновление личной информации пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь обновлен", response = UserUpdateInfoDto.class),
            @ApiResponse(code = 400, message = "E-mail занят другим пользователем", response = String.class),
            @ApiResponse(code = 404, message = "Пользователь не обновлен из-за несоответствия id", response = String.class)
    })
    @PutMapping
    @Validated(OnUpdate.class)
    public ResponseEntity<?> updateUserInfo(@ApiParam(value = "Пользователь с обновленными данными") @Valid @RequestBody UserUpdateInfoDto userUpdateInfoDto) {
        Optional<User> optionalUser = userService.getById(userUpdateInfoDto.getUserId());
        if (optionalUser.isPresent()) {
            if (userService.existsAnotherByEmail(userUpdateInfoDto.getEmail(), userUpdateInfoDto.getUserId())) {
                logger.info(String.format("Пользователь с email: %s уже существует", userUpdateInfoDto.getEmail()));
                return ResponseEntity.badRequest().body(String.format("User with email: %s already exist. Email should be unique", userUpdateInfoDto.getEmail()));
            }
            User user = userConverter.toEntity(userUpdateInfoDto);
            userService.updateInfo(user);
            logger.info(String.format("Пользователь с ID: %d обновлён успешно", userUpdateInfoDto.getUserId()));
            return ResponseEntity.ok(userConverter.toDto(user));
        }
        logger.info(String.format("Пользователь с ID: %d не существует", userUpdateInfoDto.getUserId()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User with ID: %d does not exist.", userUpdateInfoDto.getUserId()));
    }

    @ApiOperation(value = "Изменение пароля")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пароль обновлен", response = UserResetPasswordDto.class),
            @ApiResponse(code = 404, message = "Пароль не обновлен из-за несоответствия id", response = String.class)
    })
    @PatchMapping("/{id}/password")
    @Validated(OnCreate.class)
    public ResponseEntity<?> updateUserPassword(@ApiParam(value = "Id пользователя")
                                                @PathVariable Long id,
                                                @ApiParam(value = "Новый пароль")
                                                @Valid @RequestBody UserResetPasswordDto userResetPasswordDto) {

        Optional<User> optionalUser = userService.getById(id);
        if (!optionalUser.isPresent()) {
            logger.info(String.format("Пользователь с ID: %d не существует", id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("User with ID: %d does not exist.", id));
        }
        User user = optionalUser.get();
        user.setPassword(userResetPasswordDto.getPassword());
        userService.updateUserPassword(user);
        logger.info(String.format("Пароль пользователя %d изменен", id));
        return ResponseEntity.ok()
                .body(String.format("Password changed for user %d", id));
    }

    @ApiOperation(value = "Удаление пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь удален", response = String.class),
            @ApiResponse(code = 404, message = "Пользователя с данным id нет в базе данных", response = String.class)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@ApiParam(value = "Идентификатор пользователя", example = "10") @PathVariable @NonNull Long id) {
        if (userService.existById(id)) {
            userService.deleteById(id);
            logger.info(String.format("Пользователь с ID: %d удалён успешно ", id));
            return ResponseEntity.ok(String.format("User with ID: %d deleted", id));
        }
        logger.info(String.format("Пользователь с ID: %d не удалён", id));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User with ID: %d does not exist.", id));
    }

    @ApiOperation(value = "Получение списка друзей пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список друзей пользователя получен", responseContainer = "List", response = UserFriendDto.class),
            @ApiResponse(code = 404, message = "Пользователя с таким id не существует", response = String.class)
    })
    @GetMapping("/{id}/friends")
    public ResponseEntity<?> getUserFriends(
            @ApiParam(value = "Текущая страница", example = "1") @RequestParam("currentPage") int currentPage,
            @ApiParam(value = "Количество данных на страницу", example = "15") @RequestParam("itemsOnPage") int itemsOnPage,
            @ApiParam(value = "Идентификатор пользователя", example = "10") @PathVariable("id") @NonNull Long userId) {
        if (userService.existById(userId)) {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("userId", userId);
            parameters.put("currentPage", currentPage);
            parameters.put("itemsOnPage", itemsOnPage);

            logger.info("Получен список друзей пользователя");
            return ResponseEntity.ok(userDtoService.getUserFriendsDtoById(parameters));
        }
        logger.info("Пользователя с таким id не существует, список друзей пользователя не получен");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User with ID: %d does not exist.", userId));
    }

    @ApiOperation(value = "Изменение статуса пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Статус пользователя изменён", response = UserDto.class),
            @ApiResponse(code = 404, message = "Пользователь не найден", response = String.class)
    })
    @PatchMapping(value = "/status")
    public ResponseEntity<?> updateUserStatus(@ApiParam(value = "Статус пользователя") @Valid @RequestBody StatusDto statusDto) {
        Optional<UserDto> optionalUserDto = userDtoService.getUserDtoById(statusDto.getUserId());
        if (optionalUserDto.isPresent()) {
            UserDto userDto = optionalUserDto.get();
            userDto.setStatus(statusDto.getStatus());
            userService.update(userConverter.toEntity(userDto));
            logger.info("Статус изменён");
            return ResponseEntity.ok(userDto);
        }
        logger.info(String.format("Пользователь с указанным ID: %d не найден!", statusDto.getUserId()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User with ID: %d does not exist.", statusDto.getUserId()));
    }

    @ApiOperation(value = "Авторизация пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь успешно авторизован", response = UserDto.class),
            @ApiResponse(code = 400, message = "Неправльный пароль", response = String.class),
            @ApiResponse(code = 404, message = "Пользователь с этим email не найден", response = String.class)
    })
    @Validated(OnCreate.class)
    @PostMapping("/login")
    public ResponseEntity<?> userAuthorization(@ApiParam(value = "Данные для авторизации") @Valid @RequestBody UserAuthorizationDto userAuthorizationDto) {
        Optional<User> user = userService.getByEmail(userAuthorizationDto.getEmail());
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User with email: %s does not exist.", userAuthorizationDto.getEmail()));
        }
        if (userAuthorizationDto.getPassword().equals(user.get().getPassword())) {
            return ResponseEntity.ok(userConverter.toDto(user.get()));
        }
        return ResponseEntity.badRequest().body("Incorrect password");
    }

    @ApiOperation(value = "Получение авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь получен", response = UserDto.class),
            @ApiResponse(code = 404, message = "Пользователь не найден", response = String.class)
    })
    @GetMapping("/principal")
    public ResponseEntity<?> getPrincipal() {
        User userPrincipal = userService.getPrincipal();
        if (userPrincipal != null) {
            return ResponseEntity.ok().body(userConverter.toDto(userPrincipal));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Авторизованный пользователь не найден"));

    }

    @ApiOperation(value = "Получение фильтра по списку друзей пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список друзей пользователя получен", responseContainer = "List", response = UserFriendDto.class),
            @ApiResponse(code = 404, message = "Пользователя с таким id не существует", response = String.class)
    })
    @GetMapping("/{id}/filterfriends")
    public ResponseEntity<?> getFilterUserFriends(
            @ApiParam(value = "Текущая страница", example = "1") @RequestParam("currentPage") int currentPage,
            @ApiParam(value = "Количество данных на страницу", example = "15") @RequestParam("itemsOnPage") int itemsOnPage,
            @ApiParam(value = "Идентификатор пользователя", example = "10") @PathVariable("id") @NonNull Long userId,
            @ApiParam(value = "Фильтр по начальной дате рождения", example = "2008-05-30") @RequestParam(value = "startDateOfBirth", required = false) String startDateOfBirth,
            @ApiParam(value = "Фильтр по конечной дате рождения", example = "2008-05-30") @RequestParam(value = "endDateOfBirth", required = false) String endDateOfBirth,
            @ApiParam(value = "Фильтр по образованию", example = "MIT University") @RequestParam(value = "education", required = false) String education,
            @ApiParam(value = "Фильтр по профессии", example = "Plumber") @RequestParam(value = "profession", required = false) String profession,
            @ApiParam(value = "Фильтр по городу", example = "SPb") @RequestParam(value = "city", required = false) String city) {
        if (userService.existById(userId)) {

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("userId", userId);
            parameters.put("currentPage", currentPage);
            parameters.put("itemsOnPage", itemsOnPage);

            Map<String, Object> filters = new HashMap<>();
            filters.put("startDateOfBirth", startDateOfBirth);
            filters.put("endDateOfBirth", endDateOfBirth);
            filters.put("education", education);
            filters.put("profession", profession);
            filters.put("city", city);

            parameters.put("filters", filters);

            logger.info("Получен список друзей пользователя с фильтрами");
            return ResponseEntity.ok(userDtoService.getFilterFriendsDtoById(parameters));
        }
        logger.info("Пользователя с таким id не существует, список друзей пользователя не получен");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("User with ID: %d does not exist.", userId));
    }
}