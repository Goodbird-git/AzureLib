package mod.azure.azurelib.loading.json.raw;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mod.azure.azurelib.loading.json.FormatVersion;
import mod.azure.azurelib.util.JsonUtil;
import net.minecraft.util.JSONUtils;

/**
 * Container class for model information, only used in deserialization at startup
 */
public class Model {
	
	protected final @Nullable FormatVersion formatVersion;
	protected final MinecraftGeometry[] minecraftGeometry;
	
	public Model(@Nullable FormatVersion formatVersion, MinecraftGeometry[] minecraftGeometry) {
		this.formatVersion = formatVersion;
		this.minecraftGeometry = minecraftGeometry;
	}
	
	public static JsonDeserializer<Model> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			JsonObject obj = json.getAsJsonObject();
			FormatVersion formatVersion = context.deserialize(obj.get("format_version"), FormatVersion.class);
			MinecraftGeometry[] minecraftGeometry = JsonUtil.jsonArrayToObjectArray(JSONUtils.getAsJsonArray(obj, "minecraft:geometry", new JsonArray()), context, MinecraftGeometry.class);

			return new Model(formatVersion, minecraftGeometry);
		};
	}
	
	@Nullable
	public FormatVersion formatVersion() {
		return this.formatVersion;
	}
	
	public MinecraftGeometry[] minecraftGeometry() {
		return this.minecraftGeometry;
	}
}
