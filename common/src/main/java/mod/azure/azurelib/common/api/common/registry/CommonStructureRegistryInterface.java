package mod.azure.azurelib.common.api.common.registry;

import mod.azure.azurelib.common.platform.Services;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.function.Supplier;

/**
 * Example of using this Interface to create a new Structure:
 * <p>
 * The following code demonstrates how to register a new structure type in the game:
 * </p>
 * <pre>{@code
 * public static final Supplier<StructureType<?>> TEST = CommonStructureRegistryInterface.registerStructure("modid", "structurename", () -> CustomStructure.CODEC);
 * }</pre>
 * <p>
 * In this example:
 * </p>
 * <ul>
 * <li><code>registerStructure</code> is a method to register a new structure type with the specified mod ID and structure name.</li>
 * </ul>
 */
public interface CommonStructureRegistryInterface {

    /**
     * Registers a new structure type.
     *
     * @param modID         The mod ID.
     * @param structureName The name of the structure.
     * @param structure     A supplier for the structure type.
     * @param <T>           The type of the structure type.
     * @return A supplier for the registered structure type.
     */
    static <T extends StructureType<?>> Supplier<T> registerStructure(String modID, String structureName, Supplier<T> structure) {
        return Services.COMMON_REGISTRY.registerStructure(modID, structureName, structure);
    }
}