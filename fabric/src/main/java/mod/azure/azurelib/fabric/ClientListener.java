package mod.azure.azurelib.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.network.packet.*;
import mod.azure.azurelib.fabric.core2.example.DroneRenderer;
import mod.azure.azurelib.fabric.core2.example.ExampleEntityTypes;

public final class ClientListener implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (AzureLib.hasKeyBindsInitialized) {
            ClientUtils.RELOAD = new KeyMapping(
                "key.azurelib.reload",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.azurelib.binds"
            );
            KeyBindingHelper.registerKeyBinding(ClientUtils.RELOAD);
            ClientUtils.SCOPE = new KeyMapping(
                "key.azurelib.scope",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.azurelib.binds"
            );
            KeyBindingHelper.registerKeyBinding(ClientUtils.SCOPE);
            ClientUtils.FIRE_WEAPON = new KeyMapping(
                "key.azurelib.fire",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.azurelib.binds"
            );
            KeyBindingHelper.registerKeyBinding(ClientUtils.FIRE_WEAPON);
        }
        ClientPlayNetworking.registerGlobalReceiver(
            BlockEntityAnimTriggerPacket.TYPE,
            (packet, context) -> packet.handle()
        );
        ClientPlayNetworking.registerGlobalReceiver(
            BlockEntityAnimDataSyncPacket.TYPE,
            (packet, context) -> packet.handle()
        );
        ClientPlayNetworking.registerGlobalReceiver(EntityAnimTriggerPacket.TYPE, (packet, context) -> packet.handle());
        ClientPlayNetworking.registerGlobalReceiver(
            AzEntityAnimTriggerPacket.TYPE,
            (packet, context) -> packet.handle()
        );
        ClientPlayNetworking.registerGlobalReceiver(
            EntityAnimDataSyncPacket.TYPE,
            (packet, context) -> packet.handle()
        );
        ClientPlayNetworking.registerGlobalReceiver(AnimTriggerPacket.TYPE, (packet, context) -> packet.handle());
        ClientPlayNetworking.registerGlobalReceiver(AnimDataSyncPacket.TYPE, (packet, context) -> packet.handle());
        ClientPlayNetworking.registerGlobalReceiver(SendConfigDataPacket.TYPE, (packet, context) -> packet.handle());

        EntityRendererRegistry.register(ExampleEntityTypes.DRONE, DroneRenderer::new);
    }
}
