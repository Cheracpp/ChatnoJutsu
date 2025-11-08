package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.model.Message;
import com.aymane.chatnojutsu.service.MessageService;
import java.security.Principal;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @MessageMapping("/chat")
  public void processMessage(@Payload MessageDTO messageDTO, Principal principal) {
    String userId = principal.getName();
    messageService.save(messageDTO, userId);
    messageService.sendMessage(messageDTO, userId);
  }

  @GetMapping("/messages/{roomId}")
  public ResponseEntity<List<Message>> getRoomMessages(@PathVariable ObjectId roomId) {
    return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId));
  }
}