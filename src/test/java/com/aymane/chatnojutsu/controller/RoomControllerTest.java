package com.aymane.chatnojutsu.controller;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aymane.chatnojutsu.config.CsrfFilter;
import com.aymane.chatnojutsu.config.JwtFilter;
import com.aymane.chatnojutsu.dto.RoomDTO;
import com.aymane.chatnojutsu.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = RoomController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JwtFilter.class,
        CsrfFilter.class})})
public class RoomControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RoomService roomService;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithValidInputs_ReturnsRoomDTO() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, List.of("participant1", "participant2"), "direct", null);
    RoomDTO result = new RoomDTO("123456789", List.of("participant1", "participant2"), "direct",
        null);
    given(roomService.findOrCreateRoom(roomDTO)).willReturn(result);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomDTO)).with(csrf())).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.roomId", is("123456789")))
        .andExpect(jsonPath("$.type", is("direct")))
        .andExpect(jsonPath("$.participants[0]", is("participant1")))
        .andExpect(jsonPath("$.participants[1]", is("participant2")))
        .andExpect(jsonPath("$.type", is("direct"))).andExpect(jsonPath("$.name", is(nullValue())));
    verify(roomService).findOrCreateRoom(roomDTO);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithEmptyParticipants_ReturnsBadRequest() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, Collections.emptyList(), "direct", null);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomDTO)).with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithInvalidRoomType_ReturnsBadRequest() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, List.of("participant1", "participant2"), "", null);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomDTO)).with(csrf()))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(roomService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void getRooms_WithValidInputs_ReturnsListOfRoomDTO() throws Exception {
    RoomDTO roomDTO1 = new RoomDTO("1", List.of("participant1", "participant2"), "direct", null);
    RoomDTO roomDTO2 = new RoomDTO("2", List.of("participant1", "participant3"), "direct", null);
    RoomDTO roomDTO3 = new RoomDTO("3", List.of("participant1", "participant4"), "direct", null);
    List<RoomDTO> roomDTOs = List.of(roomDTO1, roomDTO2, roomDTO3);

    given(roomService.getRoomsByUserId(any(String.class))).willReturn(roomDTOs);

    mockMvc.perform(get("/api/rooms")).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].roomId", is("1"))).andExpect(jsonPath("$[0].type", is("direct")))
        .andExpect(jsonPath("$[0].participants[0]", is("participant1")))
        .andExpect(jsonPath("$[0].participants[1]", is("participant2")))
        .andExpect(jsonPath("$[0].type", is("direct")))
        .andExpect(jsonPath("$[0].name", is(nullValue())))
        .andExpect(jsonPath("$[1].roomId", is("2"))).andExpect(jsonPath("$[1].type", is("direct")))
        .andExpect(jsonPath("$[1].participants[0]", is("participant1")))
        .andExpect(jsonPath("$[1].participants[1]", is("participant3")))
        .andExpect(jsonPath("$[1].type", is("direct")))
        .andExpect(jsonPath("$[1].name", is(nullValue())))
        .andExpect(jsonPath("$[2].roomId", is("3"))).andExpect(jsonPath("$[2].type", is("direct")))
        .andExpect(jsonPath("$[2].participants[0]", is("participant1")))
        .andExpect(jsonPath("$[2].participants[1]", is("participant4")))
        .andExpect(jsonPath("$[2].type", is("direct")))
        .andExpect(jsonPath("$[2].name", is(nullValue())));

    verify(roomService).getRoomsByUserId(any(String.class));
  }

  @Test
  public void findOrCreateRoom_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, List.of("participant1", "participant2"), "direct", null);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomDTO)).with(csrf()))
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(roomService);
  }

  @Test
  public void getRooms_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/rooms")).andExpect(status().isUnauthorized());

    verifyNoInteractions(roomService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void getRooms_WhenUserHasNoRooms_ReturnsEmptyList() throws Exception {
    given(roomService.getRoomsByUserId("testUser")).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/rooms")).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", empty()));

    verify(roomService).getRoomsByUserId("testUser");
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithGroupType_ReturnsRoomDTO() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, List.of("participant1", "participant2", "participant3"),
        "group", "Team Chat");
    RoomDTO result = new RoomDTO("789", List.of("participant1", "participant2", "participant3"),
        "group", "Team Chat");
    given(roomService.findOrCreateRoom(roomDTO)).willReturn(result);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomDTO)).with(csrf())).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.roomId", is("789"))).andExpect(jsonPath("$.type", is("group")))
        .andExpect(jsonPath("$.name", is("Team Chat")))
        .andExpect(jsonPath("$.participants[0]", is("participant1")))
        .andExpect(jsonPath("$.participants[1]", is("participant2")))
        .andExpect(jsonPath("$.participants[2]", is("participant3")));

    verify(roomService).findOrCreateRoom(roomDTO);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithOnlyOneParticipant_ReturnsBadRequest() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, List.of("participant1"), "direct", null);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomDTO)).with(csrf()))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(roomService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithNullParticipants_ReturnsBadRequest() throws Exception {
    String requestBody = "{\"roomId\":null,\"participants\":null,\"type\":\"direct\",\"name\":null}";

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON).content(requestBody)
        .with(csrf())).andExpect(status().isBadRequest());

    verifyNoInteractions(roomService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithNullType_ReturnsBadRequest() throws Exception {
    String requestBody = "{\"roomId\":null,\"participants\":[\"user1\",\"user2\"],\"type\":null,\"name\":null}";

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON).content(requestBody)
        .with(csrf())).andExpect(status().isBadRequest());

    verifyNoInteractions(roomService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithInvalidTypeValue_ReturnsBadRequest() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, List.of("participant1", "participant2"), "invalid", null);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roomDTO)).with(csrf()))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(roomService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void findOrCreateRoom_WithoutCsrfToken_ReturnsForbidden() throws Exception {
    RoomDTO roomDTO = new RoomDTO(null, List.of("participant1", "participant2"), "direct", null);

    mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(roomDTO))).andExpect(status().isForbidden());

    verifyNoInteractions(roomService);
  }
}



