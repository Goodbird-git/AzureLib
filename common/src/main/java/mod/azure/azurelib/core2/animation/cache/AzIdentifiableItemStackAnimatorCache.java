package mod.azure.azurelib.core2.animation.cache;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AzIdentifiableItemStackAnimatorCache {

    private static final AzIdentifiableItemStackAnimatorCache INSTANCE = new AzIdentifiableItemStackAnimatorCache();

    // TODO: Purge null empty weak reference mappings periodically.
    private static final Map<UUID, WeakReference<ItemStack>> ITEM_STACKS_BY_UUID = new HashMap<>();

    public static AzIdentifiableItemStackAnimatorCache getInstance() {
        return INSTANCE;
    }

    public void add(ItemStack itemStack) {
        var uuid = itemStack.get(AzureLib.AZ_ID.get());

        if (uuid != null) {
            ITEM_STACKS_BY_UUID.computeIfAbsent(uuid, ($) -> new WeakReference<>(itemStack));
        }
    }

    public @Nullable AzItemAnimator getOrNull(UUID uuid) {
        var itemStackReference = ITEM_STACKS_BY_UUID.get(uuid);
        var itemStack = itemStackReference.get();
        return itemStack == null ? null : (AzItemAnimator) AzAnimatorAccessor.getOrNull(itemStack);
    }
}
