package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.RoomDTO;
import com.aymane.chatnojutsu.mapper.RoomMapper;
import com.aymane.chatnojutsu.model.Room;
import com.aymane.chatnojutsu.repository.RoomRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

  private final RoomRepository roomRepository;
  private final RoomMapper roomMapper;

  @Autowired
  public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
    this.roomRepository = roomRepository;
    this.roomMapper = roomMapper;
  }

  public Room save(RoomDTO roomDTO) {
    Room room = roomMapper.fromRoomDTO(roomDTO);
    return roomRepository.save(room);
  }

  public RoomDTO getRoomId(RoomDTO roomDTO) {
    List<String> participants = roomDTO.participants();
    Optional<Room> optionalRoom = roomRepository.findRoomWithExactParticipants(participants);
    Room room = optionalRoom.orElseGet(() -> save(roomDTO));
    return roomMapper.toRoomDTO(room);
  }

  public List<RoomDTO> getRoomsByUserId(String userId) {
    List<Room> rooms = roomRepository.findByParticipantId(userId,
        Sort.by(Sort.Direction.DESC, "lastMessageSentAt"));
    return rooms.stream().map(roomMapper::toRoomDTO).collect(Collectors.toList());
  }
}
