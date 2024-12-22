package mod.azure.azurelib.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.scoreboard.Team;

import java.util.Objects;

public class AzEntityNameRenderUtil {

    public static <T extends Entity> boolean shouldShowName(EntityRenderDispatcher entityRenderDispatcher, T entity) {
        double nameRenderDistance = entity.isSneaking() ? 32d : 64d;

        if (!(entity instanceof EntityLiving)) {
            return false;
        }

        if (entityRenderDispatcher.distanceToSqr(entity) >= nameRenderDistance * nameRenderDistance) {
            return false;
        }

        if (
            entity instanceof EntityMob && (!entity.shouldShowName() && (!entity.hasCustomName()
                || entity != entityRenderDispatcher.crosshairPickEntity))
        ) {
            return false;
        }

        final Minecraft minecraft = Minecraft.getMinecraft();
        // TODO: See if we can do this null check better.
        EntityPlayerSP player = Objects.requireNonNull(minecraft.player);
        boolean visibleToClient = !entity.isInvisibleToPlayer(player);
        Team entityTeam = entity.getTeam();

        if (entityTeam == null) {
            return Minecraft.renderNames() && entity != minecraft.getCameraEntity() && visibleToClient
                && !entity.isVehicle();
        }

        Team playerTeam = minecraft.player.getTeam();

        switch (entityTeam.getNameTagVisibility()) {
            case ALWAYS:
                return visibleToClient;
            case NEVER:
                return false;
            case HIDE_FOR_OTHER_TEAMS:
                return playerTeam == null
                        ? visibleToClient
                        : entityTeam.isSameTeam(playerTeam)
                        && (entityTeam.getSeeFriendlyInvisiblesEnabled() || visibleToClient);
            case HIDE_FOR_OWN_TEAM:
                return playerTeam == null
                        ? visibleToClient
                        : !entityTeam.isSameTeam(playerTeam) && visibleToClient;
            default:
                throw new IllegalStateException("Unexpected value: " + entityTeam.getNameTagVisibility());
        }
    }

    private AzEntityNameRenderUtil() {
        throw new UnsupportedOperationException();
    }
}
