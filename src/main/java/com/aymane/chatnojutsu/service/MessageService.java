package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.mapper.MessageMapper;
import com.aymane.chatnojutsu.model.Message;
import com.aymane.chatnojutsu.model.Room;
import com.aymane.chatnojutsu.repository.MessageRepository;
import com.aymane.chatnojutsu.repository.RoomRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;

    public MessageService(MessageRepository messageRepository, RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    public Message save(MessageDTO messageDTO, String roomId) {
        // updating Room
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if(optionalRoom.isPresent()){
        Room room = optionalRoom.get();
        room.setLastMessageSentAt(messageDTO.createdAt());
        roomRepository.save(room);
        }
        // saving the message
        Message message = MessageMapper.toMessage(messageDTO);
        message.setRoomId(roomId);
        return messageRepository.save(message);
    }

    public List<Message> findMessagesBetweenTwoUsers(String roomId) {

        return messageRepository.findByRoomId(
                roomId,
                Sort.by(Sort.Direction.ASC, "createdAt")
        ).orElse(new ArrayList<>());
    }
}
