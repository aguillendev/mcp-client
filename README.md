# Cliente MCP (Model Context Protocol) con Spring AI

Este proyecto demuestra cómo crear un cliente MCP en un proyecto Spring Boot (3.4+) utilizando Java 21 y la integración oficial de Spring AI. El cliente se conecta a un servidor remoto MCP utilizando el transporte de Server-Sent Events (SSE).

## Explicación: ¿Cómo recupera el cliente la lista de herramientas?

El cliente recupera y registra automáticamente las herramientas del servidor de la siguiente manera:

1. **Inicialización y Negociación (Handshake):**
   Al arrancar la aplicación, el método `@Bean(initMethod = "initialize")` de `McpSyncClient` inicia el proceso. El cliente se conecta a la URL del servidor MCP mediante SSE (`HttpClientSseClientTransport`) y realiza un *handshake* inicial para intercambiar capacidades (capabilities) entre cliente y servidor.

2. **Descubrimiento de Herramientas (List Tools):**
   Durante la inicialización, el cliente envía un mensaje JSON-RPC para listar las herramientas disponibles (`tools/list`). El servidor MCP remoto responde con el listado de herramientas y sus metadatos (descripción, parámetros JSON Schema esperados, etc.).

3. **Integración con Spring AI (ToolCallbackProvider):**
   Usamos un `SyncMcpToolCallbackProvider`, el cual encapsula (wrap) el cliente `McpSyncClient`. Este provider mapea dinámicamente cada herramienta devuelta por el servidor a un `ToolCallback` compatible con Spring AI.

4. **Registro en el LLM:**
   Finalmente, inyectamos este provider en el `ChatClient.Builder` (vía `.defaultTools(mcpToolProvider.getToolCallbacks())`). Así, cuando enviamos un prompt, Spring AI incluye automáticamente en la petición a OpenAI (o el proveedor LLM correspondiente) la especificación de todas las herramientas MCP. Si el LLM decide usar alguna de estas herramientas, Spring AI llama al cliente MCP, quien a su vez ejecuta la herramienta en el servidor remoto mediante otra llamada JSON-RPC (`tools/call`), retornando el resultado al LLM de forma completamente transparente (Tool Calling automático).
