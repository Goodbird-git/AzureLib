package mod.azure.azurelib.fabric.core2.example.entities.doomhunter;

import mod.azure.azurelib.core2.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;

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
