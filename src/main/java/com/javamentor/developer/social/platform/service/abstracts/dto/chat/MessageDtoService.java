package com.javamentor.developer.social.platform.service.abstracts.dto.chat;

import com.javamentor.developer.social.platform.models.dto.chat.MessageDto;

import java.util.List;

public interface MessageDtoService {
    List<MessageDto> getAllMessageDtoFromGroupChatByChatId(Long chatId);
    List<MessageDto> getAllMessageDtoFromSingleChatByChatId(Long chatId);
}