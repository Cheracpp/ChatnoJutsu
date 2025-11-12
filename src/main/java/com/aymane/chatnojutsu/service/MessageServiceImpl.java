package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.mapper.MessageMapper;
import com.aymane.chatnojutsu.model.Message;
import com.aymane.chatnojutsu.repository.MessageRepository;
import com.aymane.chatnojutsu.repository.RoomRepository;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

  private static final String DIRECT_DESTINATION = "/queue/";
  private final MessageRepository messageRepository;
  private final RoomRepository roomRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;

  public MessageServiceImpl(MessageRepository messageRepository, RoomRepository roomRepository,
      SimpMessagingTemplate messagingTemplate, MessageMapper messageMapper) {
    this.messageRepository = messageRepository;
    this.roomRepository = roomRepository;
    this.messagingTemplate = messagingTemplate;
    this.messageMapper = messageMapper;
  }

  @Override
  public Message save(MessageDTO messageDTO, String senderId) {
    return messageRepository.save(messageMapper.fromMessageDTO(messageDTO, senderId));
  }

  @Override
  public List<Message> getMessagesByRoomId(ObjectId roomId) {
    return messageRepository.findByRoomId(roomId, Sort.by(Sort.Direction.ASC, "timestamp"))
                            .orElse(new ArrayList<>());
  }

  @Override
  public void sendMessage(MessageDTO message, String senderId) {
    for (String participant : message.participants()) {
      if (participant.equals(senderId)) {
        continue;
      }

      messagingTemplate.convertAndSend(DIRECT_DESTINATION + participant, message);
    }
  }
}
