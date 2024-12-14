package mod.azure.azurelib.core2.animation.primitive;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mod.azure.azurelib.core2.animation.controller.AzAnimationController;

/**
 * Loop type functional interface to define post-play handling for a given animation. <br>
 * Custom loop types are supported by extending this class and providing the extended class instance as the loop type
 * for the animation
 */
@FunctionalInterface
public interface AzLoopType {

    Map<String, AzLoopType> LOOP_TYPES = new ConcurrentHashMap<>(4);

    AzLoopType DEFAULT = (animatable, controller, currentAnimation) -> currentAnimation.loopType()
        .shouldPlayAgain(animatable, controller, currentAnimation);

    AzLoopType PLAY_ONCE = register(
        "play_once",
        register("false", (animatable, controller, currentAnimation) -> false)
    );

    AzLoopType HOLD_ON_LAST_FRAME = register("hold_on_last_frame", (animatable, controller, currentAnimation) -> {
        controller.getStateMachine().pause();

        return true;
    });

    AzLoopType LOOP = register("loop", register("true", (animatable, controller, currentAnimation) -> true));

    /**
     * Retrieve a AzLoopType instance based on a {@link JsonElement}. Returns either {@link AzLoopType#PLAY_ONCE} or
     * {@link AzLoopType#LOOP} based on a boolean or string element type, or any other registered loop type with a
     * matching type string.
     *
     * @param json The <code>loop</code> {@link JsonElement} to attempt to parse
     * @return A usable AzLoopType instance
     */
    static AzLoopType fromJson(JsonElement json) {
        if (json == null || !json.isJsonPrimitive()) {
            return PLAY_ONCE;
        }

        var primitive = json.getAsJsonPrimitive();

        if (primitive.isBoolean()) {
            return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;
        }

        if (primitive.isString()) {
            return fromString(primitive.getAsString());
        }

        return PLAY_ONCE;
    }

    static AzLoopType fromString(String name) {
        return LOOP_TYPES.getOrDefault(name, PLAY_ONCE);
    }

    /**
     * Register a AzLoopType with AzureLib for handling loop functionality of animations..<br>
     * <b><u>MUST be called during mod construct</u></b><br>
     * It is recommended you don't call this directly, and instead call it via {@code AzureLibUtil#addCustomLoopType}
     *
     * @param name     The name of the loop type
     * @param loopType The loop type to register
     * @return The registered {@code AzLoopType}
     */
    static AzLoopType register(String name, AzLoopType loopType) {
        LOOP_TYPES.put(name, loopType);

        return loopType;
    }

    /**
     * Override in a custom instance to dynamically decide whether an animation should repeat or stop
     *
     * @param animatable       The animating object relevant to this method call
     * @param controller       The {@link AzAnimationController} playing the current animation
     * @param currentAnimation The current animation that just played
     * @return Whether the animation should play again, or stop
     */
    boolean shouldPlayAgain(
        Object animatable,
        AzAnimationController<?> controller,
        AzAnimation currentAnimation
    );
}
