package mod.azure.azurelib.render.armor.bone;

import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.inventory.EntityEquipmentSlot;

public class AzArmorBoneContext {

    private AzBakedModel lastModel;

    private AzBone head;

    private AzBone body;

    private AzBone rightArm;

    private AzBone leftArm;

    private AzBone rightLeg;

    private AzBone leftLeg;

    private AzBone rightBoot;

    private AzBone leftBoot;

    public AzArmorBoneContext() {
        this.head = null;
        this.body = null;
        this.rightArm = null;
        this.leftArm = null;
        this.rightLeg = null;
        this.leftLeg = null;
        this.rightBoot = null;
        this.leftBoot = null;
    }

    public void setAllVisible(boolean pVisible) {
        setBoneVisible(this.head, pVisible);
        setBoneVisible(this.body, pVisible);
        setBoneVisible(this.rightArm, pVisible);
        setBoneVisible(this.leftArm, pVisible);
        setBoneVisible(this.rightLeg, pVisible);
        setBoneVisible(this.leftLeg, pVisible);
        setBoneVisible(this.rightBoot, pVisible);
        setBoneVisible(this.leftBoot, pVisible);
    }

    /**
     * Gets and caches the relevant armor model bones for this baked model if it hasn't been done already
     */
    public void grabRelevantBones(AzBakedModel model, AzArmorBoneProvider boneProvider) {
        if (this.lastModel == model) {
            return;
        }

        this.lastModel = model;
        this.head = boneProvider.getHeadBone(model);
        this.body = boneProvider.getBodyBone(model);
        this.rightArm = boneProvider.getRightArmBone(model);
        this.leftArm = boneProvider.getLeftArmBone(model);
        this.rightLeg = boneProvider.getRightLegBone(model);
        this.leftLeg = boneProvider.getLeftLegBone(model);
        this.rightBoot = boneProvider.getRightBootBone(model);
        this.leftBoot = boneProvider.getLeftBootBone(model);
    }

    /**
     * Transform the currently rendering {@link AzBakedModel} to match the positions and rotations of the base model
     */
    public void applyBaseTransformations(ModelBiped baseModel) {
        if (this.head != null) {
            ModelRenderer headPart = baseModel.bipedHead;

            RenderUtils.matchModelPartRot(headPart, this.head);
            this.head.updatePosition(headPart.offsetX, -headPart.offsetY, headPart.offsetZ);
        }

        if (this.body != null) {
            ModelRenderer bodyPart = baseModel.bipedBody;

            RenderUtils.matchModelPartRot(bodyPart, this.body);
            this.body.updatePosition(bodyPart.offsetX, -bodyPart.offsetY, bodyPart.offsetZ);
        }

        if (this.rightArm != null) {
            ModelRenderer rightArmPart = baseModel.bipedRightArm;

            RenderUtils.matchModelPartRot(rightArmPart, this.rightArm);
            this.rightArm.updatePosition(rightArmPart.offsetX + 5, 2 - rightArmPart.offsetY, rightArmPart.offsetZ);
        }

        if (this.leftArm != null) {
            ModelRenderer leftArmPart = baseModel.bipedLeftArm;

            RenderUtils.matchModelPartRot(leftArmPart, this.leftArm);
            this.leftArm.updatePosition(leftArmPart.offsetX - 5f, 2f - leftArmPart.offsetY, leftArmPart.offsetZ);
        }

        if (this.rightLeg != null) {
            ModelRenderer rightLegPart = baseModel.bipedRightLeg;

            RenderUtils.matchModelPartRot(rightLegPart, this.rightLeg);
            this.rightLeg.updatePosition(rightLegPart.offsetX + 2, 12 - rightLegPart.offsetY, rightLegPart.offsetZ);

            if (this.rightBoot != null) {
                RenderUtils.matchModelPartRot(rightLegPart, this.rightBoot);
                this.rightBoot.updatePosition(rightLegPart.offsetX + 2, 12 - rightLegPart.offsetY, rightLegPart.offsetZ);
            }
        }

        if (this.leftLeg != null) {
            ModelRenderer leftLegPart = baseModel.bipedLeftLeg;

            RenderUtils.matchModelPartRot(leftLegPart, this.leftLeg);
            this.leftLeg.updatePosition(leftLegPart.offsetX - 2, 12 - leftLegPart.offsetY, leftLegPart.offsetZ);

            if (this.leftBoot != null) {
                RenderUtils.matchModelPartRot(leftLegPart, this.leftBoot);
                this.leftBoot.updatePosition(leftLegPart.offsetX - 2, 12 - leftLegPart.offsetY, leftLegPart.offsetZ);
            }
        }
    }

    /**
     * Resets the bone visibility for the model based on the current {@link ModelRenderer} and {@link EntityEquipmentSlot}, and
     * then sets the bones relevant to the current part as visible for rendering.<br>
     * <br>
     * If you are rendering a geo entity with armor, you should probably be calling this prior to rendering
     */
    public void applyBoneVisibilityByPart(EntityEquipmentSlot currentSlot, ModelRenderer currentPart, ModelBiped model) {
        setAllVisible(false);

        currentPart.isHidden = false;
        AzBone bone = null;

        if (currentPart == model.bipedHeadwear || currentPart == model.bipedHead) {
            bone = this.head;
        } else if (currentPart == model.bipedBody) {
            bone = this.body;
        } else if (currentPart == model.bipedLeftArm) {
            bone = this.leftArm;
        } else if (currentPart == model.bipedRightArm) {
            bone = this.rightArm;
        } else if (currentPart == model.bipedLeftLeg) {
            bone = currentSlot == EntityEquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
        } else if (currentPart == model.bipedRightLeg) {
            bone = currentSlot == EntityEquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
        }

        if (bone != null) {
            bone.setHidden(false);
        }
    }

    /**
     * Resets the bone visibility for the model based on the currently rendering slot, and then sets bones relevant to
     * the current slot as visible for rendering.<br>
     * <br>
     * This is only called by default for non-geo entities (I.E. players or vanilla mobs)
     */
    public void applyBoneVisibilityBySlot(EntityEquipmentSlot currentSlot) {
        setAllVisible(false);

        switch (currentSlot) {
            case HEAD:
                setBoneVisible(this.head, true);
                break;
            case CHEST:
                setBoneVisible(this.body, true);
                setBoneVisible(this.rightArm, true);
                setBoneVisible(this.leftArm, true);
                break;
            case LEGS:
                setBoneVisible(this.rightLeg, true);
                setBoneVisible(this.leftLeg, true);
                break;
            case FEET:
                setBoneVisible(this.rightBoot, true);
                setBoneVisible(this.leftBoot, true);
                break;
            case MAINHAND:
            case OFFHAND:
                // NO-OP
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentSlot);
        }
    }

    /**
     * Sets a bone as visible or hidden, with nullability
     */
    protected void setBoneVisible(AzBone bone, boolean visible) {
        if (bone == null)
            return;

        bone.setHidden(!visible);
    }
}
