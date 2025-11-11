package com.aymane.chatnojutsu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RoomDTO(String roomId,
                      @NotEmpty(message = "{error.room.participants}") @Size(message = "{error.room.participants.size}", min = 2) List<String> participants,
                      @NotBlank @Pattern(regexp = "^(direct|group)$", message = "{error.room.type}") String type,
                      String name) {

}