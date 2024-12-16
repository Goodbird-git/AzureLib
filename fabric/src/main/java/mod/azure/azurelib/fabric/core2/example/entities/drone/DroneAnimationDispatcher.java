package mod.azure.azurelib.fabric.core2.example.entities.drone;

import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;

public class DroneAnimationDispatcher extends AzAnimationDispatcher {

    public DroneAnimationDispatcher(Drone entity) {
        super(entity);
    }

    public void clientCrawl() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.CRAWL_ANIMATION_NAME);
    }

    public void clientCrawlHold() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.CRAWL_HOLD_ANIMATION_NAME);
    }

    public void clientIdle() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.IDLE_ANIMATION_NAME);
    }

    public void clientRun() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.RUN_ANIMATION_NAME);
    }

    public void clientSwim() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.SWIM_ANIMATION_NAME);
    }

    public void clientWalk() {
        dispatchFromClient(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.WALK_ANIMATION_NAME);
    }

    public void serverClawAttack() {
        dispatchFromServer(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_CLAW_ANIMATION_NAME);
    }

    public void serverTailAttack() {
        dispatchFromServer(DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME, DroneAnimationRefs.ATTACK_TAIL_ANIMATION_NAME);
    }
}
