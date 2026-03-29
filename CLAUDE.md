# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

See the root [`CLAUDE.md`](../CLAUDE.md) for multi-module build commands, environment variables, cross-cutting patterns, and dependency details.
See [`server-api/CLAUDE.md`](../server-api/CLAUDE.md) for the reusable Spring server framework (API versioning, API key auth, error handling, server config).

## Build & Test

```bash
# From repo root
./gradlew :simplified-server:build          # Build (includes shadowJar)
./gradlew :simplified-server:test           # Run all tests

# Fat JAR (shadowJar merged into build task)
./gradlew :simplified-server:shadowJar      # Output: build/libs/simplified-server-0.1.0.jar
```

## Module Overview

`simplified-server` is the SkyBlock-specific Spring Boot REST server. It depends on `server-api` (framework), `api`, and `minecraft-api` via Maven coordinates. The `server-api` framework provides API versioning, API key authentication, error handling, and server configuration; this module provides the concrete controllers, OpenAPI metadata, and application entry point.

Follows the same split pattern as `discord-api` (framework) vs `simplified-bot` (implementation).

### Entry Point

- **`SimplifiedServer`** - Spring Boot application. Gson is the primary HTTP serializer via `MinecraftApi.getGson()`, registered first in `configureMessageConverters()`. Jackson auto-configuration remains enabled for SpringDoc's internal OpenAPI spec generation. Configures `StringHttpMessageConverter` (for HTML error pages) and `GsonHttpMessageConverter` as message converters. Registers `SecurityHeaderInterceptor` for security response headers. Implements `WebMvcConfigurer` directly (no `@EnableWebMvc`, no `WebMvcConfigurationSupport`). Uses `ServerConfig.optimized()` to supply all default properties programmatically. Scans both `dev.sbs.simplifiedserver` and `dev.sbs.serverapi` packages via `@SpringBootApplication(scanBasePackages = ...)`.

### Package Structure

**`config/`** - Application-specific configuration:
- `OpenApiConfig` - `@Configuration` defining the `OpenAPI` metadata bean (title, description, version) used by SpringDoc for spec generation at `/v3/api-docs` and rendered by the Scalar UI at the root path.

**`controller/`** - Spring MVC REST controllers:
- `MojangController` - Mojang API proxy endpoints under `/mojang/`. Single `getUser(String identifier)` endpoint handles both username and UUID by attempting `UUID.fromString()` first. Uses `MinecraftApi.getMojangProxy()` for upstream calls. Annotated with `@Tag` and `@Operation` for OpenAPI documentation.
- `TestApiKeyController` - Example endpoints under `/api/` demonstrating `@ApiKeyProtected` with `ApiKeyRole` role requirements.
- `TestVersionController` - Example versioned endpoints (`/v1/hello`, `/v2/hello`, `/default`) demonstrating URL path versioning.

### Dependencies

- **`server-api`** - Reusable Spring server framework (API versioning, auth, error handling, config). Transitively provides `api` and `spring-boot-starter-web`.
- **`minecraft-api`** - Minecraft/Hypixel API client, Mojang proxy.
- **`springdoc-openapi-starter-webmvc-scalar`** - OpenAPI spec generation and Scalar UI (implementation-specific).

### Configuration

All properties are managed programmatically through `ServerConfig` (from `server-api`):
- `ServerConfig.builder()` - Full control with Spring Boot defaults
- `ServerConfig.optimized()` - Production preset (virtual threads, compression, graceful shutdown, reverse proxy support)
- `api.key.authentication.enabled` - Toggles API key security (default `true` in `ServerConfig`)
- `springdocEnabled` - Toggles SpringDoc OpenAPI spec generation and Scalar UI (default `true` in `ServerConfig`)

### API Documentation (SpringDoc + Scalar)

- **Dependency:** `springdoc-openapi-starter-webmvc-scalar` (version in `gradle/libs.versions.toml`)
- **Root path:** `GET /` redirects to the Scalar UI (`springdoc.use-root-path=true`)
- **OpenAPI spec:** `GET /v3/api-docs` returns the auto-generated OpenAPI 3.0 JSON
- **Jackson coexistence:** Jackson auto-configuration is enabled (not excluded) so SpringDoc can use it internally. Gson remains the primary HTTP serializer because `GsonHttpMessageConverter` is registered first in `configureMessageConverters()`
- **Controller annotations:** `@Tag` (class-level) and `@Operation` (method-level) from `io.swagger.v3.oas.annotations` enrich the generated documentation
- **Versioned endpoints:** Discovered automatically via the custom `ApiVersionHandlerMapping` which extends `RequestMappingHandlerMapping`
