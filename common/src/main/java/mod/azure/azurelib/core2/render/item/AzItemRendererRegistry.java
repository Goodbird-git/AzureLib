package mod.azure.azurelib.core2.render.item;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AzItemRendererRegistry {

    private static final Map<Item, AzItemRenderer> ITEM_TO_RENDERER = new HashMap<>();

    public static void register(Item item, AzItemRenderer itemRenderer) {
        ITEM_TO_RENDERER.put(item, itemRenderer);
    }

    public static @Nullable AzItemRenderer getOrNull(Item item) {
        return ITEM_TO_RENDERER.get(item);
    }
}
