package com.javamentor.developer.social.platform.webapp.controllers.v2;

import com.javamentor.developer.social.platform.models.dto.UserDto;
import com.javamentor.developer.social.platform.models.dto.group.GroupDto;
import com.javamentor.developer.social.platform.models.dto.group.GroupInfoDto;
import com.javamentor.developer.social.platform.models.dto.group.GroupWallDto;
import com.javamentor.developer.social.platform.models.entity.group.Group;
import com.javamentor.developer.social.platform.models.entity.user.User;
import com.javamentor.developer.social.platform.service.abstracts.dto.GroupDtoService;
import com.javamentor.developer.social.platform.service.abstracts.model.group.GroupHasUserService;
import com.javamentor.developer.social.platform.service.abstracts.model.group.GroupService;
import com.javamentor.developer.social.platform.service.abstracts.model.user.UserService;
import io.swagger.annotations.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v2/groups", produces = "application/json")
@SuppressWarnings("deprecation")
@Api(value = "GroupsApi-v2", description = "Операции над группами")
public class GroupControllerV2 {
    private final GroupDtoService groupDtoService;
    private final GroupHasUserService groupHasUserService;
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public GroupControllerV2(GroupDtoService groupDtoService, GroupHasUserService groupHasUserService, UserService userService, GroupService groupService) {
        this.groupDtoService = groupDtoService;
        this.groupHasUserService = groupHasUserService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @ApiOperation(value = "Получение информации обо всех группах")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Инфа обо всех группах получена", responseContainer = "List", response = GroupInfoDto.class),
            @ApiResponse(code = 400, message = "Неверные параметры", response = String.class)
    })
    @GetMapping(value = "", params = {"page", "size"})
    public ResponseEntity<List<GroupInfoDto>> getAllGroups(@ApiParam(value = "Текущая страница", example = "0") @RequestParam("page") int page,
                                                           @ApiParam(value = "Количество данных на страницу", example = "15") @RequestParam("size") int size) {
        return ResponseEntity.ok().body(groupDtoService.getAllGroups(page, size));
    }

    @ApiOperation(value = "Получение информации об одной группе")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о группе получена", response = GroupDto.class),
            @ApiResponse(code = 404, message = "Группа с данным id не найдена", response = String.class)
    })
    @GetMapping("/{groupId}")
    public ResponseEntity<?> showGroup(@ApiParam(value = "Идентификатор группы", example = "1") @PathVariable @NonNull Long groupId) {
        Optional<GroupDto> groupDtoOptional = groupDtoService.getGroupById(groupId);
        if (!groupDtoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Group id %s not found", groupId));
        }
        return ResponseEntity.ok().body(groupDtoOptional.get());
    }

    @ApiOperation(value = "Получение всех постов группы по id группы")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Посты группы получены", responseContainer = "List", response = GroupWallDto.class)
    })
    @GetMapping(value = "/{groupId}/posts", params = {"page", "size"})
    public ResponseEntity<List<GroupWallDto>> showGroupWall(@ApiParam(value = "Идентификатор группы", example = "1") @PathVariable @NonNull Long groupId,
                                                            @ApiParam(value = "Текущая страница", example = "0") @RequestParam("page") int page,
                                                            @ApiParam(value = "Количество данных на страницу", example = "15") @RequestParam("size") int size) {
        return ResponseEntity.ok().body(groupDtoService.getPostsByGroupId(groupId, page, size));
    }

    @ApiOperation(value = "Получение группы по наименованию группы")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Группа найдена", response = GroupInfoDto.class),
            @ApiResponse(code = 404, message = "Группа не найдена", response = String.class)
    })
    @GetMapping(value = "/name", params = "name")
    public ResponseEntity<?> findGroupByName(@ApiParam(value = "Наименование группы", example = "JAVA IS 1") @RequestParam("name") String name) {
        Optional<GroupDto> groupInfoDto = groupDtoService.getGroupByName(name);
        if (!groupInfoDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Group name %s not found", name));
        }
        return ResponseEntity.ok().body(groupInfoDto.get());
    }

    @ApiOperation(value = "Получение всех юзеров, входящих в группу")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Юзеры группы получены", responseContainer = "List", response = UserDto.class),
            @ApiResponse(code = 404, message = "Группа не найдена", response = String.class)
    })
    @GetMapping(value = "/{groupId}/users")
    public ResponseEntity<?> getUsersFromTheGroup(@ApiParam(value = "Идентификатор группы", example = "1") @PathVariable @NonNull Long groupId) {
        Optional<GroupDto> groupDtoOptional = groupDtoService.getGroupById(groupId);
        if (!groupDtoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Group id %s not found", groupId));
        }
        return ResponseEntity.ok(groupDtoService.getUsersFromTheGroup(groupId));
    }

    @ApiOperation(value = "Вступление в группу пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Пользователь вступил в группу"),
            @ApiResponse(code = 400, message = "Пользователь уже есть в данной группе"),
            @ApiResponse(code = 404, message = "Пользователь или группа не найдены")
    })
    @PutMapping(value = "/{groupId}/users", params = "userId")
    public ResponseEntity<String> userJoinGroup(@ApiParam(value = "Идентификатор группы", example = "1") @PathVariable("groupId") @NonNull Long groupId,
                                                @ApiParam(value = "Идентификатор пользователя", example = "1") @RequestParam("userId") @NonNull Long userId) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/add").
                buildAndExpand().toUri();
        if (groupHasUserService.verificationUserInGroup(groupId,userId)) {
            String msg = String.format("Пользователь с id: %d уже есть в группе с id: %s", userId, groupId);
            return ResponseEntity.badRequest().body(msg);
        }
        if (userService.existById(userId) & groupService.existById(groupId)) {
            User user = userService.getById(userId);
            Group group = groupService.getById(groupId);
            groupHasUserService.setUserIntoGroup(user, group);
            String msg = String.format("Пользователь с id: %d добавлен в группу с id: %s", userId, groupId);
            return ResponseEntity.created(location).body(msg);
        } else {
            //String msg = String.format("Пользователь с id: %d и/или группа с id: %s не найдены", userId, groupId);
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Удаление пользователя из группы")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Пользователь удален из группы", response = String.class),
            @ApiResponse(code = 404, message = "Пользователь или группа не найдены")
    })
    @DeleteMapping(value = "/{groupId}/users", params = "userId")
    public ResponseEntity<?> deleteUserById(@ApiParam(value = "Идентификатор группы", example = "10") @PathVariable("groupId") @NonNull Long groupId,
                                            @ApiParam(value = "Идентификатор юзера", example = "1") @RequestParam("userId") @NonNull Long userId){
        if (groupHasUserService.verificationUserInGroup(groupId, userId)) {
            groupHasUserService.deleteUserById(groupId, userId);
            String msg = String.format("Пользователь с id: %d удален из группы с id: %s", userId, groupId);
            return ResponseEntity.ok().body(msg);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
