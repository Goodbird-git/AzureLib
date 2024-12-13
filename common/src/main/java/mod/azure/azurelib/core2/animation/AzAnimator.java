package mod.azure.azurelib.core2.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import mod.azure.azurelib.common.internal.common.AzureLibException;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core2.animation.cache.AzBakedAnimationCache;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.primitive.AzAnimation;

public abstract class AzAnimator<T> {

    // Holds animation controllers.
    private final AzAnimationControllerContainer<T> animationControllerContainer;
    // Processes animations.
    private final AzAnimationProcessor<T> animationProcessor;
    // Tracks animation time.
    private final AzAnimationTimer timer;

    protected AzAnimator() {
        this.animationControllerContainer = new AzAnimationControllerContainer<>();
        this.animationProcessor = new AzAnimationProcessor<>(this);
        this.timer = new AzAnimationTimer(shouldPlayAnimsWhileGamePaused());
    }

    public abstract void registerControllers(AzAnimationControllerContainer<T> animationControllerContainer);

    public abstract @NotNull ResourceLocation getAnimationLocation(T animatable);

    public void animate(T animatable) {
        timer.tick();

        preAnimationSetup(animatable, timer.getAnimTime());

        var minecraft = Minecraft.getInstance();
        var shouldRun = !minecraft.isPaused() || shouldPlayAnimsWhileGamePaused();

        if (shouldRun && !animationProcessor.getBoneCache().getRegisteredBones().isEmpty()) {
            animationProcessor.update(animatable);
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
        var level = Objects.requireNonNull(Minecraft.getInstance().level);
        var parser = MolangParser.INSTANCE;

        parser.setMemoizedValue(MolangQueries.LIFE_TIME, () -> animTime / 20d);
        parser.setMemoizedValue(MolangQueries.ACTOR_COUNT, level::getEntityCount);
        parser.setMemoizedValue(MolangQueries.TIME_OF_DAY, () -> level.getDayTime() / 24000f);
        parser.setMemoizedValue(MolangQueries.MOON_PHASE, level::getMoonPhase);
    }

    /**
     * This method is called once per render frame for each {@link T animatable} being rendered.<br>
     * Override to set custom animations (such as head rotation, etc).
     *
     * @param animatable The {@code GeoAnimatable} instance currently being rendered
     */
    public void setCustomAnimations(T animatable) {}

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
        return timer.getAnimTime();
    }

    /**
     * Defines the speed in which the {@link AzAnimationProcessor} should return {@link CoreGeoBone GeoBones} that
     * currently have no animations to their default position.
     */
    public double getBoneResetTime() {
        return 1;
    }

    public boolean isFirstTick() {
        return timer.isFirstTick();
    }

    protected void finishFirstTick() {
        timer.finishFirstTick();
    }
}
