package mod.azure.azurelib.core2.animation.cache;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;

/**
 * The AzIdentifiableItemStackAnimatorCache class is a singleton utility for managing a cache of {@link ItemStack}
 * objects, each associated with a unique identifier (UUID). This class provides functionality to register and retrieve
 * item animators that apply to specific {@link ItemStack}s using their respective UUIDs.
 */
public class AzIdentifiableItemStackAnimatorCache {

    private static final AzIdentifiableItemStackAnimatorCache INSTANCE = new AzIdentifiableItemStackAnimatorCache();

    // TODO: Purge animators periodically.
    private static final Map<UUID, AzItemAnimator> ANIMATORS_BY_UUID = new HashMap<>();

    public static AzIdentifiableItemStackAnimatorCache getInstance() {
        return INSTANCE;
    }

    private AzIdentifiableItemStackAnimatorCache() {}

    public void add(ItemStack itemStack, AzItemAnimator animator) {
        var uuid = itemStack.get(AzureLib.AZ_ID.get());

        if (uuid != null) {
            ANIMATORS_BY_UUID.computeIfAbsent(uuid, ($) -> animator);
        }
    }

    public @Nullable AzItemAnimator getOrNull(UUID uuid) {
        return uuid == null ? null : ANIMATORS_BY_UUID.get(uuid);
    }
}
