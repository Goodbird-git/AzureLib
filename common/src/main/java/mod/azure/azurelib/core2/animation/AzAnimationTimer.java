package mod.azure.azurelib.core2.animation;

import net.minecraft.client.Minecraft;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;

public class AzAnimationTimer {

    private final boolean shouldPlayAnimationsWhileGamePaused;

    // Remnants from AnimatableManager.
    private double lastUpdateTime;

    private boolean isFirstTick = true;

    private double firstTickTime = -1;

    // Remnants from GeoModel.
    private double animTime;

    private double lastGameTickTime;

    public AzAnimationTimer(boolean shouldPlayAnimationsWhileGamePaused) {
        this.shouldPlayAnimationsWhileGamePaused = shouldPlayAnimationsWhileGamePaused;
    }

    public void tick() {
        var minecraft = Minecraft.getInstance();
        // TODO: If encountering rendering smoothness issues, break glass (this used to be a DataTickets.TICK fetch).
        var currentTick = RenderUtils.getCurrentTick();

        if (firstTickTime == -1) {
            firstTickTime = currentTick + minecraft.getTimer().getGameTimeDeltaTicks();
        }

        double currentFrameTime = currentTick - firstTickTime;
        boolean isReRender = !isFirstTick && currentFrameTime == lastUpdateTime;

        // TODO: Figure out why this was here to begin with.
        // if (isReRender && instanceId == this.lastRenderedInstance) {
        // return;
        // }

        if (!isReRender && (!minecraft.isPaused() || shouldPlayAnimationsWhileGamePaused)) {
            this.lastUpdateTime = currentFrameTime;

            this.animTime += lastUpdateTime - this.lastGameTickTime;
            this.lastGameTickTime = lastUpdateTime;
        }
    }

    public double getAnimTime() {
        return animTime;
    }

    public boolean isFirstTick() {
        return this.isFirstTick;
    }

    protected void finishFirstTick() {
        this.isFirstTick = false;
    }
}
