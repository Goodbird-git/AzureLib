package mod.azure.azurelib.fabric.core2.example.entities.doomhunter;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;

public class DoomHunterAnimationDispatcher {

    private final AzDispatchCommand chainsawCommand = AzDispatchCommand
        .create("base_controller", "chainsaw", AzLoopType.LOOP);

    private final DoomHunter doomHunter;

    public DoomHunterAnimationDispatcher(DoomHunter doomHunter) {
        this.doomHunter = doomHunter;
    }

    public void clientChainsaw() {
        AzDispatcher.fromClient(chainsawCommand).sendForEntity(doomHunter);
    }
}
