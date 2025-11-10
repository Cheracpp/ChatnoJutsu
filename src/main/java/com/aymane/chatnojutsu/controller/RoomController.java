package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.dto.RoomDTO;
import com.aymane.chatnojutsu.service.RoomServiceImpl;
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

  RoomServiceImpl roomService;

  @Autowired
  public RoomController(RoomServiceImpl roomService) {
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
