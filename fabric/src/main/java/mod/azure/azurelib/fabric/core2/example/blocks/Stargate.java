package mod.azure.azurelib.fabric.core2.example.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.fabric.core2.example.ExampleEntityTypes;

public class Stargate extends BaseEntityBlock implements EntityBlock {

    public static final MapCodec<Stargate> CODEC = simpleCodec(Stargate::new);

    public Stargate(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ExampleEntityTypes.STARGATE.create(pos, state);
    }
}
