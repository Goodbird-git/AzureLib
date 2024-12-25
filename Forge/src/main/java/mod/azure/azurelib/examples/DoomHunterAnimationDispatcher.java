package mod.azure.azurelib.examples;

import mod.azure.azurelib.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.animation.primitive.AzLoopType;

public class DoomHunterAnimationDispatcher {

    private static final AzCommand CHAINSAW = AzCommand
        .create("base_controller", "chainsaw", AzLoopType.LOOP);

    private final DoomHunter doomHunter;

    public DoomHunterAnimationDispatcher(DoomHunter doomHunter) {
        this.doomHunter = doomHunter;
    }

    public void chainsaw() {
        CHAINSAW.sendForEntity(doomHunter);
    }
}
