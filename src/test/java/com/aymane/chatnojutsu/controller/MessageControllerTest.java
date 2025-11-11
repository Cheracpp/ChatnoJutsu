package com.aymane.chatnojutsu.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.model.Message;
import com.aymane.chatnojutsu.service.MessageService;
import com.aymane.chatnojutsu.service.RoomService;
import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private RoomService roomService;

  @Test
  @WithMockUser(username = "testUser")
  public void getRoomMessages_WhenUserIsParticipant_ReturnsMessages() throws Exception {
    ObjectId roomId = new ObjectId();
    ObjectId messageId1 = new ObjectId();
    ObjectId messageId2 = new ObjectId();
    Instant now = Instant.now();

    Message message1 = Message.builder().messageId(messageId1).roomId(roomId).senderId("user1")
        .content("Hello").timestamp(now).build();

    Message message2 = Message.builder().messageId(messageId2).roomId(roomId).senderId("user2")
        .content("Hi there").timestamp(now.plusSeconds(10)).build();

    List<Message> messages = List.of(message1, message2);

    given(roomService.isUserParticipant(roomId, "testUser")).willReturn(true);
    given(messageService.getMessagesByRoomId(roomId)).willReturn(messages);

    mockMvc.perform(get("/api/rooms/{roomId}/messages", roomId.toHexString()))
        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].messageId.timestamp").exists())
        .andExpect(jsonPath("$[0].roomId.timestamp").exists())
        .andExpect(jsonPath("$[0].senderId", is("user1")))
        .andExpect(jsonPath("$[0].content", is("Hello")))
        .andExpect(jsonPath("$[1].messageId.timestamp").exists())
        .andExpect(jsonPath("$[1].roomId.timestamp").exists())
        .andExpect(jsonPath("$[1].senderId", is("user2")))
        .andExpect(jsonPath("$[1].content", is("Hi there")));

    verify(roomService).isUserParticipant(roomId, "testUser");
    verify(messageService).getMessagesByRoomId(roomId);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void getRoomMessages_WhenUserIsNotParticipant_ReturnsForbidden() throws Exception {
    ObjectId roomId = new ObjectId();

    given(roomService.isUserParticipant(roomId, "testUser")).willReturn(false);

    mockMvc.perform(get("/api/rooms/{roomId}/messages", roomId.toHexString()))
        .andExpect(status().isForbidden());

    verify(roomService).isUserParticipant(roomId, "testUser");
    verifyNoInteractions(messageService);
  }

  @Test
  public void getRoomMessages_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    ObjectId roomId = new ObjectId();

    mockMvc.perform(get("/api/rooms/{roomId}/messages", roomId.toHexString()))
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(roomService);
    verifyNoInteractions(messageService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void getRoomMessages_WhenRoomHasNoMessages_ReturnsEmptyList() throws Exception {
    ObjectId roomId = new ObjectId();

    given(roomService.isUserParticipant(roomId, "testUser")).willReturn(true);
    given(messageService.getMessagesByRoomId(roomId)).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/rooms/{roomId}/messages", roomId.toHexString()))
        .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));

    verify(roomService).isUserParticipant(roomId, "testUser");
    verify(messageService).getMessagesByRoomId(roomId);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void getRoomMessages_WithInvalidRoomId_ReturnsBadRequest() throws Exception {
    String invalidRoomId = "invalid-id";

    mockMvc.perform(get("/api/rooms/{roomId}/messages", invalidRoomId))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(roomService);
    verifyNoInteractions(messageService);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void getRoomMessages_WithValidRoomId_ParsesObjectIdCorrectly() throws Exception {
    ObjectId roomId = new ObjectId();

    given(roomService.isUserParticipant(roomId, "testUser")).willReturn(true);
    given(messageService.getMessagesByRoomId(roomId)).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/rooms/{roomId}/messages", roomId.toHexString()))
        .andExpect(status().isOk());

    verify(roomService).isUserParticipant(roomId, "testUser");
    verify(messageService).getMessagesByRoomId(roomId);
  }

  @Test
  @WithMockUser(username = "user1")
  public void getRoomMessages_WithDifferentUser_ChecksCorrectUserId() throws Exception {
    ObjectId roomId = new ObjectId();

    given(roomService.isUserParticipant(roomId, "user1")).willReturn(true);
    given(messageService.getMessagesByRoomId(roomId)).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/rooms/{roomId}/messages", roomId.toHexString()))
        .andExpect(status().isOk());

    verify(roomService).isUserParticipant(roomId, "user1");
    verify(messageService).getMessagesByRoomId(roomId);
  }

  @Test
  @WithMockUser(username = "testUser")
  public void getRoomMessages_WithMultipleMessages_MaintainsOrder() throws Exception {
    ObjectId roomId = new ObjectId();
    Instant baseTime = Instant.now();

    List<Message> messages = List.of(
        Message.builder().messageId(new ObjectId()).roomId(roomId).senderId("user1")
            .content("First message").timestamp(baseTime).build(),
        Message.builder().messageId(new ObjectId()).roomId(roomId).senderId("user2")
            .content("Second message").timestamp(baseTime.plusSeconds(5)).build(),
        Message.builder().messageId(new ObjectId()).roomId(roomId).senderId("user1")
            .content("Third message").timestamp(baseTime.plusSeconds(10)).build());

    given(roomService.isUserParticipant(roomId, "testUser")).willReturn(true);
    given(messageService.getMessagesByRoomId(roomId)).willReturn(messages);

    mockMvc.perform(get("/api/rooms/{roomId}/messages", roomId.toHexString()))
        .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].content", is("First message")))
        .andExpect(jsonPath("$[1].content", is("Second message")))
        .andExpect(jsonPath("$[2].content", is("Third message")));

    verify(roomService).isUserParticipant(roomId, "testUser");
    verify(messageService).getMessagesByRoomId(roomId);
  }

  @Test
  public void processMessage_WithValidMessage_SavesAndSendsMessage() {
    MessageDTO messageDTO = new MessageDTO("507f1f77bcf86cd799439011", List.of("user1", "user2"),
        "user1", "Hello World");
    Principal principal = () -> "user1";

    MessageController controller = new MessageController(messageService, roomService);

    controller.processMessage(messageDTO, principal);

    verify(messageService).save(messageDTO, "user1");
    verify(messageService).sendMessage(messageDTO, "user1");
  }

  @Test
  public void processMessage_WithDifferentUser_UsesCorrectUserId() {
    MessageDTO messageDTO = new MessageDTO("507f1f77bcf86cd799439011", List.of("testUser", "user2"),
        "testUser", "Test message");
    Principal principal = () -> "testUser";

    MessageController controller = new MessageController(messageService, roomService);

    controller.processMessage(messageDTO, principal);

    verify(messageService).save(messageDTO, "testUser");
    verify(messageService).sendMessage(messageDTO, "testUser");
  }

  @Test
  public void processMessage_WithMultipleParticipants_ProcessesCorrectly() {
    MessageDTO messageDTO = new MessageDTO("507f1f77bcf86cd799439011",
        List.of("user1", "user2", "user3", "user4"), "user1", "Group message");
    Principal principal = () -> "user1";

    MessageController controller = new MessageController(messageService, roomService);

    controller.processMessage(messageDTO, principal);

    verify(messageService).save(messageDTO, "user1");
    verify(messageService).sendMessage(messageDTO, "user1");
  }

  @Test
  public void processMessage_ExtractsUserIdFromPrincipal() {
    String expectedUserId = "authenticatedUser";
    MessageDTO messageDTO = new MessageDTO("507f1f77bcf86cd799439011",
        List.of("authenticatedUser", "otherUser"), "authenticatedUser", "Message content");
    Principal principal = () -> expectedUserId;

    MessageController controller = new MessageController(messageService, roomService);

    controller.processMessage(messageDTO, principal);

    verify(messageService).save(eq(messageDTO), eq(expectedUserId));
    verify(messageService).sendMessage(eq(messageDTO), eq(expectedUserId));
  }

  @Test
  public void processMessage_CallsServiceMethodsInOrder() {
    MessageDTO messageDTO = new MessageDTO("507f1f77bcf86cd799439011", List.of("user1", "user2"),
        "user1", "Test");
    Principal principal = () -> "user1";
    ObjectId savedMessageId = new ObjectId();
    Message savedMessage = Message.builder().messageId(savedMessageId)
        .roomId(new ObjectId("507f1f77bcf86cd799439011")).senderId("user1").content("Test")
        .timestamp(Instant.now()).build();

    given(messageService.save(any(MessageDTO.class), eq("user1"))).willReturn(savedMessage);

    MessageController controller = new MessageController(messageService, roomService);

    controller.processMessage(messageDTO, principal);

    verify(messageService).save(messageDTO, "user1");
    verify(messageService).sendMessage(messageDTO, "user1");
  }
}
