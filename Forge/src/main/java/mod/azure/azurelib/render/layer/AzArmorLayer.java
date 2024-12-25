package mod.azure.azurelib.render.layer;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.cache.object.GeoCube;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.armor.AzArmorRenderer;
import mod.azure.azurelib.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.render.armor.bone.AzArmorBoneContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Builtin class for handling dynamic armor rendering on AzureLib entities.<br>
 * Supports {@link ItemArmor Vanilla} armor models.<br>
 * Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering.
 */
public class AzArmorLayer<T extends EntityLiving> implements AzRenderLayer<T> {

    protected static final ModelBiped INNER_ARMOR_MODEL = new ModelBiped(
        Minecraft.getMinecraft().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)
    );

    protected static final ModelBiped OUTER_ARMOR_MODEL = new ModelBiped(
        Minecraft.getMinecraft().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)
    );

    protected ItemStack mainHandStack;

    protected ItemStack offhandStack;

    protected ItemStack helmetStack;

    protected ItemStack chestplateStack;

    protected ItemStack leggingsStack;

    protected ItemStack bootsStack;

    /**
     * Prepares the necessary item stacks for rendering by accessing the relevant equipment slots of the animatable
     * instance. If the animatable instance is not a EntityLiving, the method returns without action.
     *
     * @param context The rendering context containing the animatable instance and other necessary data for rendering.
     */
    @Override
    public void preRender(AzRendererPipelineContext<T> context) {
        if (!(context.animatable() instanceof EntityLiving)) {
            return;
        }

        this.mainHandStack = context.animatable().getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        this.offhandStack = context.animatable().getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        this.helmetStack = context.animatable().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        this.chestplateStack = context.animatable().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        this.leggingsStack = context.animatable().getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        this.bootsStack = context.animatable().getItemStackFromSlot(EntityEquipmentSlot.FEET);
    }

    @Override
    public void render(AzRendererPipelineContext<T> context) {}

    /**
     * Renders the given armor or skull block for the specified bone using the provided rendering context. Depending on
     * the type of item, it delegates rendering to appropriate methods.
     *
     * @param context The rendering context containing necessary parameters for rendering, like pose stack, light level,
     *                etc.
     * @param bone    The specific bone of the model where the armor or skull block will be rendered.
     */
    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {
        ItemStack armorStack = getArmorItemForBone(context, bone);

        if (armorStack == null) {
            return;
        }

        if (
            armorStack.getItem() instanceof ItemBlock && ((ItemBlock) armorStack.getItem())
                .getBlock() instanceof BlockSkull
        ) {
            renderSkullAsArmor(context, bone, armorStack, ((BlockSkull) ((ItemBlock) armorStack.getItem()).getBlock()));
        } else {
            renderArmor(context, bone, armorStack);
        }
    }

    /**
     * Renders armor items on a given bone within the render cycle of a model. This method determines the appropriate
     * equipment slot, renderer, and model for the armor item and handles the rendering process accordingly.
     *
     * @param context    The rendering context containing the animatable instance and other data essential for
     *                   rendering.
     * @param bone       The specific bone of the model where the armor piece will be rendered.
     * @param armorStack The ItemStack representing the armor item to render.
     */
    private void renderArmor(
        AzRendererPipelineContext<T> context,
        AzBone bone,
        ItemStack armorStack
    ) {
        EntityEquipmentSlot slot = getEquipmentSlotForBone(context, bone, armorStack);
        AzArmorRenderer renderer = getRendererForItem(armorStack);
        ModelBiped model = getModelForItem(armorStack, slot);
        ModelRenderer modelPart = getModelPartForBone(context, model);

        if (!modelPart.childModels.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(-1, -1, 1);

            if (renderer != null && context.animatable() instanceof Entity) {
                AzArmorBoneContext boneContext = renderer.rendererPipeline().context().boneContext();

                prepModelPartForRender(context, bone, modelPart);
                renderer.prepForRender(context.animatable(), armorStack, slot, model);
                boneContext.applyBoneVisibilityByPart(slot, modelPart, model);
                /**
                 * TODO
                 */
                model.render(
                    poseStack,
                    null,
                    context.packedLight(),
                    context.packedOverlay(),
                    armorStack.is(
                        ItemTags.DYEABLE
                    ) ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(armorStack, -6265536)) : -1
                );
            } else if (armorStack.getItem() instanceof ItemArmor) {
                prepModelPartForRender(context, bone, modelPart);
                renderVanillaArmorPiece(
                    context,
                    bone,
                    slot,
                    armorStack,
                    modelPart
                );
            }

            GlStateManager.popMatrix();
        }
    }

    /**
     * Return an EquipmentSlot for a given {@link ItemStack} and animatable instance.<br>
     * This is what determines the base model to use for rendering a particular stack
     */
    protected @NotNull EntityEquipmentSlot getEquipmentSlotForBone(
        AzRendererPipelineContext<T> context,
        AzBone bone,
        ItemStack stack
    ) {
        T animatable = context.animatable();

        if (animatable instanceof EntityLiving) {
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                boolean isHumanoidArmorSlotType = slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR;

                if (isHumanoidArmorSlotType && stack == animatable.getItemStackFromSlot(slot)) {
                    return slot;
                }
            }
        }

        return EntityEquipmentSlot.CHEST;
    }

    /**
     * Return a ModelPart for a given {@link AzBone}.<br>
     * This is then transformed into position for the final render
     */
    @NotNull
    protected ModelRenderer getModelPartForBone(AzRendererPipelineContext<T> context, ModelBiped baseModel) {
        return baseModel.bipedBody;
    }

    /**
     * Get the {@link ItemStack} relevant to the bone being rendered.<br>
     * Return null if this bone should be ignored
     */
    protected ItemStack getArmorItemForBone(AzRendererPipelineContext<T> context, AzBone bone) {
        return null;
    }

    /**
     * Renders an individual armor piece base on the given {@link AzBone} and {@link ItemStack}
     */
    protected <I extends Item> void renderVanillaArmorPiece(
        AzRendererPipelineContext<T> context,
        AzBone bone,
        EntityEquipmentSlot slot,
        ItemStack armorStack,
        ModelRenderer modelPart
    ) {
        ItemArmor.ArmorMaterial material = ((ItemArmor) armorStack.getItem()).getArmorMaterial();

        for (var layer : material.value().layers()) {
            var buffer = getVanillaArmorBuffer(
                context,
                armorStack,
                slot,
                bone,
                layer,
                false
            );

            modelPart.render(context.poseStack(), buffer, context.packedLight(), context.packedOverlay());
        }

        if (armorStack.hasFoil())
            modelPart.render(
                getVanillaArmorBuffer(
                    context,
                    armorStack,
                    slot,
                    bone,
                    null,
                    true
                ),
                context.packedLight(),
                context.packedOverlay(),
                1
            );
    }

    /**
     * Retrieves a {@link BufferBuilder} for rendering vanilla-styled armor. The method determines whether the armor
     * should apply a glint effect or not and selects the appropriate render type accordingly.
     *
     * @param context  The rendering context providing necessary data for rendering, including the animatable instance
     *                 and the buffer source.
     * @param stack    The armor {@link ItemStack} being rendered.
     * @param slot     The {@link EntityEquipmentSlot} the armor piece occupies.
     * @param bone     The model bone associated with the armor piece.
     * @param layer    The optional {@link ItemArmor.ArmorMaterial} providing texture resources for rendering the armor.
     * @param forGlint A flag indicating whether the armor piece should render with a glint effect.
     * @return The {@link BufferBuilder} used to render the designated armor piece with the appropriate style and
     *         effect.
     */
    protected BufferBuilder getVanillaArmorBuffer(
        AzRendererPipelineContext<T> context,
        ItemStack stack,
        EntityEquipmentSlot slot,
        AzBone bone,
        ItemArmor.ArmorMaterial layer,
        boolean forGlint
    ) {
        if (forGlint) {
            return context.multiBufferSource().getBuffer(RenderType.armorEntityGlint());
        }

        return context.multiBufferSource()
            .getBuffer(RenderType.armorCutoutNoCull(layer.texture(slot == EquipmentSlot.LEGS)));
    }

    /**
     * Retrieves the appropriate {@link AzArmorRenderer} for the given {@link ItemStack}. This method uses the
     * {@link AzArmorRendererRegistry} to fetch a renderer if one is registered for the specified item's class or
     * instance.
     *
     * @param stack The {@link ItemStack} for which the renderer is to be obtained.
     * @return The {@link AzArmorRenderer} associated with the item in the stack, or null if no renderer exists.
     */
    protected AzArmorRenderer getRendererForItem(ItemStack stack) {
        Item item = stack.getItem();
        return AzArmorRendererRegistry.getOrNull(item);
    }

    /**
     * Returns a cached instance of a base HumanoidModel that is used for rendering/modelling the provided
     * {@link ItemStack}
     */
    protected ModelBiped getModelForItem(ItemStack stack, EntityEquipmentSlot slot) {
        AzArmorRenderer renderer = getRendererForItem(stack);

        if (renderer == null) {
            return slot == EntityEquipmentSlot.LEGS ? INNER_ARMOR_MODEL : OUTER_ARMOR_MODEL;
        }

        return renderer.rendererPipeline().armorModel();
    }

    /**
     * Render a given {@link BlockSkull} as a worn armor piece in relation to a given {@link AzBone}
     */
    protected void renderSkullAsArmor(
        AzRendererPipelineContext<T> context,
        AzBone bone,
        ItemStack stack,
        BlockSkull skullBlock
    ) {
        var type = skullBlock.getType();
        var model = SkullBlockRenderer.createSkullRenderers(Minecraft.getInstance().getEntityModels())
            .get(type);
        var renderType = SkullBlockRenderer.getRenderType(type, stack.get(DataComponents.PROFILE));

        context.poseStack().pushMatrix();
        RenderUtils.translateAndRotateMatrixForBone(bone);
        context.poseStack().scale(1.1875f, 1.1875f, 1.1875f);
        context.poseStack().translate(-0.5f, 0, -0.5f);
        SkullBlockRenderer.renderSkull(
            null,
            0,
            0,
            context.poseStack(),
            context.multiBufferSource(),
            context.packedLight(),
            model,
            renderType
        );
        context.poseStack().popMatrix();
    }

    /**
     * Prepares the given {@link ModelRenderer} for render by setting its translation, position, and rotation values based
     * on the provided {@link AzBone}
     *
     * @param context
     * @param bone       The AzBone to base the translations on
     * @param sourcePart The ModelPart to translate
     */
    protected void prepModelPartForRender(AzRendererPipelineContext<T> context, AzBone bone, ModelRenderer sourcePart) {
        GeoCube firstCube = bone.getCubes().get(0);
        ModelRenderer armorCube = sourcePart.childModels.get(0);
        double armorBoneSizeX = firstCube.size().x;
        double armorBoneSizeY = firstCube.size().y;
        double armorBoneSizeZ = firstCube.size().z;
        var actualArmorSizeX = Math.abs(armorCube.maxX - armorCube.minX);
        var actualArmorSizeY = Math.abs(armorCube.maxY - armorCube.minY);
        var actualArmorSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
        float scaleX = (float) (armorBoneSizeX / actualArmorSizeX);
        float scaleY = (float) (armorBoneSizeY / actualArmorSizeY);
        float scaleZ = (float) (armorBoneSizeZ / actualArmorSizeZ);

        sourcePart.setPos(
            -(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
            -(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
            (bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ)
        );

        sourcePart.xRot = -bone.getRotX();
        sourcePart.yRot = -bone.getRotY();
        sourcePart.zRot = bone.getRotZ();

        GlStateManager.scale(scaleX, scaleY, scaleZ);
    }
}
