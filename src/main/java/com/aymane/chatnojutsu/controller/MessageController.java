package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.dto.RoomDTO;
import com.aymane.chatnojutsu.model.Message;
import com.aymane.chatnojutsu.service.MessageService;
import com.aymane.chatnojutsu.service.RoomService;
import com.aymane.chatnojutsu.utils.ChatNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
@Slf4j
public class MessageController {
    private final MessageService messageService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;


    public MessageController(MessageService messageService, RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO messageDTO) {
        String roomId = roomService.getRoomId(new RoomDTO(messageDTO.messageFrom(), messageDTO.messageTo()));
        Message message = messageService.save(messageDTO, roomId);
        messagingTemplate.convertAndSendToUser(
                message.getMessageTo(), "/queue/private",
                new ChatNotification(
                        message.getMessageId(),
                        message.getMessageFrom(),
                        message.getMessageTo(),
                        message.getContent()
                )
        );
    }

    @GetMapping("/messages/{messageFrom}/{messageTo}")
    public ResponseEntity<List<Message>> findMessagesBetweenTwoUsers(@PathVariable String messageFrom, @PathVariable String messageTo) {

        String roomId = roomService.getRoomId(new RoomDTO(messageFrom, messageTo));
        return ResponseEntity.ok(
                messageService.findMessagesBetweenTwoUsers(roomId));
    }
}