package mod.azure.azurelib.core2.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import mod.azure.azurelib.common.internal.common.AzureLibException;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core2.animation.cache.AzBakedAnimationCache;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.primitive.AzAnimation;

public abstract class AzAnimator<T> {

    // Holds general properties for the animator and its controllers.
    private final AzAnimatorConfig config;

    // Holds animation controllers.
    private final AzAnimationControllerContainer<T> animationControllerContainer;

    // Processes animations.
    private final AzAnimationProcessor<T> animationProcessor;

    // Tracks animation time.
    private final AzAnimationTimer timer;

    protected AzAnimator(AzAnimatorConfig config) {
        this.config = config;
        this.animationControllerContainer = new AzAnimationControllerContainer<>();
        this.animationProcessor = new AzAnimationProcessor<>(this);
        this.timer = new AzAnimationTimer(config);
    }

    public abstract void registerControllers(AzAnimationControllerContainer<T> animationControllerContainer);

    public abstract @NotNull ResourceLocation getAnimationLocation(T animatable);

    public void animate(T animatable) {
        timer.tick();

        preAnimationSetup(animatable, timer.getAnimTime());

        var minecraft = Minecraft.getInstance();
        var shouldRun = !minecraft.isPaused() || config.shouldPlayAnimationsWhileGamePaused();

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

    public AzAnimatorConfig config() {
        return config;
    }

    public boolean isFirstTick() {
        return timer.isFirstTick();
    }

    protected void finishFirstTick() {
        timer.finishFirstTick();
    }
}
