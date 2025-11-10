package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.model.Message;
import com.aymane.chatnojutsu.service.MessageService;
import com.aymane.chatnojutsu.service.RoomService;
import java.security.Principal;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class MessageController {

  private final MessageService messageService;
  private final RoomService roomService;

  public MessageController(MessageService messageService, RoomService roomService) {
    this.messageService = messageService;
    this.roomService = roomService;
  }

  @MessageMapping("/messages")
  public void processMessage(@Payload MessageDTO messageDTO, Principal principal) {
    String userId = principal.getName();
    messageService.save(messageDTO, userId);
    messageService.sendMessage(messageDTO, userId);
  }

  @GetMapping("/api/rooms/{roomId}/messages")
  public ResponseEntity<List<Message>> getRoomMessages(@PathVariable ObjectId roomId,
      @AuthenticationPrincipal UserDetails userDetails) {
    String userId = userDetails.getUsername();

    if (!roomService.isUserParticipant(roomId, userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId));
  }
}