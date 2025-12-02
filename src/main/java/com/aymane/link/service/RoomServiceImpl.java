package com.aymane.link.service;

import com.aymane.link.dto.RoomDTO;
import com.aymane.link.mapper.RoomMapper;
import com.aymane.link.model.Room;
import com.aymane.link.repository.RoomRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

  private final RoomRepository roomRepository;
  private final RoomMapper roomMapper;

  @Autowired
  public RoomServiceImpl(RoomRepository roomRepository, RoomMapper roomMapper) {
    this.roomRepository = roomRepository;
    this.roomMapper = roomMapper;
  }

  @Override
  public Room save(RoomDTO roomDTO) {
    Room room = roomMapper.fromRoomDTO(roomDTO);
    return roomRepository.save(room);
  }

  @Override
  public RoomDTO findOrCreateRoom(RoomDTO roomDTO) {
    List<String> participants = roomDTO.participants();
    Optional<Room> optionalRoom = roomRepository.findRoomWithExactParticipants(participants);
    Room room = optionalRoom.orElseGet(() -> save(roomDTO));
    return roomMapper.toRoomDTO(room);
  }

  @Override
  public List<RoomDTO> getRoomsByUserId(String userId) {
    List<Room> rooms = roomRepository.findByParticipantIdOrderedByLastMessage(userId);
    return rooms.stream()
                .map(roomMapper::toRoomDTO)
                .collect(Collectors.toList());
  }

  @Override
  public boolean isUserParticipant(ObjectId roomId, String userId) {
    return roomRepository.existsByRoomIdAndParticipantsContaining(roomId, userId);
  }
}
