package mod.azure.azurelib.animation.dispatch;

import com.sun.istack.internal.NotNull;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Map;
import java.util.Objects;

/**
 * This enum represents the dispatch side for animation commands, which can either be client-side or server-side. It is
 * used as part of the AzureLib animation system for identifying where an animation command originates from or should be
 * executed.
 * <br>
 * Each enum constant has an associated unique identifier for easy lookup and transmission across the network. This
 * mapping is also used within codecs for serialization and deserialization purposes.
 */
public enum AzDispatchSide  {

    CLIENT(0),
    SERVER(1);

    private static final Map<Integer, AzDispatchSide> ID_TO_ENUM_MAP = new Int2ObjectArrayMap<>();

    static {
        // Populate the map for quick lookup
        for (AzDispatchSide side : values()) {
            ID_TO_ENUM_MAP.put(side.id, side);
        }
    }

    private final int id;

    AzDispatchSide(int id) {
        this.id = id;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, id, Integer.MAX_VALUE);
    }

    static AzDispatchSide fromBytes(ByteBuf buf) {
        return Objects.requireNonNull(ID_TO_ENUM_MAP.get((int) buf.readByte()));
    }

    public @NotNull String getSerializedName() {
        return name();
    }
}
