package mod.azure.azurelib.render.armor;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.armor.bone.AzArmorBoneContext;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AzArmorRendererPipelineContext extends AzRendererPipelineContext<ItemStack> {

    private final AzArmorBoneContext boneContext;

    private LayerArmorBase<?> baseModel;

    private Entity currentEntity;

    private EntityEquipmentSlot currentSlot;

    private ItemStack currentStack;

    public AzArmorRendererPipelineContext(AzRendererPipeline<ItemStack> rendererPipeline) {
        super(rendererPipeline);
        this.baseModel = null;
        this.boneContext = new AzArmorBoneContext();
        this.currentEntity = null;
        this.currentSlot = null;
        this.currentStack = null;
    }

    @Override
    public @NotNull RenderType getDefaultRenderType(
        ItemStack animatable,
        ResourceLocation texture,
        MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.armorCutoutNoCull(texture);
    }

    public void prepare(
        Entity entity,
        ItemStack stack,
        EntityEquipmentSlot slot,
        LayerArmorBase<?> baseModel
    ) {
        this.baseModel = baseModel;
        this.currentEntity = entity;
        this.currentStack = stack;
        this.currentSlot = slot;
    }

    /**
     * Gets a tint-applying color to render the given animatable with
     * <p>
     * Returns {@link Color#WHITE} by default
     */
    @Override
    public Color getRenderColor(ItemStack animatable, float partialTick, int packedLight) {
        return this.currentStack.is(ItemTags.DYEABLE)
            ? Color.ofOpaque(
                DyedItemColor.getOrDefault(this.currentStack, -6265536)
            )
            : Color.WHITE;
    }

    public LayerArmorBase<?> baseModel() {
        return baseModel;
    }

    public AzArmorBoneContext boneContext() {
        return boneContext;
    }

    public Entity currentEntity() {
        return currentEntity;
    }

    public EntityEquipmentSlot currentSlot() {
        return currentSlot;
    }

    public ItemStack currentStack() {
        return currentStack;
    }
}
