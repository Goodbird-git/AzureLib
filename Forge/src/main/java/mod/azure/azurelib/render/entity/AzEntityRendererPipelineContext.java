package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.minecraft.entity.Entity;

/**
 * A context class specifically for rendering entities using a custom rendering pipeline. This class extends
 * {@code AzRendererPipelineContext} and provides implementations for methods to customize entity rendering, such as
 * determining default render types and packed overlay settings.
 *
 * @param <T> the type of entity being rendered, extending {@code Entity}
 */
public class AzEntityRendererPipelineContext<T extends Entity> extends AzRendererPipelineContext<T> {

    public AzEntityRendererPipelineContext(AzRendererPipeline<T> rendererPipeline) {
        super(rendererPipeline);
    }
}
