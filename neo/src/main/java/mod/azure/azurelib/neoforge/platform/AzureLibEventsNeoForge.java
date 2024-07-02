package mod.azure.azurelib.neoforge.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.api.client.renderer.*;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import mod.azure.azurelib.common.platform.services.AzureEvents;
import mod.azure.azurelib.neoforge.event.GeoRenderEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.common.NeoForge;

public class AzureLibEventsNeoForge implements AzureEvents {
    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderLayers} event
     */
    @Override
    public void fireCompileBlockRenderLayers(GeoBlockRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Pre} event
     */
    @Override
    public boolean fireBlockPreRender(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Post} event
     */
    @Override
    public void fireBlockPostRender(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.CompileRenderLayers} event
     */
    @Override
    public void fireCompileArmorRenderLayers(GeoArmorRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event
     */
    @Override
    public boolean fireArmorPreRender(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Post} event
     */
    @Override
    public void fireArmorPostRender(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderLayers} event
     */
    @Override
    public void fireCompileEntityRenderLayers(GeoEntityRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Pre} event
     */
    @Override
    public boolean fireEntityPreRender(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Post} event
     */
    @Override
    public void fireEntityPostRender(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderLayers} event
     */
    @Override
    public void fireCompileReplacedEntityRenderLayers(GeoReplacedEntityRenderer<?, ?> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Pre} event
     */
    @Override
    public boolean fireReplacedEntityPreRender(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Post} event
     */
    @Override
    public void fireReplacedEntityPostRender(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderLayers} event
     */
    @Override
    public void fireCompileItemRenderLayers(GeoItemRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Pre} event
     */
    @Override
    public boolean fireItemPreRender(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Post} event
     */
    @Override
    public void fireItemPostRender(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderLayers} event
     */
    @Override
    public void fireCompileObjectRenderLayers(GeoObjectRenderer<?> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.CompileRenderLayers(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Pre} event
     */
    @Override
    public boolean fireObjectPreRender(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Post} event
     */
    @Override
    public void fireObjectPostRender(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }
}
