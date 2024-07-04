package mod.azure.azurelib.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Render hook to inject AzureLib's ISTER rendering callback
 */
@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
	@Inject(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/tileentity/ItemStackTileEntityRenderer;render(Lnet/minecraft/item/ItemStack;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;II)V"), cancellable = true)
	public void itemModelHook(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, boolean bl, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int i, int j, IBakedModel bakedModel, CallbackInfo ci) {
		if (itemStack.getItem() instanceof GeoItem)
			RenderProvider.of(itemStack).getCustomRenderer().render(itemStack, poseStack, multiBufferSource, i, j);
	}
}
