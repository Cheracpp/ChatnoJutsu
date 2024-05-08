package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.RoomDTO;
import com.aymane.chatnojutsu.mapper.RoomMapper;
import com.aymane.chatnojutsu.model.Room;
import com.aymane.chatnojutsu.repository.RoomRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room save(RoomDTO roomDTO) {
        Room room = RoomMapper.RoomDTOtoRoom(roomDTO);
        return roomRepository.save(room);
    }

    public String getRoomId(RoomDTO roomDTO) {
        Optional<Room> optionalRoom = roomRepository.getRoom(roomDTO.messageFrom(), roomDTO.messageTo());
        String roomId;
        roomId = optionalRoom
                .map(Room::getId)
                .orElseGet(() -> save(roomDTO).getId());
        return roomId;
    }
    public List<String> getChatsByUsername(String username){
        List<String> listOfChats = new ArrayList<>();
        List<Room> listOfRooms = roomRepository.findRoomsByUsername(username, Sort.by(Sort.Direction.DESC, "lastMessageSentAt"));
        for(Room room : listOfRooms){
            if(room.getLastMessageSentAt() == null){
            }else if(Objects.equals(room.getMessageFrom(), username)){
                listOfChats.add(room.getMessageTo());
            }else {
                listOfChats.add(room.getMessageFrom());
            }
        }
        return listOfChats;
    }
}
