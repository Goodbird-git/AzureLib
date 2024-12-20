package mod.azure.azurelib.core2.render.layer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.AbstractSkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.api.client.renderer.GeoArmorRenderer;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

/**
 * Builtin class for handling dynamic armor rendering on AzureLib entities.<br>
 * Supports both {@link GeoItem AzureLib} and {@link net.minecraft.world.item.ArmorItem Vanilla} armor models.<br>
 * Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering.
 */
public class AzArmorLayer extends AzRenderLayer<LivingEntity> {

    protected static final HumanoidModel<LivingEntity> INNER_ARMOR_MODEL = new HumanoidModel<>(
        Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)
    );

    protected static final HumanoidModel<LivingEntity> OUTER_ARMOR_MODEL = new HumanoidModel<>(
        Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)
    );

    @Nullable
    protected ItemStack mainHandStack;

    @Nullable
    protected ItemStack offhandStack;

    @Nullable
    protected ItemStack helmetStack;

    @Nullable
    protected ItemStack chestplateStack;

    @Nullable
    protected ItemStack leggingsStack;

    @Nullable
    protected ItemStack bootsStack;

    @Override
    public void preRender(AzRendererPipelineContext context) {
        if (!(context.animatable() instanceof LivingEntity livingEntity))
            return;
        this.mainHandStack = livingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        this.offhandStack = livingEntity.getItemBySlot(EquipmentSlot.OFFHAND);
        this.helmetStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        this.chestplateStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        this.leggingsStack = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
        this.bootsStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);
    }

    @Override
    public void render(AzRendererPipelineContext context) {}

    @Override
    public void renderForBone(AzRendererPipelineContext context, AzBone bone) {
        ItemStack armorStack = getArmorItemForBone(context, bone);

        if (armorStack == null)
            return;

        if (
            armorStack.getItem() instanceof BlockItem blockItem && blockItem
                .getBlock() instanceof AbstractSkullBlock skullBlock
        ) {
            renderSkullAsArmor(context, bone, armorStack, skullBlock);
        } else {
            EquipmentSlot slot = getEquipmentSlotForBone(context, bone, armorStack);
            HumanoidModel<?> model = getModelForItem(context, bone, slot, armorStack);
            ModelPart modelPart = getModelPartForBone(context, model);

            if (!modelPart.cubes.isEmpty()) {
                context.poseStack().pushPose();
                context.poseStack().scale(-1, -1, 1);

                if (
                    model instanceof GeoArmorRenderer<?> geoArmorRenderer && context
                        .animatable() instanceof Entity entity
                ) {
                    prepModelPartForRender(context, bone, modelPart);
                    geoArmorRenderer.prepForRender(entity, armorStack, slot, model);
                    geoArmorRenderer.applyBoneVisibilityByPart(slot, modelPart, model);
                    geoArmorRenderer.renderToBuffer(
                        context.poseStack(),
                        null,
                        context.packedLight(),
                        context.packedOverlay(),
                        armorStack.is(
                            ItemTags.DYEABLE
                        ) ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(armorStack, -6265536)) : -1
                    );
                } else if (armorStack.getItem() instanceof ArmorItem) {
                    prepModelPartForRender(context, bone, modelPart);
                    renderVanillaArmorPiece(
                        context,
                        bone,
                        slot,
                        armorStack,
                        modelPart
                    );
                }

                context.poseStack().popPose();
            }
        }
    }

    /**
     * Return an EquipmentSlot for a given {@link ItemStack} and animatable instance.<br>
     * This is what determines the base model to use for rendering a particular stack
     */
    @NotNull
    protected EquipmentSlot getEquipmentSlotForBone(AzRendererPipelineContext context, AzBone bone, ItemStack stack) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (
                slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && context
                    .animatable() instanceof LivingEntity livingEntity
            ) {
                if (stack == livingEntity.getItemBySlot(slot))
                    return slot;
            }
        }

        return EquipmentSlot.CHEST;
    }

    /**
     * Return a ModelPart for a given {@link AzBone}.<br>
     * This is then transformed into position for the final render
     */
    @NotNull
    protected ModelPart getModelPartForBone(AzRendererPipelineContext context, HumanoidModel<?> baseModel) {
        return baseModel.body;
    }

    /**
     * Get the {@link ItemStack} relevant to the bone being rendered.<br>
     * Return null if this bone should be ignored
     */
    @Nullable
    protected ItemStack getArmorItemForBone(AzRendererPipelineContext context, AzBone bone) {
        return null;
    }

    /**
     * Renders an individual armor piece base on the given {@link AzBone} and {@link ItemStack}
     */
    protected <I extends Item & GeoItem> void renderVanillaArmorPiece(
        AzRendererPipelineContext context,
        AzBone bone,
        EquipmentSlot slot,
        ItemStack armorStack,
        ModelPart modelPart
    ) {
        var material = ((ArmorItem) armorStack.getItem()).getMaterial();

        for (ArmorMaterial.Layer layer : material.value().layers()) {
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

        var trim = armorStack.get(DataComponents.TRIM);

        if (trim != null) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(
                    Sheets.ARMOR_TRIMS_SHEET
                )
                .getSprite(
                    slot == EquipmentSlot.LEGS ? trim.innerTexture(material) : trim.outerTexture(material)
                );
            VertexConsumer buffer = sprite.wrap(
                context.multiBufferSource().getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal()))
            );
            modelPart.render(context.poseStack(), buffer, context.packedLight(), context.packedOverlay());
        }

        if (armorStack.hasFoil())
            modelPart.render(
                context.poseStack(),
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

    protected VertexConsumer getVanillaArmorBuffer(
        AzRendererPipelineContext context,
        ItemStack stack,
        EquipmentSlot slot,
        AzBone bone,
        @Nullable ArmorMaterial.Layer layer,
        boolean forGlint
    ) {
        if (forGlint)
            return context.multiBufferSource().getBuffer(RenderType.armorEntityGlint());

        return context.multiBufferSource()
            .getBuffer(RenderType.armorCutoutNoCull(layer.texture(slot == EquipmentSlot.LEGS)));
    }

    /**
     * Returns a cached instance of a base HumanoidModel that is used for rendering/modelling the provided
     * {@link ItemStack}
     */
    @NotNull
    protected HumanoidModel<?> getModelForItem(
        AzRendererPipelineContext context,
        AzBone bone,
        EquipmentSlot slot,
        ItemStack stack
    ) {
        var defaultModel = slot == EquipmentSlot.LEGS ? INNER_ARMOR_MODEL : OUTER_ARMOR_MODEL;
        var livingEntity = (LivingEntity) context.animatable();

        return RenderProvider.of(stack).getHumanoidArmorModel(livingEntity, stack, slot, defaultModel);
    }

    /**
     * Render a given {@link AbstractSkullBlock} as a worn armor piece in relation to a given {@link AzBone}
     */
    protected void renderSkullAsArmor(
        AzRendererPipelineContext context,
        AzBone bone,
        ItemStack stack,
        AbstractSkullBlock skullBlock
    ) {
        var type = skullBlock.getType();
        var model = SkullBlockRenderer.createSkullRenderers(Minecraft.getInstance().getEntityModels())
            .get(type);
        var renderType = SkullBlockRenderer.getRenderType(type, stack.get(DataComponents.PROFILE));

        context.poseStack().pushPose();
        RenderUtils.translateAndRotateMatrixForBone(context.poseStack(), bone);
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
        context.poseStack().popPose();
    }

    /**
     * Prepares the given {@link ModelPart} for render by setting its translation, position, and rotation values based
     * on the provided {@link AzBone}
     *
     * @param context
     * @param bone       The AzBone to base the translations on
     * @param sourcePart The ModelPart to translate
     */
    protected void prepModelPartForRender(AzRendererPipelineContext context, AzBone bone, ModelPart sourcePart) {
        final var firstCube = bone.getCubes().get(0);
        final var armorCube = sourcePart.cubes.get(0);
        final var armorBoneSizeX = firstCube.size().x();
        final var armorBoneSizeY = firstCube.size().y();
        final var armorBoneSizeZ = firstCube.size().z();
        final var actualArmorSizeX = Math.abs(armorCube.maxX - armorCube.minX);
        final var actualArmorSizeY = Math.abs(armorCube.maxY - armorCube.minY);
        final var actualArmorSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
        var scaleX = (float) (armorBoneSizeX / actualArmorSizeX);
        var scaleY = (float) (armorBoneSizeY / actualArmorSizeY);
        var scaleZ = (float) (armorBoneSizeZ / actualArmorSizeZ);

        sourcePart.setPos(
            -(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
            -(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
            (bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ)
        );

        sourcePart.xRot = -bone.getRotX();
        sourcePart.yRot = -bone.getRotY();
        sourcePart.zRot = bone.getRotZ();

        context.poseStack().scale(scaleX, scaleY, scaleZ);
    }
}
