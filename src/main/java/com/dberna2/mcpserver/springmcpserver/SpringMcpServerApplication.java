package com.dberna2.mcpserver.springmcpserver;

import com.dberna2.mcpserver.springmcpserver.application.ExampleService;
import com.dberna2.mcpserver.springmcpserver.config.definition.ToolDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.Prompt;
import io.modelcontextprotocol.spec.McpSchema.PromptArgument;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringMcpServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringMcpServerApplication.class, args);
  }

  @Bean
  public ToolCallbackProvider weatherTools(ExampleService exampleService) {
    return MethodToolCallbackProvider.builder().toolObjects(exampleService).build();
  }

  @Bean
  public McpSyncServer mcpServer(final List<ToolDefinition> toolDefinitions) {

    Package pkg = SpringMcpServerApplication.class.getPackage();
    System.out.println("GroupId (Vendor): " + pkg.getImplementationVendor());
    System.out.println("ArtifactId (Title): " + pkg.getImplementationTitle());
    System.out.println("Version: " + pkg.getImplementationVersion());

    final SyncToolSpecification[] toolSpecifications = toolDefinitions.stream()
        .map(ToolDefinition::getToolSpecification)
        .toArray(SyncToolSpecification[]::new);
    //var transportProvider = new HttpServletSseServerTransportProvider(new ObjectMapper(), "");
    var transportProvider = new StdioServerTransportProvider(new ObjectMapper());

     return McpServer.sync(transportProvider)
        .serverInfo("javaone-mcp-server", "0.0.2")
        .capabilities(
            McpSchema.ServerCapabilities.builder()
                .tools(true)
                .prompts(true)
                .resources(true, true)
                .logging()
                .build()
        )
        .tools(toolSpecifications)
        .build();
  }

  @Bean
  public List<McpServerFeatures.SyncPromptSpecification> myPrompts() {
    var prompt = new Prompt("greeting", "A friendly greeting prompt",
        List.of(new PromptArgument("name", "The name to greet", true)));

    var promptSpecification = new McpServerFeatures.SyncPromptSpecification(
        prompt,
        (exchange, request) -> {
      String nameArgument = (String) request.arguments().get("name");
      if (nameArgument == null) {
        nameArgument = "friend";
      }
      var userMessage = new PromptMessage(Role.USER, new TextContent("Hello " + nameArgument + "! How can I assist you today?"));

      return new GetPromptResult("A personalized greeting message", List.of(userMessage));
    });

    return List.of(promptSpecification);
  }
}
