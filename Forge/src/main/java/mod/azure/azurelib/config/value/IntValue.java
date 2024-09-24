package mod.azure.azurelib.config.value;

import mod.azure.azurelib.config.ConfigUtils;
import mod.azure.azurelib.config.adapter.TypeAdapter;
import mod.azure.azurelib.config.exception.ConfigValueMissingException;
import mod.azure.azurelib.config.format.IConfigFormat;
import net.minecraft.network.PacketBuffer;

import java.lang.reflect.Field;

public final class IntValue extends IntegerValue<Integer> {

    public IntValue(ValueData<Integer> valueData) {
        super(valueData, Range.unboundedInt());
    }

    @Override
    public Integer getCorrectedValue(Integer in) {
        if (this.range == null)
            return in;
        if (!this.range.isWithin(in)) {
            int corrected = this.range.clamp(in);
            ConfigUtils.logCorrectedMessage(this.getId(), in, corrected);
            return corrected;
        }
        return in;
    }

    @Override
    public void serialize(IConfigFormat format) {
        format.writeInt(this.getId(), this.get());
    }

    @Override
    public void deserialize(IConfigFormat format) throws ConfigValueMissingException {
        this.set(format.readInt(this.getId()));
    }

    public static final class Adapter extends TypeAdapter {

        @Override
        public ConfigValue<?> serialize(String name, String[] comments, Object value, TypeSerializer serializer, AdapterContext context) {
            return new IntValue(ValueData.of(name, (int) value, context, comments));
        }

        @Override
        public void encodeToBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            buffer.writeInt((Integer) value.get());
        }

        @Override
        public Object decodeFromBuffer(ConfigValue<?> value, PacketBuffer buffer) {
            return buffer.readInt();
        }

        @Override
        public void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
            field.setInt(instance, (Integer) value);
        }
    }
}
