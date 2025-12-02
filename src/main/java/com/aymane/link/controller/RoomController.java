package com.aymane.link.controller;

import com.aymane.link.dto.RoomDTO;
import com.aymane.link.service.RoomService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

  RoomService roomService;

  @Autowired
  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  @PostMapping
  public ResponseEntity<RoomDTO> findOrCreateRoom(@Valid @RequestBody RoomDTO roomDTO) {
    RoomDTO room = roomService.findOrCreateRoom(roomDTO);
    return ResponseEntity.ok(room);
  }

  @GetMapping
  public ResponseEntity<List<RoomDTO>> getRooms(@AuthenticationPrincipal UserDetails userDetails) {
    List<RoomDTO> roomsByUserId = roomService.getRoomsByUserId(userDetails.getUsername());
    return ResponseEntity.ok(roomsByUserId);
  }
}
