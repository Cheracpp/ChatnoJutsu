package com.aymane.chatnojutsu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aymane.chatnojutsu.config.CustomUserDetails;
import com.aymane.chatnojutsu.dto.RegisterRequest;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.repository.UserRepository;
import com.aymane.chatnojutsu.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private User testUser;
  private UserDTO testUserDTO;
  private RegisterRequest validRegisterRequest;
  private CustomUserDetails mockUserDetails;

  @BeforeEach
  public void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setEmail("test@example.com");

    testUserDTO = new UserDTO("1", "testuser", "test@example.com");

    validRegisterRequest = new RegisterRequest("testuser", "ValidPass123!", "test@example.com");

    mockUserDetails = new CustomUserDetails("1", "testuser", "password", Collections.emptyList());
  }

  @Test
  public void createNewUser_WithValidInput_ReturnsCreatedAndLocation() throws Exception {
    given(userService.registerNewUser(any(RegisterRequest.class))).willReturn(testUser);

    mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                                      .content(
                                          objectMapper.writeValueAsString(validRegisterRequest)))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", "http://localhost/api/users/1"));

    verify(userService).registerNewUser(any(RegisterRequest.class));
  }

  @Test
  public void createNewUser_WithInvalidInput_ReturnsBadRequest() throws Exception {
    RegisterRequest invalidRequest = new RegisterRequest("", "", "");

    mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                                      .content(objectMapper.writeValueAsString(invalidRequest)))
           .andExpect(status().isBadRequest());
  }

  @Test
  public void getAllUsers_ReturnsListOfUsers() throws Exception {
    UserDTO user1 = new UserDTO("1", "user1", "user1@example.com");
    UserDTO user2 = new UserDTO("2", "user2", "user2@example.com");
    UserDTO user3 = new UserDTO("3", "user3", "user3@example.com");
    List<UserDTO> users = List.of(user1, user2, user3);

    given(userService.getAllUsers()).willReturn(users);

    mockMvc.perform(get("/api/users"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.length()").value(3))
           .andExpect(jsonPath("$[0].id").value("1"))
           .andExpect(jsonPath("$[0].username").value("user1"))
           .andExpect(jsonPath("$[0].email").value("user1@example.com"))
           .andExpect(jsonPath("$[1].id").value("2"))
           .andExpect(jsonPath("$[2].id").value("3"));

    verify(userService).getAllUsers();
  }

  @Test
  public void getAllUsers_WhenNoUsers_ReturnsEmptyList() throws Exception {
    given(userService.getAllUsers()).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/users"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.length()").value(0));

    verify(userService).getAllUsers();
  }

  @Test
  public void getUsers_WithValidQuery_ReturnsMatchingUsers() throws Exception {
    String query = "test";
    List<UserDTO> matchingUsers = List.of(testUserDTO);

    given(userService.getUsersByQuery(query)).willReturn(matchingUsers);

    mockMvc.perform(get("/api/users/search").param("query", query))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.length()").value(1))
           .andExpect(jsonPath("$[0].id").value("1"))
           .andExpect(jsonPath("$[0].username").value("testuser"));

    verify(userService).getUsersByQuery(query);
  }

  @Test
  public void getUsers_WithShortQuery_ReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/api/users/search").param("query", "ab"))
           .andExpect(status().isBadRequest());
  }

  @Test
  public void getUsers_WithBlankQuery_ReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/api/users/search").param("query", "   "))
           .andExpect(status().isBadRequest());
  }

  @Test
  public void getUsers_WithMissingQuery_ReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/api/users/search"))
           .andExpect(status().isBadRequest());
  }

  @Test
  public void getUsersDetails_WithValidUserIds_ReturnsUserMap() throws Exception {
    List<String> userIds = List.of("1", "2", "3");
    UserDTO user1 = new UserDTO("1", "user1", "user1@example.com");
    UserDTO user2 = new UserDTO("2", "user2", "user2@example.com");
    UserDTO user3 = new UserDTO("3", "user3", "user3@example.com");
    Map<String, UserDTO> usersMap = Map.of("1", user1, "2", user2, "3", user3);

    given(userService.getUsersByIds(anyList())).willReturn(usersMap);

    mockMvc.perform(post("/api/users/details").contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(userIds)))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.['1'].username").value("user1"))
           .andExpect(jsonPath("$.['2'].username").value("user2"))
           .andExpect(jsonPath("$.['3'].username").value("user3"));

    verify(userService).getUsersByIds(anyList());
  }

  @Test
  public void getUsersDetails_WithEmptyList_ReturnsEmptyMap() throws Exception {
    List<String> emptyList = Collections.emptyList();
    Map<String, UserDTO> emptyMap = Collections.emptyMap();

    given(userService.getUsersByIds(anyList())).willReturn(emptyMap);

    mockMvc.perform(post("/api/users/details").contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(emptyList)))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$").isEmpty());

    verify(userService).getUsersByIds(anyList());
  }
}
