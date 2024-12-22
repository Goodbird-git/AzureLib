package mod.azure.azurelib.fabric.core2.example.entities.drone;

import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.easing.AzEasingTypes;

public class DroneAnimationDispatcher extends AzAnimationDispatcher {

    private static final AzDispatchCommand ATTACK_CLAW_COMMAND = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_CLAW_ANIMATION_NAME);

    private static final AzDispatchCommand ATTACK_TAIL_COMMAND = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_TAIL_ANIMATION_NAME);

    private static final AzDispatchCommand CRAWL_COMMAND = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.CRAWL_ANIMATION_NAME);

    private static final AzDispatchCommand CRAWL_HOLD_COMMAND = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.CRAWL_HOLD_ANIMATION_NAME);

    private static final AzDispatchCommand IDLE_COMMAND = AzDispatchCommand.builder()
        .setEasingType(AzEasingTypes.EASE_IN_OUT_SINE)
        .setSpeed(1)
        .setTransitionInSpeed(3)
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.IDLE_ANIMATION_NAME)
        .build();

    private static final AzDispatchCommand RUN_COMMAND = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.RUN_ANIMATION_NAME);

    private static final AzDispatchCommand WALK_COMMAND = AzDispatchCommand.builder()
        .setEasingType(AzEasingTypes.EASE_IN_OUT_QUAD)
        .setSpeed(2.5F)
        .setTransitionInSpeed(20)
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.WALK_ANIMATION_NAME)
        .build();

    private final Drone drone;

    public DroneAnimationDispatcher(Drone drone) {
        super(drone);
        this.drone = drone;
    }

    public void clientCrawl() {
        AzDispatcher.fromClient(CRAWL_COMMAND).sendForEntity(drone);
    }

    public void clientCrawlHold() {
        AzDispatcher.fromClient(CRAWL_HOLD_COMMAND).sendForEntity(drone);
    }

    public void clientIdle() {
        AzDispatcher.fromClient(IDLE_COMMAND).sendForEntity(drone);
    }

    public void clientRun() {
        AzDispatcher.fromClient(RUN_COMMAND).sendForEntity(drone);
    }

    public void clientSwim() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.SWIM_ANIMATION_NAME);
    }

    public void clientWalk() {
        AzDispatcher.fromClient(WALK_COMMAND).sendForEntity(drone);
    }

    public void serverClawAttack() {
        AzDispatcher.fromServer(ATTACK_CLAW_COMMAND).sendForEntity(drone);
    }

    public void serverTailAttack() {
        AzDispatcher.fromServer(ATTACK_TAIL_COMMAND).sendForEntity(drone);
    }
}
