package mod.azure.azurelib.common.api.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;

import java.util.function.Supplier;

import mod.azure.azurelib.common.platform.Services;

/**
 * Example of using this Interface to create a new Item:
 * <p>
 * The following code demonstrates how to register a new armor material in the game:
 * </p>
 *
 * <pre>{@code
 *
 * public static final Holder<ArmorMaterial> TEST_ARMOR_MATERIAL = CommonArmorMaterialRegistryInterface
 *     .registerArmorMaterial("modid", "materialname", YOURCLASS::dummyArmorMaterial);
 *
 * private static ArmorMaterial dummyArmorMaterial() {
 *     ArmorMaterial diamond = ArmorMaterials.DIAMOND.value();
 *     return new ArmorMaterial(
 *         diamond.defense(),
 *         diamond.enchantmentValue(),
 *         diamond.equipSound(),
 *         diamond.repairIngredient(),
 *         List.of(), // If your Material has other layers like Player Armors inner/outer
 *         diamond.toughness(),
 *         diamond.knockbackResistance()
 *     );
 * }
 * }</pre>
 * <p>
 * In this example:
 * </p>
 * <ul>
 * <li><code>registerArmorMaterial</code> is a method to register a new armor material with the specified mod ID and
 * material name.</li>
 * <li><code>dummyArmorMaterial</code> creates a new instance of <code>ArmorMaterial</code> using the properties of the
 * existing <code>ArmorMaterials.DIAMOND</code>.</li>
 * </ul>
 * <p>
 * The {@link net.minecraft.world.item.ArmorMaterial ArmorMaterial} class represents the material properties for an
 * armor item.
 * </p>
 */
public interface CommonArmorMaterialRegistryInterface {

    /**
     * Registers a new Armor Material.
     *
     * @param modID         The mod ID.
     * @param matName       The name of the material.
     * @param armorMaterial A supplier for the armor material.
     * @param <T>           The type of the armor material.
     * @return A holder for the registered armor material.
     */
    static <T extends ArmorMaterial> Holder<T> registerArmorMaterial(
        String modID,
        String matName,
        Supplier<T> armorMaterial
    ) {
        return Services.COMMON_REGISTRY.registerArmorMaterial(modID, matName, armorMaterial);
    }
}
