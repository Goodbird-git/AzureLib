package mod.azure.azurelib.animation;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.AzureLibException;
import mod.azure.azurelib.animation.cache.AzBakedAnimationCache;
import mod.azure.azurelib.animation.cache.AzBoneCache;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.primitive.AzBakedAnimation;
import mod.azure.azurelib.animation.primitive.AzBakedAnimations;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.model.AzBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

/**
 * The {@code AzAnimator} class is an abstract base class for managing animations for various types of objects such as
 * entities, blocks, or items. It provides a reusable structure for animating objects, allowing the integration of a
 * variety of animation controllers and custom animations.
 *
 * @param <T> The type of object this animator will animate (e.g., an entity, block entity, or item stack).
 */
public abstract class AzAnimator<T> {

    private final AzAnimationContext<T> reusableContext;

    // Holds animation controllers.
    private final AzAnimationControllerContainer<T> animationControllerContainer;

    public boolean reloadAnimations;

    protected AzAnimator() {
        this(AzAnimatorConfig.defaultConfig());
    }

    protected AzAnimator(AzAnimatorConfig config) {
        this.animationControllerContainer = new AzAnimationControllerContainer<>();

        AzBoneCache boneCache = new AzBoneCache();
        AzAnimationTimer timer = new AzAnimationTimer(config);

        this.reusableContext = new AzAnimationContext<>(boneCache, config, timer);
    }

    public abstract void registerControllers(AzAnimationControllerContainer<T> animationControllerContainer);

    public abstract @NotNull ResourceLocation getAnimationLocation(T animatable);

    public void animate(T animatable) {
        reusableContext.animatable = animatable;

        AzBoneCache boneCache = reusableContext.boneCache();
        AzAnimationTimer timer = reusableContext.timer();

        timer.tick();

        preAnimationSetup(animatable, timer.getAnimTime());

        if (!boneCache.isEmpty()) {

            for (AzAnimationController<T> controller : animationControllerContainer.getAll()) {
                controller.update(reusableContext);
            }

            this.reloadAnimations = false;

            boneCache.update(reusableContext);
        }

        setCustomAnimations(animatable);
    }

    /**
     * Apply transformations and settings prior to acting on any animation-related functionality
     */
    protected void preAnimationSetup(T animatable, double animTime) {
        applyMolangQueries(animatable, animTime);
    }

    protected void applyMolangQueries(T animatable, double animTime) {
        WorldClient level = Objects.requireNonNull(Minecraft.getMinecraft().world);
        MolangParser parser = MolangParser.INSTANCE;

        parser.setMemoizedValue(MolangQueries.LIFE_TIME, () -> animTime / 20d);
        /**
         * TODO; find replacement of getCountLoadedEntities
         */
        //parser.setMemoizedValue(MolangQueries.ACTOR_COUNT, level::getCountLoadedEntities);
        parser.setMemoizedValue(MolangQueries.TIME_OF_DAY, () -> level.getWorldTime() / 24000f);
        parser.setMemoizedValue(MolangQueries.MOON_PHASE, level::getMoonPhase);
    }

    /**
     * This method is called once per render frame for each {@link T animatable} being rendered.<br>
     * Override to set custom animations (such as head rotation, etc).
     *
     * @param animatable The {@code GeoAnimatable} instance currently being rendered
     */
    public void setCustomAnimations(T animatable) {}

    public void setActiveModel(AzBakedModel model) {
        boolean modelChanged = reusableContext.boneCache().setActiveModel(model);

        if (modelChanged) {
            // If the model changed, we need to clear the bone animation queue cache for every controller.
            // TODO: We shouldn't have to remember to do this. If the baked model changes, then the bone cache
            // should be re-instantiated. If the bone cache is re-instantiated, then so should the bone animation
            // queue caches.
            animationControllerContainer.getAll()
                .forEach(controller -> controller.boneAnimationQueueCache().clear());
        }
    }

    /**
     * Get the baked animation object used for rendering from the given resource path
     */
    public AzBakedAnimation getAnimation(T animatable, String name) {
        ResourceLocation location = getAnimationLocation(animatable);
        AzBakedAnimations bakedAnimations = AzBakedAnimationCache.getInstance().getNullable(location);

        if (bakedAnimations == null) {
            throw new AzureLibException(location, "Unable to find animation.");
        }

        return bakedAnimations.getAnimation(name);
    }

    public AzAnimationContext<T> context() {
        return reusableContext;
    }

    public AzAnimationControllerContainer<T> getAnimationControllerContainer() {
        return animationControllerContainer;
    }
}
