package mod.azure.azurelib.core2.util.eda;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EDARef<T> {

    private final Entity entity;

    private final EntityDataAccessor<T> entityDataAccessor;

    private final @Nullable String name;

    private final @Nullable EDASerializer<T> serializer;

    private final @Nullable Consumer<T> setterCallback;

    private final @Nullable Runnable syncUpdateCallback;

    private EDARef(
        Entity entity,
        EntityDataAccessor<T> entityDataAccessor,
        @Nullable String name,
        @Nullable EDASerializer<T> serializer,
        @Nullable Consumer<T> setterCallback,
        @Nullable Runnable syncUpdateCallback
    ) {
        this.entity = entity;
        this.entityDataAccessor = entityDataAccessor;
        this.name = name;
        this.serializer = serializer;
        this.setterCallback = setterCallback;
        this.syncUpdateCallback = syncUpdateCallback;

        if (entity instanceof EDARefHolder edaRefHolder) {
            edaRefHolder.addRef(this);
        }
    }

    public T get() {
        return entity.getEntityData().get(entityDataAccessor);
    }

    public void set(T value) {
        entity.getEntityData().set(entityDataAccessor, value);

        if (setterCallback != null) {
            setterCallback.accept(value);
        }
    }

    public void deserialize(CompoundTag compoundTag) {
        if (name == null || serializer == null) {
            return;
        }

        var deserializedValue = serializer.deserialize(compoundTag, name);
        set(deserializedValue);
    }

    public void serialize(CompoundTag compoundTag) {
        if (name == null || serializer == null) {
            return;
        }

        serializer.serialize(compoundTag, name, get());
    }

    public EntityDataAccessor<T> getEntityDataAccessor() {
        return entityDataAccessor;
    }

    public @Nullable EDASerializer<T> getSerializer() {
        return serializer;
    }

    public @Nullable Runnable getSyncUpdateCallback() {
        return syncUpdateCallback;
    }

    public static <T> Builder<T> builder(Entity entity, EntityDataAccessor<T> entityDataAccessor) {
        return new Builder<>(entity, entityDataAccessor);
    }

    public static <T> EDARef<T> create(Entity entity, EntityDataAccessor<T> entityDataAccessor) {
        return new EDARef<>(entity, entityDataAccessor, null, null, null, null);
    }

    public static class Builder<T> {

        private final Entity entity;

        private final EntityDataAccessor<T> entityDataAccessor;

        private @Nullable String name;

        private @Nullable EDASerializer<T> serializer;

        private @Nullable Consumer<T> setterCallback;

        private @Nullable Runnable syncUpdateCallback;

        private Builder(Entity entity, EntityDataAccessor<T> entityDataAccessor) {
            this.entity = entity;
            this.entityDataAccessor = entityDataAccessor;
        }

        public Builder<T> setSetterCallback(@Nullable Consumer<T> setterCallback) {
            this.setterCallback = setterCallback;
            return this;
        }

        public Builder<T> setSerializer(String name, @Nullable EDASerializer<T> serializer) {
            this.name = name;
            this.serializer = serializer;
            return this;
        }

        public Builder<T> setSyncUpdateCallback(@Nullable Runnable syncUpdateCallback) {
            this.syncUpdateCallback = syncUpdateCallback;
            return this;
        }

        public EDARef<T> build() {
            return new EDARef<>(entity, entityDataAccessor, name, serializer, setterCallback, syncUpdateCallback);
        }
    }
}
