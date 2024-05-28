package mod.azure.azurelib.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.common.internal.common.util.IncompatibleModsCheck;
import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelib.fabric.network.Networking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

public final class ClientListener implements ClientModInitializer {

    private static final Set<String> MYGUNMODS = ObjectOpenHashSet.of(
            "doom", "hwg", "arachnids", "aftershock", "mchalo", "avp"
    );

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(IncompatibleModsCheck::warnings);
        if (MYGUNMODS.stream().anyMatch(FabricLoader.getInstance()::isModLoaded)) {
            ClientUtils.RELOAD = new KeyMapping("key.azurelib.reload", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R,
                    "category.azurelib.binds");
            KeyBindingHelper.registerKeyBinding(ClientUtils.RELOAD);
            ClientUtils.SCOPE = new KeyMapping("key.azurelib.scope", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT,
                    "category.azurelib.binds");
            KeyBindingHelper.registerKeyBinding(ClientUtils.SCOPE);
            ClientUtils.FIRE_WEAPON = new KeyMapping("key.azurelib.fire", InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "category.azurelib.binds");
            KeyBindingHelper.registerKeyBinding(ClientUtils.FIRE_WEAPON);
        }
        Networking.PacketRegistry.registerClient();
        Services.NETWORK.registerClientReceiverPackets();
    }
}
