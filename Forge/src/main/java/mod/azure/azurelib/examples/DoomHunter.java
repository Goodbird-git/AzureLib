package mod.azure.azurelib.examples;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public class DoomHunter extends EntityMob implements AzAnimatorAccessor<Entity> {

    @Unique
    private AzAnimator<Entity> animator;

    @Override
    public void setAnimator(AzAnimator<Entity> animator) {
        this.animator = animator;
    }

    @Override
    public AzAnimator<Entity> getAnimatorOrNull() {
        return animator;
    }

    private final DoomHunterAnimationDispatcher animationDispatcher;

    public DoomHunter(World level) {
        super(level);
        this.animationDispatcher = new DoomHunterAnimationDispatcher(this);
        this.setSize(3.0F, 7.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            animationDispatcher.chainsaw();
        }
    }
}
