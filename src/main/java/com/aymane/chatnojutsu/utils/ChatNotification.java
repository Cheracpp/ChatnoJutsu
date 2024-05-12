package com.aymane.chatnojutsu.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    String messageId;
    String messageFrom;
    String messageTo;
    String content;
}
