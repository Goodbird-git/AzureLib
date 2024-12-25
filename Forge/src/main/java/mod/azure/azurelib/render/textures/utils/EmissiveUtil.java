/**
 * Credit to: https://github.com/TeamChocoQuest/ChocolateQuestRepoured/blob/1.12.2/src/main/java/team/cqr/cqrepoured/client/util/EmissiveUtil.java
 */
package mod.azure.azurelib.render.textures.utils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public class EmissiveUtil {

    public static void preEmissiveTextureRendering() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void postEmissiveTextureRendering() {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }
}
