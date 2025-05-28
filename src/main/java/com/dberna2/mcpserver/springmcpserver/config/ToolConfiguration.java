package com.dberna2.mcpserver.springmcpserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class ToolConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(ToolConfiguration.class);

  @Bean
  @SuppressWarnings("unchecked")
  public Map<String, McpSchema.Tool> buildTools() throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    final PathMatchingResourcePatternResolver resolver     = new PathMatchingResourcePatternResolver();
    final Map<String, McpSchema.Tool> toolDefinitions = new HashMap<>();

    final Resource[] resources = resolver.getResources("classpath:tools/*.json");

    for (final Resource resource : resources) {
      try (final InputStream is = resource.getInputStream()) {
        final Map<String, Object> rawTool = mapper.readValue(is, Map.class);
        final McpSchema.Tool tool = new McpSchema.Tool(
            (String) rawTool.get("name"),
            (String) rawTool.get("description"),
            mapper.writeValueAsString(rawTool.get("schema"))
        );
        toolDefinitions.put(tool.name(), tool);
        LOGGER.info("Tool definition loaded [{}]", tool.name());
      }
    }
    return toolDefinitions;
  }
}
