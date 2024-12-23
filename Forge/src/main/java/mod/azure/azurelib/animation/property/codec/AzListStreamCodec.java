package mod.azure.azurelib.animation.property.codec;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

public class AzListStreamCodec<T> implements StreamCodec<FriendlyByteBuf, List<T>> {

    private final StreamCodec<FriendlyByteBuf, T> codec;

    public AzListStreamCodec(StreamCodec<FriendlyByteBuf, T> codec) {
        this.codec = codec;
    }

    @Override
    public @NotNull List<T> decode(FriendlyByteBuf buf) {
        var size = buf.readByte();
        var list = new ArrayList<T>(size);

        for (int i = 0; i < size; i++) {
            list.add(codec.decode(buf));
        }

        return list;
    }

    @Override
    public void encode(FriendlyByteBuf buf, List<T> elements) {
        buf.writeByte(elements.size());
        elements.forEach(element -> codec.encode(buf, element));
    }
}