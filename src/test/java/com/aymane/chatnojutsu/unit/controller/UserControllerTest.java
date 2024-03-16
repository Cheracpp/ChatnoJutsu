package com.aymane.chatnojutsu.unit.controller;

import com.aymane.chatnojutsu.controller.UserController;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.service.UserService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    @Test
    public void createNewUser_WithValidInput_UserCreated() throws Exception {
        String userJSON = "{\"username\":\"testUser\",\"password\":\"testPassword\",\"email\":\"test@email.com\"}";
        User user = new User();
        user.setId(1L);

        given(userService.registerNewUser(any(UserDTO.class))).willReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/1"));

        ArgumentCaptor<UserDTO> userDtoCaptor = ArgumentCaptor.forClass(UserDTO.class);
        verify(userService).registerNewUser(userDtoCaptor.capture());
        UserDTO capturedUserDto = userDtoCaptor.getValue();

        assertThat(capturedUserDto.username()).isEqualTo("testUser");
        assertThat(capturedUserDto.email()).isEqualTo("test@email.com");
        assertThat(capturedUserDto.password()).isEqualTo("testPassword");
    }

    @Test
    public void getAllUsers_WhenUsersExist_ReturnsUserList() throws Exception {
        List<String> listOfAllUserUsernames = Arrays.asList("user1", "user2", "user3");
        given(userService.getAllUsers()).willReturn(listOfAllUserUsernames);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("user1"))
                .andExpect(jsonPath("$[1]").value("user2"))
                .andExpect(jsonPath("$[2]").value("user3"));
    }

}