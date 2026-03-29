package dev.sbs.simplifiedserver.controller;

import dev.sbs.serverapi.security.ApiKeyProtected;
import dev.sbs.serverapi.security.ApiKeyRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller demonstrating {@link ApiKeyProtected} with {@link ApiKeyRole}
 * role requirements under the {@code /api/} path prefix.
 */
@Tag(name = "API Key Test", description = "Example endpoints demonstrating API key authentication")
@RestController
@RequestMapping("/api")
public class TestApiKeyController {

    @Operation(summary = "Admin panel access", description = "Requires ADMIN role via X-API-Key header.")
    @GetMapping("/admin-panel")
    @ApiKeyProtected(requiredPermissions = { ApiKeyRole.ADMIN })
    public @NotNull ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Welcome, Administrator.");
    }

    @Operation(summary = "Restart service", description = "Requires DEVELOPER role via X-API-Key header.")
    @PostMapping("/restart")
    @ApiKeyProtected(requiredPermissions = { ApiKeyRole.DEVELOPER })
    public @NotNull ResponseEntity<String> restartService() {
        return ResponseEntity.ok("Service is restarting...");
    }

    @Operation(summary = "Basic user access", description = "Requires USER role via X-API-Key header.")
    @GetMapping("/basic")
    @ApiKeyProtected(requiredPermissions = { ApiKeyRole.USER })
    public @NotNull ResponseEntity<String> basicAccess() {
        return ResponseEntity.ok("Basic user access granted.");
    }

}
