package mod.azure.azurelib.core2.animation.dispatch;

import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AzDispatcher {

    private AzDispatcher() {}

    public static AzDispatchExecutor fromClient(AzDispatchCommand command, AzDispatchCommand... additionalCommands) {
        var commands = unifyCommands(command, additionalCommands);
        return new AzDispatchExecutor(commands, AzDispatchSide.CLIENT);
    }

    public static AzDispatchExecutor fromServer(AzDispatchCommand command, AzDispatchCommand... additionalCommands) {
        var commands = unifyCommands(command, additionalCommands);
        return new AzDispatchExecutor(commands, AzDispatchSide.SERVER);
    }

    private static @NotNull ArrayList<AzDispatchCommand> unifyCommands(AzDispatchCommand command, AzDispatchCommand[] additionalCommands) {
        var commands = new ArrayList<AzDispatchCommand>();
        commands.add(command);
        commands.addAll(List.of(additionalCommands));
        return commands;
    }
}
