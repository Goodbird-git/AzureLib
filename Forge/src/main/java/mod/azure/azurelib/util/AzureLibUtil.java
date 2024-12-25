package mod.azure.azurelib.util;

import mod.azure.azurelib.model.factory.AzBakedModelFactory;
import mod.azure.azurelib.model.factory.registry.AzBakedModelFactoryRegistry;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;

import javax.annotation.Nullable;

/**
 * Helper class for various AzureLib-specific functions.
 */
public final class AzureLibUtil {

	public static <T> T self(Object object) {
		return (T) object;
	}

	/**
	 * Register a custom {@link AzBakedModelFactory} with AzureLib, allowing for dynamic handling of geo model
	 * loading.<br>
	 * <b><u>MUST be called during mod construct</u></b><br>
	 *
	 * @param namespace The namespace (modid) to register the factory for
	 * @param factory   The factory responsible for model loading under the given namespace
	 */
	public static synchronized void addCustomBakedModelFactory(String namespace, AzBakedModelFactory factory) {
		AzBakedModelFactoryRegistry.register(namespace, factory);
	}

	/**
	 * Summons an Area of Effect Cloud with the set particle, y offset, radius, duration, and effect options.
	 * 
	 * @param entity     The Entity summoning the AoE
	 * @param particle   Sets the Particle
	 * @param yOffset    Set the yOffset if wanted
	 * @param duration   Sets the duration of the AoE
	 * @param radius     Sets the radius of the AoE
	 * @param hasEffect  Should this have an effect?
	 * @param effect     If it should effect, what effect?
	 * @param effectTime How long the effect should be applied for?
	 */
	public static void summonAoE(EntityLiving entity, EnumParticleTypes particle, int yOffset, int duration, float radius, boolean hasEffect, @Nullable Potion effect, int effectTime) {
		EntityAreaEffectCloud areaEffectCloudEntity = new EntityAreaEffectCloud(entity.world, entity.posX, entity.posY + yOffset, entity.posZ);
		areaEffectCloudEntity.setRadius(radius);
		areaEffectCloudEntity.setDuration(duration);
		areaEffectCloudEntity.setParticle(particle);
		areaEffectCloudEntity.setRadiusPerTick(-areaEffectCloudEntity.getRadius() / areaEffectCloudEntity.getDuration());
		if (hasEffect && !entity.isPotionActive(effect))
			areaEffectCloudEntity.addEffect(new PotionEffect(effect, effectTime, 0));
		entity.world.onEntityAdded(areaEffectCloudEntity);
	}

	/**
	 * Gets the NBT tag of the item stack, or creates a new one if it doesn't exist.
	 *
	 * @param stack the item stack
	 * @return the NBT tag
	 */
	public static NBTTagCompound getOrCreateTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}
}
