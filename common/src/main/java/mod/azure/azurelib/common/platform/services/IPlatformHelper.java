package mod.azure.azurelib.common.platform.services;

import mod.azure.azurelib.common.internal.common.blocks.TickingLightBlock;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isDevelopmentEnvironment();

    Path getGameDir();

    boolean isServerEnvironment();

    boolean isEnvironmentClient();

    default BlockEntityType<TickingLightEntity> getTickingLightEntity() {
        return null;
    }

    default TickingLightBlock getTickingLightBlock() {
        return null;
    }

    Enchantment getIncendairyenchament();

    <T> Supplier<DataComponentType<T>> registerDataComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builder);
}
