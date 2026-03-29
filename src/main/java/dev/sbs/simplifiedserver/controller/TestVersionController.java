package dev.sbs.simplifiedserver.controller;

import dev.sbs.serverapi.version.ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller demonstrating URL-path-based API versioning with {@link ApiVersion}.
 *
 * <p>Versioned endpoints are accessible at {@code /v{N}/path} (e.g., {@code /v1/hello}).
 * The {@code /default} endpoint is unversioned and always accessible at its base path.</p>
 */
@Tag(name = "Version Test", description = "Example endpoints demonstrating URL-path-based API versioning")
@RestController
public class TestVersionController {

    @Operation(summary = "Hello v1", description = "Returns a greeting from API version 1.")
    @ApiVersion(1)
    @GetMapping("/hello")
    public @NotNull ResponseEntity<String> getHelloV1() {
        return ResponseEntity.ok("Hello from API v1!");
    }

    @Operation(summary = "Hello v2", description = "Returns a greeting from API version 2.")
    @ApiVersion(2)
    @GetMapping("/hello")
    public @NotNull ResponseEntity<String> getHelloV2() {
        return ResponseEntity.ok("Hello from API v2!");
    }

    @Operation(summary = "Hello v3", description = "Returns a greeting from API version 3.")
    @ApiVersion(3)
    @GetMapping("/hello")
    public @NotNull ResponseEntity<String> getHelloV3() {
        return ResponseEntity.ok("Hello from API v3!");
    }

    @Operation(summary = "Data v1", description = "Returns data in API version 1 format.")
    @ApiVersion(1)
    @GetMapping("/data")
    public @NotNull ResponseEntity<String> getDataV1() {
        return ResponseEntity.ok("Data for API v1: { id: 1, name: 'Item One' }");
    }

    @Operation(summary = "Data v2", description = "Returns data in API version 2 format.")
    @ApiVersion(2)
    @GetMapping("/data")
    public @NotNull ResponseEntity<String> getDataV2() {
        return ResponseEntity.ok("Data for API v2: { itemId: 1, itemName: 'Item One Updated' }");
    }

    @Operation(summary = "Default (unversioned)", description = "Returns a greeting from the default unversioned endpoint.")
    @GetMapping("/default")
    public @NotNull ResponseEntity<String> getDefaultHello() {
        return ResponseEntity.ok("Hello from default (unversioned) endpoint!");
    }

}
