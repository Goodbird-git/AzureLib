package mod.azure.azurelib;

import mod.azure.azurelib.cache.AzResourceCache;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.FutureTask;

/**
 * Base class for AzureLib!<br>
 * Hello World!<br>
 * There's not much to really see here, but feel free to stay a while and have a snack or something.
 * @see mod.azure.azurelib.util.AzureLibUtil
 */
public class AzureLib {
	public static Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "azurelib";
	public static final String NAME = "AzureLib";
	public static final String VERSION = "3.0.0";
	public static volatile boolean hasInitialized;

	public static synchronized void initialize() {
		if (!hasInitialized) {
			FMLCommonHandler.callFuture(new FutureTask<>(() -> {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
					AzResourceCache.registerReloadListener();
				}
			}, null));
		}

		hasInitialized = true;
	}

	public static ResourceLocation modResource(String name) {
		return new ResourceLocation("azurelib", name);
	}
}
