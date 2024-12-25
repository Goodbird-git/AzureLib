package mod.azure.azurelib.render.textures;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.render.textures.meta.AzGlowingTextureMeta;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Texture object type responsible for AzureLib's emissive render textures
 */
public class AzAutoGlowingTexture extends AbstractTexture  {

    private static final String APPENDIX = "_glowmask";

    protected final ResourceLocation textureBase;
    protected final ResourceLocation glowLayer;

    public AzAutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
        this.textureBase = originalLocation;
        this.glowLayer = location;
    }

    public static ResourceLocation get(ResourceLocation originalTexture) {
        String path = originalTexture.getResourcePath();
        int i = path.lastIndexOf('.');
        ResourceLocation glowingTexture = new ResourceLocation(originalTexture.getResourceDomain(), path.substring(0, i) + APPENDIX + path.substring(i));
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        if (renderManager.renderEngine.getTexture(glowingTexture) == null) {
            renderManager.renderEngine.loadTexture(glowingTexture, new AzAutoGlowingTexture(originalTexture, glowingTexture));
        }
        return glowingTexture;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();

        try (IResource iresource = resourceManager.getResource(this.textureBase)) {
            // Needed to get the GL-texture id
            ITextureObject ito = Minecraft.getMinecraft().renderEngine.getTexture(iresource.getResourceLocation());
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            BufferedImage glowingBI = new BufferedImage(bufferedimage.getWidth(), bufferedimage.getHeight(), bufferedimage.getType());

            boolean flag = false;
            boolean flag1 = false;

            if (iresource.hasMetadata()) {
                try {
                    // DONE: Fix this for the CTS!! Cts for whatever reason tries to load png as mcmeta file...
                    TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

                    if (texturemetadatasection != null) {
                        flag = texturemetadatasection.getTextureBlur();
                        flag1 = texturemetadatasection.getTextureClamp();
                    }

                    AzGlowingTextureMeta glowInformation = iresource.getMetadata("glowsections");
                    if (glowInformation != null) {
                        for (Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>> area : glowInformation.getGlowingSections()) {
                            for (int ix = area.getFirst().getFirst(); ix < area.getSecond().getFirst(); ix++) {
                                for (int iy = area.getFirst().getSecond(); iy < area.getSecond().getSecond(); iy++) {
                                    glowingBI.setRGB(ix, iy, bufferedimage.getRGB(ix, iy));

                                    // Remove it from the original
                                    bufferedimage.setRGB(ix, iy, 0);
                                }
                            }
                        }
                    }

                    /*
                     * String name = this.texture.getPath().replace("/", "-");
                     * File outputFile = new File(CQRMain.CQ_CONFIG_FOLDER, name);
                     * ImageIO.write(glowingBI, "png", outputFile);
                     */
                } catch (RuntimeException runtimeexception) {
                    AzureLib.LOGGER.warn("Failed reading metadata of: {}", this.textureBase, runtimeexception);
                }
            }

            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), glowingBI, flag, flag1);

            // Also upload the changes to the original texture...
            TextureUtil.uploadTextureImage(ito.getGlTextureId(), bufferedimage);
        }
    }
}