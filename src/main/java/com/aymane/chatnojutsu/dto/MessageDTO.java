package com.aymane.chatnojutsu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record MessageDTO(@NotBlank String roomId, @NotEmpty List<String> participants,
                         @NotBlank String senderId, @NotBlank String content) {

}
