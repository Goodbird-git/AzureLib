package mod.azure.azurelib.neoforge.platform;

import mod.azure.azurelib.common.platform.services.CommonRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NeoForgeCommonRegistry implements CommonRegistry {

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String modID, String blockEntityName, Supplier<BlockEntityType<T>> blockEntityType) {
        final DeferredRegister<BlockEntityType<?>> blockDeferredRegister = DeferredRegister.create(
                Registries.BLOCK_ENTITY_TYPE, modID);
        return blockDeferredRegister.register(blockEntityName, blockEntityType);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String modID, String blockName, Supplier<T> block) {
        final DeferredRegister<Block> blockDeferredRegister = DeferredRegister.create(Registries.BLOCK, modID);
        return blockDeferredRegister.register(blockName, block);
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String modID, String entityName, Supplier<EntityType<T>> entity) {
        final DeferredRegister<EntityType<?>> entityTypeDeferredRegister = DeferredRegister.create(
                Registries.ENTITY_TYPE, modID);
        return entityTypeDeferredRegister.register(entityName, entity);
    }

    @Override
    public <T extends ArmorMaterial> Holder<T> registerArmorMaterial(String modID, String matName, Supplier<T> armorMaterial) {
        final DeferredRegister<ArmorMaterial> armorMaterialDeferredRegister = DeferredRegister.create(
                Registries.ARMOR_MATERIAL, modID);
        return (Holder<T>) armorMaterialDeferredRegister.register(matName, armorMaterial);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String modID, String itemName, Supplier<T> item) {
        final DeferredRegister<Item> itemDeferredRegister = DeferredRegister.create(Registries.ITEM, modID);
        return itemDeferredRegister.register(itemName, item);
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSound(String modID, String soundName, Supplier<T> sound) {
        final DeferredRegister<SoundEvent> soundEventDeferredRegister = DeferredRegister.create(Registries.SOUND_EVENT,
                modID);
        return soundEventDeferredRegister.register(soundName, sound);
    }

    @Override
    public <T extends MenuType<?>> Supplier<T> registerScreen(String modID, String screenName, Supplier<T> menuType) {
        final DeferredRegister<MenuType<?>> menuTypeDeferredRegister = DeferredRegister.create(Registries.MENU, modID);
        return menuTypeDeferredRegister.register(screenName, menuType);
    }

    @Override
    public <T extends StructureType<?>> Supplier<T> registerStructure(String modID, String structureName, Supplier<T> structure) {
        final DeferredRegister<StructureType<?>> structureTypeDeferredRegister = DeferredRegister.create(
                Registries.STRUCTURE_TYPE, modID);
        return structureTypeDeferredRegister.register(structureName, structure);
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticle(String modID, String particleName, Supplier<T> particle) {
        final DeferredRegister<ParticleType<?>> particleTypeDeferredRegister = DeferredRegister.create(
                Registries.PARTICLE_TYPE, modID);
        return particleTypeDeferredRegister.register(particleName, particle);
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String modID, String tabName, Supplier<T> tab) {
        final DeferredRegister<CreativeModeTab> creativeModeTabDeferredRegister = DeferredRegister.create(
                Registries.CREATIVE_MODE_TAB, modID);
        return creativeModeTabDeferredRegister.register(tabName, tab);
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> makeSpawnEggFor(Supplier<EntityType<E>> entityType, int primaryEggColour, int secondaryEggColour, Item.Properties itemProperties) {
        return () -> new DeferredSpawnEggItem(entityType, primaryEggColour, secondaryEggColour, itemProperties);
    }

    @Override
    public CreativeModeTab.Builder newCreativeTabBuilder() {
        return CreativeModeTab.builder();
    }
}
