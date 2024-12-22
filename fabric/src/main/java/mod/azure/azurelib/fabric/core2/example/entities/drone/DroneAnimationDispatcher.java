package mod.azure.azurelib.fabric.core2.example.entities.drone;

import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.easing.AzEasingTypes;

public class DroneAnimationDispatcher extends AzAnimationDispatcher {

    private final AzDispatchCommand attackClawCommand = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_CLAW_ANIMATION_NAME);

    private final AzDispatchCommand attackTailCommand = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_TAIL_ANIMATION_NAME);

    private final AzDispatchCommand crawlCommand = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.CRAWL_ANIMATION_NAME);

    private final AzDispatchCommand crawlHoldCommand = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.CRAWL_HOLD_ANIMATION_NAME);

    private final AzDispatchCommand idleCommand;

    private final AzDispatchCommand runCommand = AzDispatchCommand
        .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.RUN_ANIMATION_NAME);

    private final AzDispatchCommand walkCommand;

    private final Drone drone;

    public DroneAnimationDispatcher(Drone drone) {
        super(drone);
        this.drone = drone;

        this.idleCommand = AzDispatchCommand.builder()
            .setEasingType(AzEasingTypes.random())
            .setSpeed(1 + (0.5F * drone.getRandom().nextFloat()))
            .setTransitionInSpeed(drone.getRandom().nextInt(7) + 3)
            .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.IDLE_ANIMATION_NAME)
            .build();

        this.walkCommand = AzDispatchCommand.builder()
            .setEasingType(AzEasingTypes.random())
            .setSpeed(2.5F)
            .setTransitionInSpeed(drone.getRandom().nextInt(7) + 3)
            .playAnimation(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.WALK_ANIMATION_NAME)
            .build();
    }

    public void clientCrawl() {
        AzDispatcher.fromClient(crawlCommand).sendForEntity(drone);
    }

    public void clientCrawlHold() {
        AzDispatcher.fromClient(crawlHoldCommand).sendForEntity(drone);
    }

    public void clientIdle() {
        AzDispatcher.fromClient(idleCommand).sendForEntity(drone);
    }

    public void clientRun() {
        AzDispatcher.fromClient(runCommand).sendForEntity(drone);
    }

    public void clientSwim() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.SWIM_ANIMATION_NAME);
    }

    public void clientWalk() {
        AzDispatcher.fromClient(walkCommand).sendForEntity(drone);
    }

    public void serverClawAttack() {
        AzDispatcher.fromServer(attackClawCommand).sendForEntity(drone);
    }

    public void serverTailAttack() {
        AzDispatcher.fromServer(attackTailCommand).sendForEntity(drone);
    }
}
