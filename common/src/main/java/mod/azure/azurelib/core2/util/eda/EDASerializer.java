package mod.azure.azurelib.core2.util.eda;

import net.minecraft.nbt.CompoundTag;

public interface EDASerializer<T> {

    T deserialize(CompoundTag compoundTag, String name);

    void serialize(CompoundTag compoundTag, String name, T Value);
}
