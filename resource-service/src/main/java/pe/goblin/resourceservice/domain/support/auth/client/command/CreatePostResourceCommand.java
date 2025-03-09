package pe.goblin.resourceservice.domain.support.auth.client.command;

import pe.goblin.resourceservice.domain.support.auth.client.AuthorizationClient;

import static pe.goblin.resourceservice.domain.support.auth.model.Resources.TEAM_POST;

public class CreatePostResourceCommand extends AuthorizationClient.CreateResourceCommand {
    public CreatePostResourceCommand(String postId, String ownerId) {
        super(TEAM_POST.getName(postId), TEAM_POST.getDisplayName(), TEAM_POST.getType(), TEAM_POST.getScopes(), ownerId, TEAM_POST.getUris());
    }
}
