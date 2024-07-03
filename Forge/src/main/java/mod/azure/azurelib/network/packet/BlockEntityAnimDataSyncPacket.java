/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.network.packet;

import java.util.function.Supplier;

import mod.azure.azurelib.animatable.GeoBlockEntity;
import mod.azure.azurelib.constant.DataTickets;
import mod.azure.azurelib.network.SerializableDataTicket;
import mod.azure.azurelib.util.ClientUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * Packet for syncing user-definable animation data for {@link BlockEntity BlockEntities}
 */
public class BlockEntityAnimDataSyncPacket<D> {
	private final BlockPos pos;
	private final SerializableDataTicket<D> dataTicket;
	private final D data;

	public BlockEntityAnimDataSyncPacket(BlockPos pos, SerializableDataTicket<D> dataTicket, D data) {
		this.pos = pos;
		this.dataTicket = dataTicket;
		this.data = data;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buffer);
	}

	public static <D> BlockEntityAnimDataSyncPacket<D> decode(FriendlyByteBuf buffer) {
		BlockPos pos = buffer.readBlockPos();
		SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>)DataTickets.byName(buffer.readUtf());

		return new BlockEntityAnimDataSyncPacket<>(pos, dataTicket, dataTicket.decode(buffer));
	}

	public void receivePacket(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context handler = context.get();

		handler.enqueueWork(() -> {
			BlockEntity blockEntity = ClientUtils.getLevel().getBlockEntity(this.pos);

			if (blockEntity instanceof GeoBlockEntity geoBlockEntity)
				geoBlockEntity.setAnimData(this.dataTicket, this.data);
		});
		handler.setPacketHandled(true);
	}
}
