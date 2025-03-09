package pe.goblin.resourceservice.domain.support.auth.model;

import java.util.Map;
import java.util.Set;

public record Permission(
        String resourceId,
        String resourceName,
        Set<String> scopes,
        Map<String, Set<String>> claims
) {
    public Permission(final String resourceId, final Set<String> scopes) {
        this(resourceId, null, scopes, null);
    }
}
