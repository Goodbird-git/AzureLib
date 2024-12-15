package mod.azure.azurelib.fabric.core2.example.armors;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;

public class DoomArmor extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private static final String EQUIP_ANIMATION_NAME = "equipping";

    private static final RawAnimation EQUIP_ANIMATION = RawAnimation.begin().thenLoop(EQUIP_ANIMATION_NAME);

    public DoomArmor(Type type) {
        super(ArmorMaterials.NETHERITE, type, new Properties());
    }

    @Override
    public void createRenderer(Consumer<RenderProvider> consumer) {
        consumer.accept(new RenderProvider() {

            private ArmorRenderer<DoomArmor> renderer;

            @Override
            public HumanoidModel<LivingEntity> getHumanoidArmorModel(
                LivingEntity livingEntity,
                ItemStack itemStack,
                EquipmentSlot equipmentSlot,
                HumanoidModel<LivingEntity> original
            ) {
                if (renderer == null) {
                    renderer = new ArmorRenderer<DoomArmor>();
                }
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, "base_controller", 0, state -> PlayState.CONTINUE).triggerableAnim(
                EQUIP_ANIMATION_NAME,
                EQUIP_ANIMATION
            )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
