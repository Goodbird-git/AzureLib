package mod.azure.azurelib;

import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.network.AzureLibNetwork;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	public static final String VERSION = "1.0.0";
	public static volatile boolean hasInitialized;

	public static synchronized void initialize() {
		if (!hasInitialized) {
//			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AzureLibCache::registerReloadListener);
			AzureLibNetwork.init();
		}

		hasInitialized = true;
	}

	public static ResourceLocation modResource(String name) {
		return new ResourceLocation(MOD_ID, name);
	}
}
