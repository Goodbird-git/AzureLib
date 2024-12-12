package mod.azure.azurelib.fabric.core2.example;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import mod.azure.azurelib.common.internal.common.AzureLib;

public class ExampleEntityTypes {

    public static final EntityType<Drone> DRONE = register(
        "drone",
        EntityType.Builder.of(Drone::new, MobCategory.MONSTER).sized(0.8f, 1.98f)
    );

    public static final EntityType<Facehugger> FACEHUGGER = register(
        "facehugger",
        EntityType.Builder.of(Facehugger::new, MobCategory.MONSTER).sized(0.75f, 0.25f)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        var entityType = builder.build(name);
        var resourceLocation = AzureLib.modResource(name);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceLocation, entityType);
        return entityType;
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(DRONE, Drone.createMonsterAttributes());
        FabricDefaultAttributeRegistry.register(FACEHUGGER, Facehugger.createMonsterAttributes());
    }
}
