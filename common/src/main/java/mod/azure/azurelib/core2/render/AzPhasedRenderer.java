package mod.azure.azurelib.core2.render;

import com.mojang.blaze3d.vertex.PoseStack;

public interface AzPhasedRenderer<T> {

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    void preRender(AzRendererPipelineContext<T> context, boolean isReRender);

    /**
     * Called after rendering the model to buffer. Post-render modifications should be performed here.<br>
     * {@link PoseStack} transformations will be unused and lost once this method ends
     */
    void postRender(AzRendererPipelineContext<T> context, boolean isReRender);
}
