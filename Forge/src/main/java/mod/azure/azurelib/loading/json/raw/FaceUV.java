/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.loading.json.raw;

import net.minecraft.util.JSONUtils;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mod.azure.azurelib.util.JsonUtil;

/**
 * Container class for face UV information, only used in deserialization at startup
 */
public class FaceUV {
	private final String materialInstance;
	private final double[] uv;
	private final double[] uvSize;

	public FaceUV(String materialInstance, double[] uv, double[] uvSize) {
		this.materialInstance = materialInstance;
		this.uv = uv;
		this.uvSize = uvSize;
	}

	public String materialInstance() {
		return materialInstance;
	}

	public double[] uv() {
		return uv;
	}

	public double[] uvSize() {
		return uvSize;
	}

	public static JsonDeserializer<FaceUV> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			String materialInstance = JSONUtils.getString(obj, "material_instance", null);
			double[] uv = JsonUtil.jsonArrayToDoubleArray(JSONUtils.getJsonArray(obj, "uv", null));
			double[] uvSize = JsonUtil.jsonArrayToDoubleArray(JSONUtils.getJsonArray(obj, "uv_size", null));

			return new FaceUV(materialInstance, uv, uvSize);
		};
	}
}
