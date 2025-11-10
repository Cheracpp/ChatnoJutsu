package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.RoomDTO;
import com.aymane.chatnojutsu.model.Room;
import java.util.List;

/**
 * Service interface for managing chat room operations.
 *
 * <p>This interface defines the contract for room management functionality including
 * creating new rooms, retrieving room information, and managing user-room associations.</p>
 */
public interface RoomService {

  /**
   * Creates and saves a new chat room.
   *
   * <p>Persists a new room entity based on the provided room data.
   * The room will be created with the specified properties and assigned a unique identifier.</p>
   *
   * @param roomDTO the room data transfer object containing room details such as name and
   *                participants
   * @return the saved Room entity with generated ID and metadata
   */
  Room save(RoomDTO roomDTO);

  /**
   * Retrieves or generates a room identifier based on room criteria.
   *
   * <p>Finds an existing room that matches the criteria in the provided
   * RoomDTO, or creates one if it doesn't exist.</p>
   *
   * @param roomDTO the room data transfer object containing criteria to match against existing
   *                rooms
   * @return a RoomDTO containing the room ID and relevant room information
   */
  RoomDTO findOrCreateRoom(RoomDTO roomDTO);

  /**
   * Retrieves all chat rooms associated with a specific user.
   *
   * <p>Returns all rooms where the specified user is a participant. the rooms are ordered by the
   * timestamp of the last message sent on each room, allowing users to see their most recent active
   * conversations at the top.</p>
   *
   * @param userId the unique identifier of the user whose rooms to retrieve
   * @return a list of RoomDTO objects representing all rooms the user participates in, or an empty
   * list if the user has no rooms
   */
  List<RoomDTO> getRoomsByUserId(String userId);
}
