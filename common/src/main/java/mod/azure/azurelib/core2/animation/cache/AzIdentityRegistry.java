package mod.azure.azurelib.core2.animation.cache;

import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;

public class AzIdentityRegistry {

    private static final Set<Item> IDENTITY_OF_ITEMS = new HashSet<>();

    public static void register(Item item) {
        IDENTITY_OF_ITEMS.add(item);
    }

    public static boolean hasIdentity(Item item) {
        return IDENTITY_OF_ITEMS.contains(item);
    }
}
