package mod.azure.azurelib.mixin;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Render hook for injecting AzureLib's armor rendering functionalities
 */
@Mixin(value = LayerArmorBase.class, priority = 700)
public abstract class MixinHumanoidArmorLayer<A extends ModelBase> implements LayerRenderer<EntityLivingBase> {

    @Shadow
    public abstract A getModelFromSlot(EntityEquipmentSlot slotIn);

    @Inject(method = "renderArmorLayer", at = @At(value = "RETURN", target = "Lnet/minecraft/client/renderer/entity/layers/ArmorLayer;isLegSlot(Lnet/minecraft/inventory/EquipmentSlotType;)Z"), cancellable = true)
    public void azurelib$renderAzureLibModel(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot equipmentSlot, CallbackInfo ci) {
        final ItemStack stack = entity.getItemStackFromSlot(equipmentSlot);
        if (stack.getItem() instanceof ItemArmor) {
            A baseModel = this.getModelFromSlot(equipmentSlot);
            final ModelBiped geckolibModel = RenderProvider.of(stack).getGenericArmorModel(entity, stack, equipmentSlot,
                    (ModelBiped) baseModel);

            if (geckolibModel != null && stack.getItem() instanceof GeoItem) {
                if (geckolibModel instanceof GeoArmorRenderer) {
                    GeoArmorRenderer geoArmorRenderer = (GeoArmorRenderer) geckolibModel;
                    geoArmorRenderer.prepForRender(entity, stack, equipmentSlot, baseModel);
                }

                baseModel.setModelAttributes((A) geckolibModel);
                geckolibModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
        }
    }
}
