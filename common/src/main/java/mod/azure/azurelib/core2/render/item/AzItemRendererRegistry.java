package mod.azure.azurelib.core2.render.item;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AzItemRendererRegistry {

    private static final Map<Item, AzItemRenderer> ITEM_TO_RENDERER = new HashMap<>();

    private static final Map<Class<? extends Item>, Supplier<AzItemRenderer>> ITEM_CLASS_TO_RENDERER_SUPPLIER =
        new HashMap<>();

    public static void register(Class<? extends Item> itemClass, Supplier<AzItemRenderer> itemRendererSupplier) {
        ITEM_CLASS_TO_RENDERER_SUPPLIER.put(itemClass, itemRendererSupplier);
    }

    public static @Nullable AzItemRenderer getOrNull(Item item) {
        return ITEM_TO_RENDERER.computeIfAbsent(item, ($) -> {
            var itemClass = item.getClass();
            var rendererSupplier = ITEM_CLASS_TO_RENDERER_SUPPLIER.get(itemClass);
            return rendererSupplier == null ? null : rendererSupplier.get();
        });
    }
}
