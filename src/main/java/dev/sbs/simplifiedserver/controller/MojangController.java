package dev.sbs.simplifiedserver.controller;

import dev.sbs.minecraftapi.MinecraftApi;
import dev.sbs.minecraftapi.client.mojang.response.MojangProfile;
import dev.sbs.minecraftapi.client.mojang.response.MojangProperties;
import dev.sbs.minecraftapi.client.mojang.response.MojangUsername;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Mojang API proxy endpoints under {@code /mojang/}.
 *
 * <p>Provides player profile lookup by username or UUID, username resolution,
 * and skin properties. Delegates to {@link MinecraftApi#getMojangProxy()} for
 * upstream calls.</p>
 */
@Tag(name = "Mojang", description = "Mojang API proxy endpoints")
@RestController
@RequestMapping("/mojang")
public class MojangController {

    /**
     * Looks up a Mojang profile by username or UUID string.
     *
     * <p>Attempts to parse the identifier as a {@link UUID} first; if that fails,
     * treats it as a username.</p>
     *
     * @param identifier a Minecraft username or UUID string
     * @return the Mojang profile
     */
    @Operation(summary = "Lookup player profile", description = "Resolves a Mojang profile by username or UUID string.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/{identifier}")
    public @NotNull MojangProfile getUser(@Parameter(description = "Minecraft username or UUID") @NotNull @PathVariable String identifier) {
        try {
            UUID uniqueId = UUID.fromString(identifier);
            return MinecraftApi.getMojangProxy().getMojangProfile(uniqueId);
        } catch (IllegalArgumentException ignored) {
            return MinecraftApi.getMojangProxy().getMojangProfile(identifier);
        }
    }

    /**
     * Resolves a Minecraft username to its Mojang username record.
     *
     * @param username the Minecraft username
     * @return the Mojang username record
     */
    @Operation(summary = "Resolve username", description = "Resolves a Minecraft username to its Mojang username record.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/username/{username}")
    public @NotNull MojangUsername getUsername(@Parameter(description = "Minecraft username") @NotNull @PathVariable String username) {
        return MinecraftApi.getMojangProxy().getEndpoint().getPlayer(username);
    }

    /**
     * Fetches the skin properties for a player by UUID.
     *
     * @param uniqueId the player UUID
     * @return the Mojang properties containing skin data
     */
    @Operation(summary = "Fetch skin properties", description = "Fetches the skin properties for a player by UUID.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/properties/{uniqueId}")
    public @NotNull MojangProperties getProperties(@Parameter(description = "Player UUID") @NotNull @PathVariable UUID uniqueId) {
        return MinecraftApi.getMojangProxy().getEndpoint().getProperties(uniqueId);
    }

}
