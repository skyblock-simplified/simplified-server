package dev.sbs.simplifiedserver.security;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation requiring API key authentication on a controller class or handler method.
 *
 * <p>When applied to a class, all handler methods in that class require a valid API key.
 * When applied to a method, only that method is protected. Method-level annotations
 * take precedence over class-level annotations.</p>
 *
 * <p>If {@link #requiredPermissions()} is non-empty, the API key must satisfy at least
 * one of the specified roles or static permission strings.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface ApiKeyProtected {

    /**
     * Roles or static permission strings required to access the annotated endpoint.
     *
     * <p>If empty, only a valid API key is required. If multiple values are provided,
     * the API key must satisfy at least one (any-match semantics).</p>
     *
     * @return an array of permission strings
     */
    @NotNull String[] requiredPermissions() default {};

}
