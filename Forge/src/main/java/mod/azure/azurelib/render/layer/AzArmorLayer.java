package mod.azure.azurelib.render.layer;

import com.mojang.authlib.GameProfile;
import mod.azure.azurelib.cache.object.GeoCube;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.armor.AzArmorRenderer;
import mod.azure.azurelib.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.render.armor.bone.AzArmorBoneContext;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

/**
 * Builtin class for handling dynamic armor rendering on AzureLib entities.<br>
 * Supports {@link ItemArmor Vanilla} armor models.<br>
 * Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering.
 */
public class AzArmorLayer<T extends EntityLiving> implements AzRenderLayer<T> {

    protected static final ModelBiped INNER_ARMOR_MODEL = new ModelBiped(0.5F); // Inner armor uses a smaller model
    protected static final ModelBiped OUTER_ARMOR_MODEL = new ModelBiped(1.0F); // Outer armor uses the full-sized model
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

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

            if (renderer != null && context.animatable() != null) {
                AzArmorBoneContext boneContext = renderer.rendererPipeline().context().boneContext();

                prepModelPartForRender(context, bone, modelPart);
                renderer.prepForRender(context.animatable(), armorStack, slot, model);
                boneContext.applyBoneVisibilityByPart(slot, modelPart, model);
                /**
                 * TODO
                 */
                model.render(context.animatable(), 0F, 0F, 1, 1, 1, 1);
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
    protected EntityEquipmentSlot getEquipmentSlotForBone(
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
        GlStateManager.translate(0.0F, -0.25F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.625F, -0.625F, -0.625F);

        boolean flag = context.animatable() instanceof EntityVillager || context.animatable() instanceof EntityZombieVillager;
        if (flag) {
            GlStateManager.translate(0.0F, 0.1875F, 0.0F);
        }

        Minecraft.getMinecraft().getItemRenderer().renderItem(context.animatable(), armorStack, ItemCameraTransforms.TransformType.HEAD);

        if (armorStack.isItemEnchanted()) {
            renderEnchantedGlint(context.animatable(), modelPart, context.partialTick());
        }
    }

    public static void renderEnchantedGlint(EntityLivingBase animatable, ModelRenderer modelPart, float partialTick) {
        float ticks = animatable.ticksExisted + partialTick;
        Minecraft.getMinecraft().renderEngine.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, ticks * (0.001F + i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            modelPart.render(1);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
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
        ItemStack itemstack,
        BlockSkull skullBlock
    ) {
        GlStateManager.scale(1.1875F, -1.1875F, -1.1875F);

        boolean flag = context.animatable() instanceof EntityVillager || context.animatable() instanceof EntityZombieVillager;

        if (flag) {
            GlStateManager.translate(0.0F, 0.0625F, 0.0F);
        }

        GameProfile gameprofile = null;

        if (itemstack.hasTagCompound()) {
            NBTTagCompound nbttagcompound = itemstack.getTagCompound();

            if (nbttagcompound != null && nbttagcompound.hasKey("SkullOwner", 10)) {
                gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
            }
            else if (nbttagcompound != null && nbttagcompound.hasKey("SkullOwner", 8)) {
                String s = nbttagcompound.getString("SkullOwner");

                if (!StringUtils.isBlank(s)) {
                    gameprofile = TileEntitySkull.updateGameprofile(new GameProfile(null, s));
                    nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                }
            }
        }
        TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemstack.getMetadata(), gameprofile, -1, 0);
    }

    /**
     * Prepares the given {@link ModelRenderer} for render by setting its translation, position, and rotation values based
     * on the provided {@link AzBone}
     *
     * @param bone       The AzBone to base the translations on
     * @param sourcePart The ModelPart to translate
     */
    protected void prepModelPartForRender(AzRendererPipelineContext<T> context, AzBone bone, ModelRenderer sourcePart) {
        GeoCube firstCube = bone.getCubes().get(0);
        ModelBox armorCube = sourcePart.cubeList.get(0);
        double armorBoneSizeX = firstCube.size().x;
        double armorBoneSizeY = firstCube.size().y;
        double armorBoneSizeZ = firstCube.size().z;
        float actualArmorSizeX = Math.abs(armorCube.posX2 - armorCube.posX1);
        float actualArmorSizeY = Math.abs(armorCube.posY2 - armorCube.posY1);
        float actualArmorSizeZ = Math.abs(armorCube.posZ2 - armorCube.posZ1);
        float scaleX = (float) (armorBoneSizeX / actualArmorSizeX);
        float scaleY = (float) (armorBoneSizeY / actualArmorSizeY);
        float scaleZ = (float) (armorBoneSizeZ / actualArmorSizeZ);

        sourcePart.setRotationPoint(
            -(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
            -(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
            (bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ)
        );

        sourcePart.rotateAngleX = -bone.getRotX();
        sourcePart.rotateAngleY = -bone.getRotY();
        sourcePart.rotateAngleZ = bone.getRotZ();

        GlStateManager.scale(scaleX, scaleY, scaleZ);
    }
}
