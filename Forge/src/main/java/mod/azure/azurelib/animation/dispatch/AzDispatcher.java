package mod.azure.azurelib.animation.dispatch;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.animation.dispatch.command.AzDispatchCommand;

import java.util.ArrayList;
import java.util.Arrays;

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
     * @param command            the primary {@link AzDispatchCommand} to be executed.
     * @param additionalCommands additional {@link AzDispatchCommand}s to be executed, if any.
     * @return an {@link AzDispatchExecutor} configured for client-side execution.
     */
    public static AzDispatchExecutor fromClient(AzDispatchCommand command, AzDispatchCommand... additionalCommands) {
        ArrayList<AzDispatchCommand> commands = unifyCommands(command, additionalCommands);
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
     * @param command            the primary {@link AzDispatchCommand} to be executed.
     * @param additionalCommands additional {@link AzDispatchCommand}s to be executed, if any.
     * @return an {@link AzDispatchExecutor} configured for server-side execution.
     */
    public static AzDispatchExecutor fromServer(AzDispatchCommand command, AzDispatchCommand... additionalCommands) {
        ArrayList<AzDispatchCommand> commands = unifyCommands(command, additionalCommands);
        return new AzDispatchExecutor(commands, AzDispatchSide.SERVER);
    }

    /**
     * Combines a primary {@link AzDispatchCommand} with additional commands into a unified list.
     *
     * @param command            the primary {@link AzDispatchCommand} to be included in the unified list.
     * @param additionalCommands an array of additional {@link AzDispatchCommand}s to be added to the list.
     * @return a list containing the primary command followed by all additional commands.
     */
    private static @NotNull ArrayList<AzDispatchCommand> unifyCommands(
        AzDispatchCommand command,
        AzDispatchCommand[] additionalCommands
    ) {
        ArrayList<AzDispatchCommand> commands = new ArrayList<AzDispatchCommand>();
        commands.add(command);
        commands.addAll(Arrays.asList(additionalCommands));
        return commands;
    }
}
