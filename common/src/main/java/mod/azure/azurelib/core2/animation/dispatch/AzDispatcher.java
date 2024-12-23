package mod.azure.azurelib.core2.animation.dispatch;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.AzCommand;

public class AzDispatcher {

    private AzDispatcher() {}

    /**
     * Creates an {@link AzDispatchExecutor} configured to execute commands on the client side.
     * <p>
     * This method combines the primary command and any additional commands into a unified set of commands, which will
     * be executed on the client. After creating the executor, you must invoke one of the appropriate `sendFor` methods
     * based on the target:
     * </p>
     * <ul>
     * <li><b>Entities:</b> Use {@code sendForEntity(Entity)} to dispatch the command from an entity.</li>
     * <li><b>Block Entities:</b> Use {@code sendForBlockEntity(BlockEntity)} to dispatch the command from a block
     * entity.</li>
     * <li><b>Items:</b> Use {@code sendForItem(Entity, ItemStack)} to dispatch the command from an item.</li>
     * </ul>
     *
     * @param command            the primary {@link AzCommand} to be executed.
     * @param additionalCommands additional {@link AzCommand}s to be executed, if any.
     * @return an {@link AzDispatchExecutor} configured for client-side execution.
     */
    public static AzDispatchExecutor fromClient(AzCommand command, AzCommand... additionalCommands) {
        var commands = unifyCommands(command, additionalCommands);
        return new AzDispatchExecutor(commands, AzDispatchSide.CLIENT);
    }

    /**
     * Creates an {@link AzDispatchExecutor} configured to execute commands on the server side.
     * <p>
     * This method combines the primary command and any additional commands into a unified set of commands, which will
     * be executed on the server. After creating the executor, you must invoke one of the appropriate `sendFor` methods
     * based on the target:
     * </p>
     * <ul>
     * <li><b>Entities:</b> Use {@code sendForEntity(Entity)} to dispatch the command from an entity.</li>
     * <li><b>Block Entities:</b> Use {@code sendForBlockEntity(BlockEntity)} to dispatch the command from a block
     * entity.</li>
     * <li><b>Items:</b> Use {@code sendForItem(Entity, ItemStack)} to dispatch the command from an item.</li>
     * </ul>
     *
     * @param command            the primary {@link AzCommand} to be executed.
     * @param additionalCommands additional {@link AzCommand}s to be executed, if any.
     * @return an {@link AzDispatchExecutor} configured for server-side execution.
     */
    public static AzDispatchExecutor fromServer(AzCommand command, AzCommand... additionalCommands) {
        var commands = unifyCommands(command, additionalCommands);
        return new AzDispatchExecutor(commands, AzDispatchSide.SERVER);
    }

    /**
     * Combines a primary {@link AzCommand} with additional commands into a unified list.
     *
     * @param command            the primary {@link AzCommand} to be included in the unified list.
     * @param additionalCommands an array of additional {@link AzCommand}s to be added to the list.
     * @return a list containing the primary command followed by all additional commands.
     */
    private static @NotNull ArrayList<AzCommand> unifyCommands(
        AzCommand command,
        AzCommand[] additionalCommands
    ) {
        var commands = new ArrayList<AzCommand>();
        commands.add(command);
        commands.addAll(List.of(additionalCommands));
        return commands;
    }
}
