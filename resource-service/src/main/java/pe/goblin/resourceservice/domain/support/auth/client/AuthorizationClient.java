package pe.goblin.resourceservice.domain.support.auth.client;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;
import org.keycloak.authorization.client.resource.ProtectedResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.stereotype.Component;
import pe.goblin.resourceservice.domain.support.auth.exception.AuthorizationClientException;
import pe.goblin.resourceservice.domain.support.auth.model.Permission;
import pe.goblin.resourceservice.global.properties.KeycloakProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AuthorizationClient {
    private final String authServerUrl;
    private final String realm;
    private final String clientId;
    private final Map<String, Object> credentials;

    public AuthorizationClient(KeycloakProperties keycloakProperties) {
        this.authServerUrl = keycloakProperties.getAuthServerUrl();
        this.realm = keycloakProperties.getRealm();
        this.clientId = keycloakProperties.getResource();
        this.credentials = keycloakProperties.getCredentials();
    }

    public String obtainRequestPartyToken(String accessToken) {
        try {
            // create a new instance based on the configuration
            AuthzClient authzClient = createAuthzClient();

            // send the entitlement request to the server in order to
            // obtain an RPT with all permissions granted to the user
            AuthorizationResponse response = authzClient.authorization(accessToken).authorize();

            return response.getToken();
        } catch (Exception e) {
            throw new AuthorizationClientException(e);
        }
    }

    public String obtainRequestPartyToken(String accessToken, Permission... permissions) {
        try {
            // create a new instance based on the configuration
            AuthzClient authzClient = createAuthzClient();

            // create an authorization request
            AuthorizationRequest request = new AuthorizationRequest();

            // add permissions to the request based on the resources and scopes you want to check access
            for (Permission permission : permissions) {
                request.addPermission(permission.resourceId(), List.copyOf(permission.scopes()));
            }

            // send the entitlement request to the server in order to
            // obtain an RPT with permissions for a single resource
            AuthorizationResponse response = authzClient.authorization(accessToken).authorize(request);
            return response.getToken();
        } catch (Exception e) {
            throw new AuthorizationClientException(e);
        }
    }

    public <T extends CreateResourceCommand> String registerResource(T cmd) {
        try {
            // create a new instance based on the configuration
            AuthzClient authzClient = createAuthzClient();

            // create a new resource representation with the information we want
            ResourceRepresentation newResource = new ResourceRepresentation();

            newResource.setName(cmd.name); // ex. newResource.setName("New Resource")
            newResource.setDisplayName(cmd.displayName);
            newResource.setType(cmd.type); // ex. newResource.setType("urn:hello-world-authz:resources:example")
            for (String scope : cmd.scopes) {
                newResource.addScope(new ScopeRepresentation(scope)); // ex. newResource.addScope(new ScopeRepresentation("urn:hello-world-authz:scopes:view"))
            }
            newResource.setUris(cmd.uris);
            newResource.setOwner(cmd.owner);

            ProtectedResource resourceClient = authzClient.protection().resource();
            ResourceRepresentation existingResource = resourceClient.findByName(newResource.getName());

            if (existingResource != null) {
                resourceClient.delete(existingResource.getId());
            }

            // create the resource on the server
            ResourceRepresentation response = resourceClient.create(newResource);

            return response.getId();
        } catch (Exception e) {
            throw new AuthorizationClientException(e);
        }
    }

    public List<Permission> introspectRpt(String requestPartyToken) {
        try {
            // create a new instance based on the configuration
            AuthzClient authzClient = createAuthzClient();

            // introspect the token
            TokenIntrospectionResponse requestingPartyToken = authzClient.protection().introspectRequestingPartyToken(requestPartyToken);

            List<Permission> permissions = new ArrayList<>();
            for (org.keycloak.representations.idm.authorization.Permission granted : requestingPartyToken.getPermissions()) {
                permissions.add(new Permission(granted.getResourceId(), granted.getResourceName(), granted.getScopes(), granted.getClaims()));
            }
            return permissions;
        } catch (Exception e) {
            throw new AuthorizationClientException(e);
        }
    }

    public String obtainClientAccessToken() {
        try {
            AuthzClient authzClient = createAuthzClient();
            AccessTokenResponse accessTokenResponse = authzClient.obtainAccessToken();
            return accessTokenResponse.getToken();
        } catch (Exception e) {
            throw new AuthorizationClientException(e);
        }
    }

    private AuthzClient createAuthzClient() {
        return AuthzClient.create(new Configuration(authServerUrl, realm, clientId, credentials, null));
    }

    public static class CreateResourceCommand {
        protected final String name;
        protected final String displayName;
        protected final String type;
        protected final List<String> scopes;
        protected final String owner;
        protected final Set<String> uris;

        protected CreateResourceCommand(String name, String displayName, String type, List<String> scopes, String owner, Set<String> uris) {
            this.name = name;
            this.displayName = displayName;
            this.type = type;
            this.scopes = scopes;
            this.owner = owner;
            this.uris = uris;
        }
    }
}
