package mod.azure.azurelib.common.internal.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.common.api.client.renderer.GeoArmorRenderer;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayer<T extends LivingEntity, A extends HumanoidModel<T>> {
    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;usesInnerModel(Lnet/minecraft/world/entity/EquipmentSlot;)Z"), cancellable = true)
    public void geckolib$renderGeckoLibModel(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot equipmentSlot, int packedLight, A baseModel, CallbackInfo ci) {
        final ItemStack stack = entity.getItemBySlot(equipmentSlot);
        final Model geckolibModel = RenderProvider.of(stack).getGenericArmorModel(entity, stack, equipmentSlot,
                (HumanoidModel<LivingEntity>) baseModel);

        if (geckolibModel != null && stack.getItem() instanceof GeoItem) {
            if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer)
                geoArmorRenderer.prepForRender(entity, stack, equipmentSlot, baseModel);

            baseModel.copyPropertiesTo((A) geckolibModel);
            geckolibModel.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, 1);
            ci.cancel();
        }
    }
}