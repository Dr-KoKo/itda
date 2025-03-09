package pe.goblin.resourceservice.domain.support.auth.model;

import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Set;

public enum Resources {
    TEAM_POST("posts-{id}", "posts", "urn:itda:resources:posts", List.of("posts.team:view", "posts.team:write"), null),
    JOB_POST("posts-{id}", "posts", "urn:itda:resources:posts", List.of("posts.job:view", "posts.job.write"), null),
    ;

    private final String name;
    private final String displayName;
    private final String type;
    private final List<String> scopes;
    private final Set<String> uris;

    Resources(String name, String displayName, String type, List<String> scopes, Set<String> uris) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.scopes = scopes;
        this.uris = uris;
    }

    public String getName(@Nullable String id) {
        return id != null ? name.replace("{id}", id) : name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public Set<String> getUris() {
        return uris;
    }
}
