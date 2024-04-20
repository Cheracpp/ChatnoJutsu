package com.aymane.chatnojutsu.dto;

import java.util.Date;

public record MessageDTO(String messageFrom, String messageTo, String content, Date createdAt) {
}
