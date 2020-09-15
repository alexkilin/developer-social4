package com.javamentor.developer.social.platform.models.dto;

import com.javamentor.developer.social.platform.models.entity.media.MediaType;
import com.javamentor.developer.social.platform.models.util.OnCreate;
import com.javamentor.developer.social.platform.models.util.OnUpdate;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AudioDto {

    @ApiModelProperty(notes = "Автоматически генерируемый ID. Не указывать при создании, " +
            "обязательно указывать при изменении объекта")
    @Null(groups = OnCreate.class, message = "'id' Must be null when creating AudioDto.class")
    @NotNull(groups = OnUpdate.class, message = "'id' Must not accept null values when updating AudioDto.class")
    private Long id;

    @ApiModelProperty(notes = "Адрес, на котором располагается аудио объект",
            required = true)
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "'url' Must not be null when creating and updating AudioDto.class")
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "'text' Must not consist of spaces AudioDto.class")
    private String url;

    @ApiModelProperty(notes = "Адрес иконки (превью) аудио объекта")
    private String icon;

    @ApiModelProperty(notes = "Название аудио объекта")
    private String name;

    @ApiModelProperty(notes = "Автор аудио объекта")
    private String author;

    @ApiModelProperty(notes = "Альбом аудио объекта")
    private String album;

    @ApiModelProperty(notes = "Длительность трека в секундах")
    private Integer length;

    @ApiModelProperty(notes = "Дата публикования медиа объекта, назначается автоматически при создании", hidden = true)
    private LocalDateTime persistDateTime;
}
