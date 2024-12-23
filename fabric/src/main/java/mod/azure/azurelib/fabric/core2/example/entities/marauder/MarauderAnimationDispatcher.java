package mod.azure.azurelib.fabric.core2.example.entities.marauder;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.fabric.core2.example.entities.doomhunter.DoomHunter;

public class MarauderAnimationDispatcher {

    private final AzDispatchCommand deathCommand = AzDispatchCommand.create("base_controller", "death", AzLoopType.HOLD_ON_LAST_FRAME);

    private final AzDispatchCommand idleCommand = AzDispatchCommand.create("base_controller", "idle", AzLoopType.LOOP);

    private final AzDispatchCommand runCommand = AzDispatchCommand.create("base_controller", "run", AzLoopType.LOOP);

    private final AzDispatchCommand spawnCommand = AzDispatchCommand.create("base_controller", "spawn", AzLoopType.PLAY_ONCE);

    private final AzDispatchCommand walkCommand = AzDispatchCommand.create("base_controller", "walk", AzLoopType.LOOP);

    private final MarauderEntity marauder;

    public MarauderAnimationDispatcher(MarauderEntity marauder) {
        this.marauder = marauder;
    }

    public void clientDeath() {
        AzDispatcher.fromClient(deathCommand).sendForEntity(marauder);
    }

    public void clientIdle() {
        AzDispatcher.fromClient(idleCommand).sendForEntity(marauder);
    }

    public void clientRun() {
        AzDispatcher.fromClient(runCommand).sendForEntity(marauder);
    }

    public void clientSpawn() {
        AzDispatcher.fromClient(spawnCommand).sendForEntity(marauder);
    }

    public void clientWalk() {
        AzDispatcher.fromClient(walkCommand).sendForEntity(marauder);
    }
}
