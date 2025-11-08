package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.model.Message;
import java.util.List;
import org.bson.types.ObjectId;

/**
 * Service interface for managing message-related operations.
 *
 * <p>This interface defines the contract for message management functionality including
 * saving messages, retrieving chat history, and sending messages in real-time.</p>
 */
public interface MessageService {

  /**
   * Saves a message to the database.
   *
   * <p>Persists a message sent by a user to a specific chat room
   * or conversation.</p>
   *
   * @param messageDTO the message data transfer object containing message content and metadata
   * @param senderId   the unique identifier of the user sending the message
   * @return the saved Message entity with generated ID and timestamp
   */
  Message save(MessageDTO messageDTO, String senderId);

  /**
   * Retrieves all messages for a specific chat room.
   *
   * <p>Returns messages in chronological order (oldest to newest)
   * to maintain conversation flow.</p>
   *
   * @param roomId the unique identifier of the chat room
   * @return a list of Message objects belonging to the specified room, or an empty list if no
   * messages exist
   */
  List<Message> getMessagesByRoomId(ObjectId roomId);

  /**
   * Sends a message in real-time to all connected clients in a chat room.
   *
   * <p>Handles the real-time delivery of messages through
   * WebSocket connections.</p>
   *
   * @param message  the message data transfer object to be sent
   * @param senderId the unique identifier of the user sending the message
   */
  void sendMessage(MessageDTO message, String senderId);
}
