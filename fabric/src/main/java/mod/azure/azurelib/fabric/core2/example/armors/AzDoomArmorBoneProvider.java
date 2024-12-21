package mod.azure.azurelib.fabric.core2.example.armors;

import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.armor.bone.AzDefaultArmorBoneProvider;

public class AzDoomArmorBoneProvider extends AzDefaultArmorBoneProvider {

    // Only models I have on for armors with animations, the arm/legs are named wrong and i just didnt want to load it
    // in bb again to rename, this should be a feature kept though
    @Override
    public AzBone getLeftBootBone(AzBakedModel model) {
        return model.getBone("armorRightBoot").orElse(null);
    }

    @Override
    public AzBone getLeftLegBone(AzBakedModel model) {
        return model.getBone("armorRightLeg").orElse(null);
    }

    @Override
    public AzBone getRightBootBone(AzBakedModel model) {
        return model.getBone("armorLeftBoot").orElse(null);
    }

    @Override
    public AzBone getRightLegBone(AzBakedModel model) {
        return model.getBone("armorLeftLeg").orElse(null);
    }
}
