package mod.azure.azurelib.animation.primitive;

import com.google.gson.JsonElement;
import mod.azure.azurelib.animation.controller.AzAnimationController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loop type functional interface to define post-play handling for a given animation. <br>
 * Custom loop types are supported by extending this class and providing the extended class instance as the loop type
 * for the animation
 */
@FunctionalInterface
public interface AzLoopType {

    Map<String, AzLoopType> LOOP_TYPES = new ConcurrentHashMap<>(4);

    /**
     * Represents the default looping behavior for animations. This implementation dynamically evaluates whether an
     * animation should repeat or stop based on the current animation's loop type. The default behavior is determined by
     * delegating to the loop type of the current animation. It invokes the {@code shouldPlayAgain} method of the
     * current animation's loop type, passing the animatable object, the animation controller, and the current animation
     * as arguments. This variable is used as the standard looping mechanism unless explicitly overridden by a custom
     * loop type.
     */
    AzLoopType DEFAULT = (animatable, controller, currentAnimation) -> currentAnimation.loopType()
        .shouldPlayAgain(animatable, controller, currentAnimation);

    /**
     * A predefined {@code AzLoopType} that indicates an animation should play only once without repeating. This loop
     * type is used to configure animations that are intended to run a single time and stop when completed, instead of
     * looping or holding on the last frame. The associated logic for this type always returns {@code false} for the
     * repeat condition, preventing the animation from replaying once it has finished.
     */
    AzLoopType PLAY_ONCE = register(
        "play_once",
        register("false", (animatable, controller, currentAnimation) -> false)
    );

    /**
     * A pre-defined AzLoopType representing the behavior of holding on the last frame of an animation after it
     * completes.
     * <p>
     * When used as the loop type for an animation, the animation will pause on its final frame. The `pause()` method of
     * the controller's state machine ensures this behavior. This is useful when the desired outcome is for the
     * animation to display its last frame persistently without replaying or resetting.
     * <p>
     * Implementation Details: - Utilizes the `controller.getStateMachine().pause()` method to halt the animation. -
     * Returns `true`, indicating the controller should not proceed to the next state or reset/replay the animation.
     */
    AzLoopType HOLD_ON_LAST_FRAME = register("hold_on_last_frame", (animatable, controller, currentAnimation) -> {
        controller.stateMachine().pause();

        return true;
    });

    /**
     * Represents a preconfigured {@code AzLoopType} that determines the looping behavior of an animation. The
     * {@code LOOP} type is designed to always repeat the animation, ensuring that the looping condition remains
     * consistently true. This constant is registered with AzureLib's loop handler using the static {@code register}
     * method. When used, it applies a behavior where the animation will indefinitely loop, regardless of any dynamic
     * runtime evaluations about the animatable object or animation state.
     */
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

        com.google.gson.JsonPrimitive primitive = json.getAsJsonPrimitive();

        if (primitive.isBoolean()) {
            return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;
        }

        if (primitive.isString()) {
            return fromString(primitive.getAsString());
        }

        return PLAY_ONCE;
    }

    /**
     * Retrieves an AzLoopType instance based on the given name. If the name does not match any registered loop type,
     * the default {@link AzLoopType#PLAY_ONCE} is returned.
     *
     * @param name The name of the loop type to retrieve.
     * @return The corresponding AzLoopType instance, or the default AzLoopType if no match is found.
     */
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
        AzBakedAnimation currentAnimation
    );
}
