package mod.azure.azurelib.render.item;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The AzItemRendererRegistry class manages the association between items and their renderers in the context of the
 * AzureLib framework. It provides functionality to register and retrieve item renderers dynamically, ensuring that
 * appropriate renderers can be applied to specific item types or instances.
 */
public class AzItemRendererRegistry {

    private static final Map<Item, AzItemRenderer> ITEM_TO_RENDERER = new HashMap<>();

    private static final Map<Class<? extends Item>, Supplier<AzItemRenderer>> ITEM_CLASS_TO_RENDERER_SUPPLIER =
        new HashMap<>();

    public static void register(Class<? extends Item> itemClass, Supplier<AzItemRenderer> itemRendererSupplier) {
        ITEM_CLASS_TO_RENDERER_SUPPLIER.put(itemClass, itemRendererSupplier);
    }

    public static AzItemRenderer getOrNull(Item item) {
        return ITEM_TO_RENDERER.computeIfAbsent(item, ($) -> {
            Class itemClass = item.getClass();
            Supplier<AzItemRenderer> rendererSupplier = ITEM_CLASS_TO_RENDERER_SUPPLIER.get(itemClass);
            return rendererSupplier == null ? null : rendererSupplier.get();
        });
    }
}
