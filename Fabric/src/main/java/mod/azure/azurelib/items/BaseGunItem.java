package mod.azure.azurelib.items;

import java.util.List;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager.ControllerRegistrar;
import mod.azure.azurelib.core.animation.Animation.LoopType;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class BaseGunItem extends Item implements GeoItem {

	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

	/*
	 * Make sure the durability is always +1 from what you a gun to use. This is make the item stops at 1 durablity properly. Example: Clip size of 20 would be registered with a durability of 21.
	 */
	public BaseGunItem(Properties properties) {
		super(properties);
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "shoot_controller", event -> PlayState.CONTINUE).triggerableAnim("firing", RawAnimation.begin().then("firing", LoopType.PLAY_ONCE)));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	public void removeAmmo(Item ammo, Player playerEntity) {
		if (!playerEntity.isCreative()) {
			for (ItemStack item : playerEntity.inventory.offhand) {
				if (item.getItem() == ammo) {
					item.shrink(1);
					break;
				}
				for (ItemStack item1 : playerEntity.inventory.items) {
					if (item1.getItem() == ammo) {
						item1.shrink(1);
						break;
					}
				}
			}
		}
	}

	public void removeOffHandItem(Item ammo, Player playerEntity) {
		if (!playerEntity.isCreative()) {
			for (ItemStack item : playerEntity.inventory.offhand) {
				if (item.getItem() == ammo) {
					item.shrink(1);
					break;
				}
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		user.startUsingItem(hand);
		return InteractionResultHolder.consume(itemStack);
	}

	/*
	 * Turns off the enchanted glint. Useful for Arachnids that uses enchantments for attachments.
	 */
	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	/*
	 * Only here so mobs can use the guns too with the bow like goal/tasks.
	 */
	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	/*
	 * Adds Ammo tooltip.
	 */
	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		tooltip.add(new TranslatableComponent("Ammo: " + (stack.getMaxDamage() - stack.getDamageValue() - 1) + " / " + (stack.getMaxDamage() - 1)).withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	/*
	 * Makes the item not use enchantments in the enchament table.
	 */
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static EntityHitResult hitscanTrace(Player player, double range, float ticks) {
		Vec3 look = player.getViewVector(ticks);
		Vec3 start = player.getEyePosition(ticks);
		Vec3 end = new Vec3(player.getX() + look.x * range, player.getEyeY() + look.y * range, player.getZ() + look.z * range);
		double traceDistance = player.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getLocation().distanceToSqr(end);
		for (Entity possible : player.level.getEntities(player, player.getBoundingBox().expandTowards(look.scale(traceDistance)).expandTowards(3.0D, 3.0D, 3.0D), (entity -> !entity.isSpectator() && entity.isPickable() && entity instanceof LivingEntity))) {
			if (possible.getBoundingBox().inflate(0.3D).clip(start, end).isPresent())
				if (start.distanceToSqr(possible.getBoundingBox().inflate(0.3D).clip(start, end).get()) < traceDistance)
					return ProjectileUtil.getEntityHitResult(player.level, player, start, end, player.getBoundingBox().expandTowards(look.scale(traceDistance)).inflate(3.0D, 3.0D, 3.0D), (target) -> !target.isSpectator() && player.isAttackable() && player.canSee(target));
		}
		return null;
	}

}