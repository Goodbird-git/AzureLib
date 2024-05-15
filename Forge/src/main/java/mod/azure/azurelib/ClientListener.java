package mod.azure.azurelib;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.glfw.GLFW;
import mod.azure.azurelib.testing.TestingEntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = AzureLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientListener {

	@SubscribeEvent
	public static void registerRenderers(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(AzureLibMod.GEO_EXAMPLE_ENTITY.get(),
				TestingEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerKeys(final FMLClientSetupEvent  event) {
		Keybindings.RELOAD = new KeyBinding("key.azurelib.reload", GLFW.GLFW_KEY_R, "category.azurelib.binds");
		ClientRegistry.registerKeyBinding(Keybindings.RELOAD);
	}
}
