package mod.azure.azurelib.fabric.core2.example.entities.drone;

import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

public class DroneAnimationRefs {

    public static final String FULL_BODY_CONTROLLER_NAME = "full_body";

    public static final String ATTACK_CLAW_ANIMATION_NAME = "animation.attackclaw";

    public static final String ATTACK_TAIL_ANIMATION_NAME = "animation.attacktail";

    public static final String CRAWL_ANIMATION_NAME = "animation.crawl";

    public static final String CRAWL_HOLD_ANIMATION_NAME = "animation.crawlhold";

    public static final String IDLE_ANIMATION_NAME = "animation.idle";

    public static final String RUN_ANIMATION_NAME = "animation.run";

    public static final String SWIM_ANIMATION_NAME = "animation.swim";

    public static final String WALK_ANIMATION_NAME = "animation.walk";

    public static final AzRawAnimation ATTACK_CLAW_ANIMATION = AzRawAnimation.begin()
        .then(ATTACK_CLAW_ANIMATION_NAME, AzLoopType.PLAY_ONCE);

    public static final AzRawAnimation ATTACK_TAIL_ANIMATION = AzRawAnimation.begin()
        .then(ATTACK_TAIL_ANIMATION_NAME, AzLoopType.PLAY_ONCE);

    public static final AzRawAnimation CRAWL_ANIMATION = AzRawAnimation.begin().thenLoop(CRAWL_ANIMATION_NAME);

    public static final AzRawAnimation CRAWL_HOLD_ANIMATION = AzRawAnimation.begin()
        .then(CRAWL_ANIMATION_NAME, AzLoopType.HOLD_ON_LAST_FRAME);

    public static final AzRawAnimation IDLE_ANIMATION = AzRawAnimation.begin().thenLoop(IDLE_ANIMATION_NAME);

    public static final AzRawAnimation RUN_ANIMATION = AzRawAnimation.begin().thenLoop(RUN_ANIMATION_NAME);

    public static final AzRawAnimation SWIM_ANIMATION = AzRawAnimation.begin().thenLoop(SWIM_ANIMATION_NAME);

    public static final AzRawAnimation WALK_ANIMATION = AzRawAnimation.begin().thenLoop(WALK_ANIMATION_NAME);
}
