package mod.azure.azurelib.animation.dispatch.command;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorAccessor;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import mod.azure.azurelib.animation.primitive.AzLoopType;
import mod.azure.azurelib.animation.property.codec.AzListStreamCodec;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a command structure used to dispatch a sequence of actions in the animation system. This class primarily
 * serves as a container for a list of {@link AzAction} instances that define specific operations or behaviors to be
 * executed. <br>
 * The class provides support for building complex dispatch commands by leveraging the hierarchical builder system,
 * enabling customization of animation-related functionality.
 */
public class AzCommand {
    public List<AzAction> actions;

    public AzCommand(List<AzAction> actions) {
        this.actions = actions;
    }

    public List<AzAction> actions() {
        return actions;
    }

    public static final StreamCodec<FriendlyByteBuf, AzCommand> CODEC = StreamCodec.composite(
        new AzListStreamCodec<>(AzAction.CODEC),
        AzCommand::actions,
        AzCommand::new
    );

    public static AzRootCommandBuilder builder() {
        return new AzRootCommandBuilder();
    }

    public static AzCommand compose(Collection<AzCommand> commands) {
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("Attempted to compose an empty collection of commands.");
        } else if (commands.size() == 1) {
            return commands.iterator().next();
        }

        return new AzCommand(
            commands.stream()
                .flatMap(command -> command.actions().stream())
                .toList()
        );
    }

    public static AzCommand compose(AzCommand first, AzCommand second, AzCommand... others) {
        ArrayList<AzCommand> allCommands = new ArrayList<AzCommand>();

        allCommands.add(first);
        allCommands.add(second);
        Collections.addAll(allCommands, others);

        return compose(allCommands);
    }

    public static AzCommand create(String controllerName, String animationName) {
        return create(controllerName, animationName, AzLoopType.PLAY_ONCE);
    }

    /**
     * Creates a dispatch command to play a specified animation on a given controller.
     *
     * @param controllerName the name of the animation controller on which the animation should be played
     * @param animationName  the name of the animation to be played on the specified controller
     * @param loopType       the loop type for the animation to use
     * @return an instance of {@code AzCommand} representing the command to play the desired animation
     */
    public static AzCommand create(String controllerName, String animationName, AzLoopType loopType) {
        return builder()
            .playSequence(
                controllerName,
                sequenceBuilder -> sequenceBuilder.queue(animationName, props -> props.withLoopType(loopType))
            )
            .build();
    }

    /**
     * Sends animation commands for the specified entity based on the configured dispatch origin. The method determines
     * whether the command should proceed, logs a warning if it cannot, and dispatches the animation commands either
     * from the client or the server side.
     *
     * @param entity the target {@link Entity} for which the animation commands are dispatched.
     */
    public void sendForEntity(Entity entity) {
        if (entity.world.isRemote) {
            dispatchFromClient(entity);
        } else {
            int entityId = entity.getEntityId();
            AzEntityDispatchCommandPacket packet = new AzEntityDispatchCommandPacket(entityId, this);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        }
    }

    /**
     * Sends animation commands for the specified block entity based on the configured dispatch origin. The method
     * determines whether the command should proceed, logs a warning if it cannot, and dispatches the animation commands
     * either from the client or the server side.
     *
     * @param entity the target {@link TileEntity} for which the animation commands are dispatched.
     */
    public void sendForBlockEntity(TileEntity entity) {
        if (entity.getWorld().isRemote) {
            dispatchFromClient(entity);
        } else {
            BlockPos entityBlockPos = entity.getPos();
            AzBlockEntityDispatchCommandPacket packet = new AzBlockEntityDispatchCommandPacket(entityBlockPos, this);
            Services.NETWORK.sendToEntitiesTrackingChunk(packet, (ServerLevel) entity.getLevel(), entityBlockPos);
        }
    }

    /**
     * Sends animation commands for the specified item based on the configured dispatch origin. The method determines
     * whether the command can proceed, assigns a unique identifier to the item if required, and dispatches the
     * animation commands either from the client or the server side.
     *
     * @param entity    the {@link Entity} associated with the {@link ItemStack}.
     * @param itemStack the {@link ItemStack} on which the animation commands are dispatched.
     */
    public void sendForItem(Entity entity, ItemStack itemStack) {
        if (entity.world.isRemote) {
            dispatchFromClient(entity);
        } else {
            var uuid = itemStack.get(AzureLib.AZ_ID.get());

            if (uuid == null) {
                AzureLib.LOGGER.warn(
                    "Could not find item stack UUID during dispatch. Did you forget to register an identity for the item? Item: {}, Item Stack: {}",
                    itemStack.getItem(),
                    itemStack
                );
                return;
            }

            AzItemStackDispatchCommandPacket packet = new AzItemStackDispatchCommandPacket(uuid, this);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        }
    }

    private <T> void dispatchFromClient(T animatable) {
        AzAnimator<T> animator = AzAnimatorAccessor.getOrNull(animatable);

        if (animator != null) {
            actions.forEach(action -> action.handle(AzDispatchSide.CLIENT, animator));
        }
    }
}
