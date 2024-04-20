package com.aymane.chatnojutsu.mapper;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.model.Message;

public class MessageMapper {
    public static Message toMessage(MessageDTO messageDTO){
        return Message.builder()
                .messageFrom(messageDTO.messageFrom())
                .messageTo(messageDTO.messageTo())
                .content(messageDTO.content())
                .createdAt(messageDTO.createdAt())
                .build();
    }
    public static MessageDTO toMessageDTO(Message message){
        return new MessageDTO(
                message.getMessageFrom(),
                message.getMessageTo(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
