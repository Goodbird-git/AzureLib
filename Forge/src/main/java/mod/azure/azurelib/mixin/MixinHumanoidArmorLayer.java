/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.mixin;

import mod.azure.azurelib.render.armor.AzArmorModel;
import mod.azure.azurelib.render.armor.AzArmorRenderer;
import mod.azure.azurelib.render.armor.AzArmorRendererPipeline;
import mod.azure.azurelib.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public abstract class MixinHumanoidArmorLayer<T extends EntityLiving, A extends ModelBiped> {

    @Inject(method = "renderArmorLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/LayerArmorBase;isLegSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Z"), cancellable = true)
    public void azurelib$renderGeckoLibModel(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot equipmentSlot, CallbackInfo ci) {
        final ItemStack stack = entity.getItemStackFromSlot(equipmentSlot);
        if (stack.getItem() instanceof ItemArmor) {
            LayerArmorBase self = AzureLibUtil.<LayerArmorBase>self(this);
            ModelBiped baseModel = (ModelBiped) self.getModelFromSlot(equipmentSlot);
            AzArmorRenderer renderer = AzArmorRendererRegistry.getOrNull(stack.getItem());

            if (renderer != null) {
                AzArmorRendererPipeline rendererPipeline = renderer.rendererPipeline();
                AzArmorModel armorModel = rendererPipeline.armorModel();
                //ModelBase typedHumanoidModel = armorModel;

                renderer.prepForRender(entity, stack, equipmentSlot, baseModel);
                //baseModel.copyModelAngles(typedHumanoidModel);
                armorModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                ci.cancel();
            }
        }
    }
}
