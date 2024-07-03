/**
 * This class is a fork of the matching class found in the SmartBrainLib repository.
 * Original source: https://github.com/Tslat/SmartBrainLib
 * Copyright © 2024 Tslat.
 * Licensed under the MIT License.
 */
package mod.azure.azurelib.sblforked;

import java.util.ServiceLoader;

public class SBLConstants {
	public static final SBLLoader SBL_LOADER = ServiceLoader.load(SBLLoader.class).findFirst().get();
}