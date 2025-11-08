package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.RegisterRequest;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.User;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing user-related operations.
 *
 * <p>This interface defines the contract for user management functionality including
 * user registration, and user retrieval operations.</p>
 */
public interface UserService {

  /**
   * Registers a new user in the system.
   *
   * @param registerRequest the registration request containing user details
   * @return the newly created User entity
   */
  User registerNewUser(RegisterRequest registerRequest);

  /**
   * Retrieves all users in the system.
   *
   * @return a list of UserDTO objects representing all users
   */
  List<UserDTO> getAllUsers();

  /**
   * Searches for users based on a query string.
   *
   * <p>The query typically matches against user fields such as username,
   * display name, or email address.</p>
   *
   * @param query the search query string to match users against
   * @return a list of UserDTO objects matching the search criteria
   */
  List<UserDTO> getUsersByQuery(String query);

  /**
   * Retrieves multiple users by their IDs.
   *
   * @param ids the list of user IDs to retrieve
   * @return a map where keys are user IDs and values are corresponding UserDTO objects
   */
  Map<String, UserDTO> getUsersByIds(List<String> ids);
}
