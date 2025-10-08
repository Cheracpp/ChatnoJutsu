package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.mapper.MessageMapper;
import com.aymane.chatnojutsu.model.Message;
import com.aymane.chatnojutsu.repository.MessageRepository;
import com.aymane.chatnojutsu.repository.RoomRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private static final String DIRECT_DESTINATION = "/queue/private";
  private final MessageRepository messageRepository;
  private final RoomRepository roomRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;

  public MessageService(MessageRepository messageRepository, RoomRepository roomRepository,
      SimpMessagingTemplate messagingTemplate, MessageMapper messageMapper) {
    this.messageRepository = messageRepository;
    this.roomRepository = roomRepository;
    this.messagingTemplate = messagingTemplate;
    this.messageMapper = messageMapper;
  }

  public Message save(MessageDTO messageDTO, String senderId) {
    // save the message first
    Message message = messageRepository.save(messageMapper.fromMessageDTO(messageDTO, senderId));

    // update the lastSentAt field in room document
    roomRepository.updateLastMessageSentAt(message.getRoomId(), message.getTimestamp());
    return message;
  }

  public List<Message> getMessagesByRoomId(String roomId) {
    return messageRepository.findByRoomId(roomId, Sort.by(Sort.Direction.ASC, "timestamp"))
        .orElse(new ArrayList<>());
  }

  public void sendMessage(MessageDTO message, String senderId) {
    for (String participant : message.participants()) {
      if (participant.equals(senderId)) {
        continue;
      }

      messagingTemplate.convertAndSendToUser(participant, DIRECT_DESTINATION, message);
    }
  }
}
