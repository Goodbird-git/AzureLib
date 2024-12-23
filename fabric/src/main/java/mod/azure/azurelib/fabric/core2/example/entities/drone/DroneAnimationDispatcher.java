package mod.azure.azurelib.fabric.core2.example.entities.drone;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;

public class DroneAnimationDispatcher {

    private final AzDispatchCommand attackClawCommand = AzDispatchCommand
        .create(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_CLAW_ANIMATION_NAME);

    private final AzDispatchCommand attackTailCommand = AzDispatchCommand
        .create(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_TAIL_ANIMATION_NAME);

    private final AzDispatchCommand crawlCommand = AzDispatchCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.CRAWL_ANIMATION_NAME,
        AzLoopType.LOOP
    );

    private final AzDispatchCommand crawlHoldCommand = AzDispatchCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.CRAWL_ANIMATION_NAME,
        AzLoopType.HOLD_ON_LAST_FRAME
    );

    private final AzDispatchCommand idleCommand = AzDispatchCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.IDLE_ANIMATION_NAME,
        AzLoopType.LOOP
    );

    private final AzDispatchCommand runCommand = AzDispatchCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.RUN_ANIMATION_NAME,
        AzLoopType.LOOP
    );

    private final AzDispatchCommand walkCommand = AzDispatchCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.WALK_ANIMATION_NAME,
        AzLoopType.LOOP
    );

    private final Drone drone;

    public DroneAnimationDispatcher(Drone drone) {
        this.drone = drone;
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
        // TODO:
        // dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.SWIM_ANIMATION_NAME);
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
