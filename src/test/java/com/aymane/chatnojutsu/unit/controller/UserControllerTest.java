package com.aymane.chatnojutsu.unit.controller;

import com.aymane.chatnojutsu.controller.UserController;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.service.RoomService;
import com.aymane.chatnojutsu.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    RoomService roomService;
    @Autowired
    ObjectMapper objectMapper;

    private UserDTO validUserDTO;
    private User mockedUser;
    @BeforeEach
    public void setUpEnvironment(){
        // Create a valid userDTO that can be used across all the tests.
        validUserDTO = new UserDTO(
                "standardUser",
                "standardPassword",
                "standard@email.com"
        );
        // create a mocked user object with predefined id
        mockedUser = new User();
        mockedUser.setId(1L);

        // reset the mocked userService so we maintain the clean state
        reset(userService);

    }
    @Test
    public void createNewUser_WithValidInput_UserCreated() throws Exception {
        // Convert the validUser DTO to JSON
        String userJSON = objectMapper.writeValueAsString(validUserDTO);

        // Mock the behaviour of userService
        given(userService.registerNewUser(any(UserDTO.class))).willReturn(mockedUser);

        // Perform the request and validate
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/1"));

        // Capture and verify UserDTO
        ArgumentCaptor<UserDTO> captor = ArgumentCaptor.forClass(UserDTO.class);
        verify(userService).registerNewUser(captor.capture());
        UserDTO capturedUser = captor.getValue();

        // Assertions
        assertAll(
                () -> assertEquals("standardUser", capturedUser.username()),
                () -> assertEquals("standard@email.com", capturedUser.email()),
                () -> assertEquals("standardPassword", capturedUser.password())
        );
    }


    @Test
    public void getAllUsers_WhenUsersExist_ReturnsUserList() throws Exception {
        // creating a list of 3 users
        List<String> usernames = Arrays.asList(validUserDTO.username(), "user2", "user3");

        // Mocking the behaviour of userService
        given(userService.getAllUsers()).willReturn(usernames);

        // Perform the request and validate that users
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("standardUser"))
                .andExpect(jsonPath("$[1]").value("user2"))
                .andExpect(jsonPath("$[2]").value("user3"));
    }
}