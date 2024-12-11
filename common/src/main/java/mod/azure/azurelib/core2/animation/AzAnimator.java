package mod.azure.azurelib.core2.animation;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.AzureLibException;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core2.animation.cache.AzBakedAnimationCache;
import mod.azure.azurelib.core2.animation.cache.AzBoneSnapshotCache;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.primitive.AzAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AzAnimator<T> {

    // Remnants from AnimatableManager.
    private final AzAnimationControllerContainer<T> animationControllerContainer;
    private final AzAnimationProcessor<T> animationProcessor;
    private final AzBoneSnapshotCache boneSnapshotCache;

    // Remnants from AnimatableManager.
    private double lastUpdateTime;
    private boolean isFirstTick = true;
    private double firstTickTime = -1;

    // Remnants from GeoModel.
    private double animTime;
    private double lastGameTickTime;

    protected AzAnimator() {
        this.animationControllerContainer = new AzAnimationControllerContainer<>();
        this.animationProcessor = new AzAnimationProcessor<>(this);
        this.boneSnapshotCache = new AzBoneSnapshotCache();
    }

    public abstract void registerControllers(AzAnimationControllerContainer<T> animationControllerContainer);
    public abstract @NotNull ResourceLocation getAnimationLocation(T animatable);

    public void animate(T animatable, AzAnimationState<T> animationState) {
        var minecraft = Minecraft.getInstance();
        // TODO: If encountering rendering smoothness issues, break glass (this used to be a DataTickets.TICK fetch).
        var currentTick = RenderUtils.getCurrentTick();

        if (firstTickTime == -1) {
            firstTickTime = currentTick + minecraft.getTimer().getGameTimeDeltaTicks();
        }

        double currentFrameTime = currentTick - firstTickTime;
        boolean isReRender = !isFirstTick && currentFrameTime == lastUpdateTime;

        // TODO: Figure out why this was here to begin with.
//        if (isReRender && instanceId == this.lastRenderedInstance) {
//            return;
//        }

        if (!isReRender && (!minecraft.isPaused() || shouldPlayAnimsWhileGamePaused())) {
            this.lastUpdateTime = currentFrameTime;

            this.animTime += lastUpdateTime - this.lastGameTickTime;
            this.lastGameTickTime = lastUpdateTime;
        }

        animationState.animationTick = this.animTime;

        preAnimationSetup(animatable, this.animTime);

        if (!animationProcessor.getRegisteredBones().isEmpty()) {
            animationProcessor.tickAnimation(animatable, animationState);
        }

        setCustomAnimations(animatable, animationState);
    }

    /**
     * Apply transformations and settings prior to acting on any animation-related functionality
     */
    protected void preAnimationSetup(T animatable, double animTime) {
        applyMolangQueries(animatable, animTime);
    }

    protected void applyMolangQueries(T animatable, double animTime) {
        var parser = MolangParser.INSTANCE;
        var minecraft = Minecraft.getInstance();
        // TODO: See if there's a better way to null-check here.
        var level = Objects.requireNonNull(minecraft.level);

        parser.setMemoizedValue(MolangQueries.LIFE_TIME, () -> animTime / 20d);
        parser.setMemoizedValue(MolangQueries.ACTOR_COUNT, level::getEntityCount);
        parser.setMemoizedValue(MolangQueries.TIME_OF_DAY, () -> level.getDayTime() / 24000f);
        parser.setMemoizedValue(MolangQueries.MOON_PHASE, level::getMoonPhase);
    }

    /**
     * This method is called once per render frame for each {@link T animatable} being rendered.<br>
     * Override to set custom animations (such as head rotation, etc).
     *
     * @param animatable     The {@code GeoAnimatable} instance currently being rendered
     * @param animationState An {@link AzAnimationState} instance created to hold animation data for the
     *                       {@code animatable} for this method call
     */
    public void setCustomAnimations(T animatable, AzAnimationState<T> animationState) {}

    /**
     * Defines whether the animations for this animator should continue playing in the background when the game is
     * paused.<br>
     * By default, animation progress will be stalled while the game is paused.
     */
    public boolean shouldPlayAnimsWhileGamePaused() {
        return false;
    }

    /**
     * Override this and return true if AzureLib should crash when attempting to animate the model, but fails to find a
     * bone.<br>
     * By default, AzureLib will just gracefully ignore a missing bone, which might cause oddities with incorrect models
     * or mismatching variables.<br>
     */
    public boolean crashIfBoneMissing() {
        return false;
    }

    /**
     * Get the baked animation object used for rendering from the given resource path
     */
    public AzAnimation getAnimation(T animatable, String name) {
        var location = getAnimationLocation(animatable);
        var bakedAnimations = AzBakedAnimationCache.getInstance().getNullable(location);

        if (bakedAnimations == null) {
            throw new AzureLibException(location, "Unable to find animation.");
        }

        return bakedAnimations.getAnimation(name);
    }

    public AzAnimationControllerContainer<T> getAnimationControllerContainer() {
        return animationControllerContainer;
    }

    public AzAnimationProcessor<T> getAnimationProcessor() {
        return animationProcessor;
    }

    public double getAnimTime() {
        return animTime;
    }

    /**
     * Defines the speed in which the {@link AzAnimationProcessor} should return {@link CoreGeoBone GeoBones} that
     * currently have no animations to their default position.
     */
    public double getBoneResetTime() {
        return 1;
    }

    public AzBoneSnapshotCache getBoneSnapshotCache() {
        return boneSnapshotCache;
    }

    public boolean isFirstTick() {
        return this.isFirstTick;
    }

    protected void finishFirstTick() {
        this.isFirstTick = false;
    }
}
