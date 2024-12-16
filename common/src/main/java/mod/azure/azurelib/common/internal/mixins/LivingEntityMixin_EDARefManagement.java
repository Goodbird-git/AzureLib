package mod.azure.azurelib.common.internal.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import mod.azure.azurelib.core2.util.eda.EDARef;
import mod.azure.azurelib.core2.util.eda.EDARefHolder;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin_EDARefManagement implements EDARefHolder {

    private final List<EDARef<?>> edaRefs = new ArrayList<>();

    @Inject(at = @At("RETURN"), method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V")
    private void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor, CallbackInfo callbackInfo) {
        for (var edaRef : edaRefs) {
            var accessor = edaRef.getEntityDataAccessor();

            if (!accessor.equals(entityDataAccessor)) {
                continue;
            }

            var syncUpdateCallback = edaRef.getSyncUpdateCallback();

            if (syncUpdateCallback != null) {
                syncUpdateCallback.run();
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        for (var edaRef : edaRefs) {
            edaRef.serialize(compoundTag);
        }
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        for (var edaRef : edaRefs) {
            edaRef.deserialize(compoundTag);
        }
    }

    @Override
    public void addRef(EDARef<?> edaRef) {
        edaRefs.add(edaRef);
    }
}
