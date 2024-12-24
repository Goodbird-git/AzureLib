package mod.azure.azurelib.network;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.network.packet.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Network handling class for AzureLib.<br>
 * Handles packet registration and some networking functions
 */
public final class AzureLibNetwork {
	private static final String VER = "1";
	private static final SimpleNetworkWrapper PACKET_CHANNEL = new SimpleNetworkWrapper(AzureLib.MOD_ID);

	private static final Map<String, GeoAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

	public static void init() {
		int id = 0;
		PACKET_CHANNEL.registerMessage(AzBlockEntityDispatchCommandPacket.class, AzBlockEntityDispatchCommandPacket.class, id++, Side.);
		PACKET_CHANNEL.registerMessage(AzEntityDispatchCommandPacket.class, AzEntityDispatchCommandPacket.class, id++, Side.);
		PACKET_CHANNEL.registerMessage(AzItemStackDispatchCommandPacket.class, AzItemStackDispatchCommandPacket.class, id++, Side.);
	}

	/**
	 * Registers a synced {@link GeoAnimatable} object for networking support.<br>
	 * It is recommended that you don't call this directly, instead implementing and calling {@link mod.azure.azurelib.animatable.SingletonGeoAnimatable#registerSyncedAnimatable}
	 */
	synchronized public static void registerSyncedAnimatable(GeoAnimatable animatable) {
		GeoAnimatable existing = SYNCED_ANIMATABLES.put(animatable.getClass().toString(), animatable);

		if (existing == null)
			AzureLib.LOGGER.debug("Registered SyncedAnimatable for " + animatable.getClass().toString());
	}

	/**
	 * Gets a registered synced {@link GeoAnimatable} object by name
	 * 
	 * @param className
	 */
	@Nullable
	public static GeoAnimatable getSyncedAnimatable(String className) {
		GeoAnimatable animatable = SYNCED_ANIMATABLES.get(className);

		if (animatable == null)
			AzureLib.LOGGER.error("Attempting to retrieve unregistered synced animatable! (" + className + ")");

		return animatable;
	}

	/**
	 * Send a packet using AzureLib's packet channel
	 */

	public static void sendToPlayer(IMessage message, EntityPlayer player) {
		PACKET_CHANNEL.sendTo(message, (EntityPlayerMP) player);
	}

	public static void sendToAll(IMessage message) {
		PACKET_CHANNEL.sendToAll(message);
	}

	public static void sendToServer(IMessage message) {
		PACKET_CHANNEL.sendToServer(message);
	}

	public static void sendToAllTracking(IMessage message, Entity entity) {
		PACKET_CHANNEL.sendToAllTracking(message, entity);
	}

	public static void sendToAllTracking(IMessage message, World world, BlockPos blockPos) {
		PACKET_CHANNEL.sendToAllTracking(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 128));
	}
}
