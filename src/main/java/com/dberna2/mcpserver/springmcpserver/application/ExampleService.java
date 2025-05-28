package com.dberna2.mcpserver.springmcpserver.application;

import com.dberna2.mcpserver.springmcpserver.model.Presentation;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

  public List<Presentation> getPresentationsByYear(final Integer year, final String name) {
    return List.of(new Presentation(name, name, LocalDateTime.now()));
  }
}
