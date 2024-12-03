package mod.azure.azurelib.core2.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Collection;
import java.util.Map;

public class AzAnimationControllerContainer<T> {

    private final Map<String, AzAnimationController<T>> animationControllersByName;

    public AzAnimationControllerContainer() {
        this.animationControllersByName = new Object2ObjectArrayMap<>();
    }

    @SafeVarargs
    public final void add(AzAnimationController<T> controller, AzAnimationController<T>... controllers) {
        animationControllersByName.put(controller.getName(), controller);

        for (var extraController : controllers) {
            animationControllersByName.put(extraController.getName(), extraController);
        }
    }

    public Collection<AzAnimationController<T>> getAll() {
        return animationControllersByName.values();
    }
}
