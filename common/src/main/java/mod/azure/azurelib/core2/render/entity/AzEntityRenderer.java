package mod.azure.azurelib.core2.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.model.cache.AzBakedModelCache;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.impl.AzEntityRendererPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AzEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private float scaleWidth = 1;
    private float scaleHeight = 1;
    private final AzEntityRendererPipeline<T> azEntityRendererPipeline;
    private final List<AzRenderLayer<T>> renderLayers;
    private final AzEntityAnimator<T> azAnimator;

    protected AzEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.azEntityRendererPipeline = new AzEntityRendererPipeline<>(this);
        this.renderLayers = new ObjectArrayList<>();
        this.azAnimator = createAnimator();

        if (azAnimator != null) {
            azAnimator.registerControllers(azAnimator.getAnimationControllerContainer());
        }
    }

    protected abstract @NotNull ResourceLocation getModelLocation(T entity);

    public void superRender(@NotNull T entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        var modelResourceLocation = getModelLocation(entity);
        var bakedGeoModel = AzBakedModelCache.getInstance().getNullable(modelResourceLocation);

        if (bakedGeoModel != null) {
            azAnimator.getAnimationProcessor().setActiveModel(bakedGeoModel);
        }

        azEntityRendererPipeline.defaultRender(poseStack, bakedGeoModel, entity, bufferSource, null, null, entityYaw, partialTick, packedLight);
    }

    protected @Nullable AzEntityAnimator<T> createAnimator() {
        return null;
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzEntityRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzEntityRenderer<T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /**
     * Returns the list of registered {@link AzRenderLayer GeoRenderLayers} for this renderer
     */
    public List<AzRenderLayer<T>> getRenderLayers() {
        return this.renderLayers;
    }

    /**
     * Adds a {@link AzRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public AzEntityRenderer<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
        this.renderLayers.add(renderLayer);

        return this;
    }

    /**
     * Whether the entity's nametag should be rendered or not.<br>
     * Pretty much exclusively used in {@link EntityRenderer#renderNameTag}
     */
    @Override
    public boolean shouldShowName(T entity) {
        var nameRenderDistance = entity.isDiscrete() ? 32d : 64d;

        if (!(entity instanceof LivingEntity)) {
            return false;
        }

        if (this.entityRenderDispatcher.distanceToSqr(entity) >= nameRenderDistance * nameRenderDistance) {
            return false;
        }

        if (
            entity instanceof Mob && (!entity.shouldShowName() && (!entity.hasCustomName()
                || entity != this.entityRenderDispatcher.crosshairPickEntity))
        ) {
            return false;
        }

        final var minecraft = Minecraft.getInstance();
        // TODO: See if we can do this null check better.
        var player = Objects.requireNonNull(minecraft.player);
        var visibleToClient = !entity.isInvisibleTo(player);
        var entityTeam = entity.getTeam();

        if (entityTeam == null) {
            return Minecraft.renderNames() && entity != minecraft.getCameraEntity() && visibleToClient
                && !entity.isVehicle();
        }

        var playerTeam = minecraft.player.getTeam();

        return switch (entityTeam.getNameTagVisibility()) {
            case ALWAYS -> visibleToClient;
            case NEVER -> false;
            case HIDE_FOR_OTHER_TEAMS -> playerTeam == null
                ? visibleToClient
                : entityTeam.isAlliedTo(
                playerTeam
            ) && (entityTeam.canSeeFriendlyInvisibles() || visibleToClient);
            case HIDE_FOR_OWN_TEAM ->
                playerTeam == null ? visibleToClient : !entityTeam.isAlliedTo(playerTeam) && visibleToClient;
        };
    }

    // Proxy method override for super.getBlockLightLevel external access.
    @Override
    public int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return super.getBlockLightLevel(entity, pos);
    }

    public AzEntityAnimator<T> getAnimator() {
        return azAnimator;
    }

    public float getScaleHeight() {
        return scaleHeight;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }
}
