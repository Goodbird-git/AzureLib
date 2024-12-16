package mod.azure.azurelib.core2.util.eda;

import net.minecraft.nbt.CompoundTag;

public class EDARefSerializers {

    public static final EDASerializer<Boolean> BOOLEAN = new EDASerializer<>() {

        @Override
        public Boolean deserialize(CompoundTag compoundTag, String name) {
            return compoundTag.getBoolean(name);
        }

        @Override
        public void serialize(CompoundTag compoundTag, String name, Boolean value) {
            compoundTag.putBoolean(name, value);
        }
    };
}
