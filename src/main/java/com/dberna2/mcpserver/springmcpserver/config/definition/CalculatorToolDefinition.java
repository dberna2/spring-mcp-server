package com.dberna2.mcpserver.springmcpserver.config.definition;

import com.dberna2.mcpserver.springmcpserver.application.ExampleService;
import com.dberna2.mcpserver.springmcpserver.model.Presentation;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class CalculatorToolDefinition implements ToolDefinition {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorToolDefinition.class);

  private final ExampleService exampleService;
  private final Map<String, McpSchema.Tool> toolDefinitions;

  public CalculatorToolDefinition(final ExampleService exampleService,
      final Map<String, McpSchema.Tool> toolDefinitions) {
    this.exampleService = exampleService;
    this.toolDefinitions = toolDefinitions;
  }

  @Override
  public SyncToolSpecification getToolSpecification() {
    final Tool tool = toolDefinitions.get("listJavaOne");
    return new SyncToolSpecification(tool, this.buildCallToolResult());
  }

  private BiFunction<McpSyncServerExchange, Map<String, Object>, CallToolResult> buildCallToolResult() {
    return (exchange, args) -> {

      LOGGER.info("Call tool '{}' failed...", args);

      final Integer year = (Integer) args.get("year");
      final String type = (String) args.get("type");

      LOGGER.info("Call tool '{}' failed", year);
      final List<Presentation> presentations = exampleService.getPresentationsByYear(year, type);

      final List<Content> contents = new ArrayList<>();
      for (final Presentation presentation : presentations) {
        contents.add(new TextContent(presentation.toString()));
      }
      return new CallToolResult(contents, false);
    };
  }
}
