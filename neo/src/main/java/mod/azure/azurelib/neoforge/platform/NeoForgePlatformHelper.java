package mod.azure.azurelib.neoforge.platform;

import mod.azure.azurelib.common.internal.common.blocks.TickingLightBlock;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import mod.azure.azurelib.common.platform.services.IPlatformHelper;
import mod.azure.azurelib.neoforge.NeoForgeAzureLibMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getGameDir() {
        return FMLLoader.getGamePath();
    }

    @Override
    public boolean isServerEnvironment() {
        return FMLEnvironment.dist.isDedicatedServer();
    }

    @Override
    public <T> Supplier<DataComponentType<T>> registerDataComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return NeoForgeAzureLibMod.DATA_COMPONENTS_REGISTER.registerComponentType(id, builder);
    }

    @Override
    public boolean isEnvironmentClient() {
        return FMLEnvironment.dist.isClient();
    }
}
