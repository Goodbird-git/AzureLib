package mod.azure.azurelib.core2.util.state;

public interface State<C extends StateMachineContext> {

    void onEnter(C context);

    void onUpdate(C context);

    void onExit(C context);
}
