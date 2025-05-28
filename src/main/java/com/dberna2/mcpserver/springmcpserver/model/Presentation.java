package com.dberna2.mcpserver.springmcpserver.model;

import java.time.LocalDateTime;

public record Presentation(String title, String url, LocalDateTime date) {
}
