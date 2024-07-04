/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import mod.azure.azurelib.util.JsonUtil;

/**
 * Container class for locator value information, only used in deserialization at startup
 */
public class LocatorValue {
	private final LocatorClass locatorClass;
	private final double[] values;

	public LocatorValue(LocatorClass locatorClass, double[] values) {
		this.locatorClass = locatorClass;
		this.values = values;
	}

	public LocatorClass locatorClass() {
		return locatorClass;
	}

	public double[] values() {
		return values;
	}

	public static JsonDeserializer<LocatorValue> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			if (json.isJsonArray()) {
				return new LocatorValue(null, JsonUtil.jsonArrayToDoubleArray(json.getAsJsonArray()));
			}
			else if (json.isJsonObject()) {
				return new LocatorValue(context.deserialize(json.getAsJsonObject(), LocatorClass.class), new double[0]);
			}
			else {
				throw new JsonParseException("Invalid format for LocatorValue in json");
			}
		};
	}
}
