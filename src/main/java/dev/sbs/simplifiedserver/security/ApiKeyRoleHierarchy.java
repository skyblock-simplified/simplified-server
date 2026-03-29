package dev.sbs.simplifiedserver.security;

import dev.sbs.api.collection.concurrent.Concurrent;
import dev.sbs.api.collection.concurrent.ConcurrentList;
import dev.sbs.api.collection.concurrent.ConcurrentMap;
import dev.sbs.api.collection.concurrent.ConcurrentSet;
import org.jetbrains.annotations.NotNull;

/**
 * Hierarchical role resolver for API key authorization.
 *
 * <p>Defines a linear role hierarchy where higher roles automatically inherit all
 * permissions of lower roles. The default hierarchy is:
 * {@code DEVELOPER > SUPER_ADMIN > ADMIN > SUPER_MODERATOR > MODERATOR > HELPER > USER > LIMITED_ACCESS}.</p>
 */
public class ApiKeyRoleHierarchy {

    private final @NotNull ConcurrentMap<String, ConcurrentSet<String>> hierarchy = Concurrent.newMap();

    public ApiKeyRoleHierarchy() {
        this.setupHierarchy(Concurrent.newList(
            "DEVELOPER", "SUPER_ADMIN", "ADMIN", "SUPER_MODERATOR", "MODERATOR", "HELPER", "USER", "LIMITED_ACCESS"
        ));
    }

    private void setupHierarchy(@NotNull ConcurrentList<String> roles) {
        for (int i = 0; i < roles.size(); i++) {
            ConcurrentSet<String> inherited = Concurrent.newSet();

            for (int j = i; j < roles.size(); j++)
                inherited.add(roles.get(j));

            this.hierarchy.put(roles.get(i), inherited);
        }
    }

    /**
     * Expands a set of assigned permissions into the full reachable set including
     * all inherited roles from the hierarchy.
     *
     * @param assignedPermissions the permissions assigned to an API key
     * @return the expanded set including inherited roles and static permissions
     */
    public @NotNull ConcurrentSet<String> getReachablePermissions(@NotNull ConcurrentSet<String> assignedPermissions) {
        ConcurrentSet<String> reachable = Concurrent.newSet(assignedPermissions);

        for (String perm : assignedPermissions) {
            if (this.hierarchy.containsKey(perm))
                reachable.addAll(this.hierarchy.get(perm));
        }

        return reachable;
    }

}
