package mod.azure.azurelib.common.internal.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface AbstractPacket extends CustomPacketPayload {

    void handle();
}
