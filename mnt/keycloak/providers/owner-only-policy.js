const context = $evaluation.getContext();
const identity = context.getIdentity();
const permission = $evaluation.getPermission();
const resource_owner = permission.getResource().getOwner()

if (resource_owner != null && identity.getId() === resource_owner) {
    $evaluation.grant();
}
