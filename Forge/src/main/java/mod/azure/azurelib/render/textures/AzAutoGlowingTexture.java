package mod.azure.azurelib.render.textures;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.render.textures.meta.AzGlowingTextureMeta;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Texture object type responsible for AzureLib's emissive render textures
 */
public class AzAutoGlowingTexture {

    static class GlowRenderType {

        /**
         * Sets up the OpenGL states for emissive rendering.
         */
        public static void setupEmissiveRender(ResourceLocation texture) {
            // Bind the texture
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.disableCull();

            GlStateManager.disableLighting();
            GlStateManager.enableLight(0);
            GlStateManager.enableLight(1);

            int lightmapCoords = 0xF000F0;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapCoords % 0xFFFF, lightmapCoords / 0xFFFF);
        }

        /**
         * Cleans up the OpenGL states after emissive rendering.
         */
        public static void cleanupEmissiveRender() {
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.disableLight(0);
            GlStateManager.disableLight(1);

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xFFFF, 0xFFFF);
        }
    }

    private static final String APPENDIX = "_glowmask";

    protected final ResourceLocation textureBase;
    protected final ResourceLocation glowLayer;

    public AzAutoGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
        this.textureBase = originalLocation;
        this.glowLayer = location;
    }

    /**
     * Get the emissive resource equivalent of the input resource path.<br>
     * Additionally prepares the texture manager for the missing texture if the resource is not present
     *
     * @return The glowlayer resourcepath for the provided input path
     */
    protected static ResourceLocation getEmissiveResource(ResourceLocation baseResource) {
        ResourceLocation path = appendToPath(baseResource, APPENDIX);

        generateTexture(path, textureManager -> textureManager.loadTexture(path, new AzAutoGlowingTexture(baseResource, path)));

        return path;
    }

    /**
     * Generates the glow layer {@link NativeImage} and appropriately modifies the base texture for use in glow render layers
     */
    @Override
    protected IRenderCall loadTexture(IResourceManager resourceManager, Minecraft mc) throws IOException {
        Texture originalTexture;

        try {
            originalTexture = mc.supplyAsync(() -> mc.getTextureManager().getTexture(this.textureBase)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to load original texture: " + this.textureBase, e);
        }

        IResource textureBaseResource = resourceManager.getResource(this.textureBase);
        NativeImage baseImage = originalTexture instanceof DynamicTexture ? ((DynamicTexture) originalTexture).getTextureData() : NativeImage.read(textureBaseResource.getInputStream());
        NativeImage glowImage = null;
        TextureMetadataSection textureBaseMeta = textureBaseResource.getMetadata(TextureMetadataSection.SERIALIZER);
        boolean blur = textureBaseMeta != null && textureBaseMeta.getTextureBlur();
        boolean clamp = textureBaseMeta != null && textureBaseMeta.getTextureClamp();

        try {
            IResource glowLayerResource = resourceManager.getResource(this.glowLayer);
            AzGlowingTextureMeta glowLayerMeta = null;

            if (glowLayerResource != null) {
                glowImage = NativeImage.read(glowLayerResource.getInputStream());
                glowLayerMeta = AzGlowingTextureMeta.fromExistingImage(glowImage);
            } else {
                AzGlowingTextureMeta meta = textureBaseResource.getMetadata(AzGlowingTextureMeta.DESERIALIZER);

                if (meta != null) {
                    glowLayerMeta = meta;
                    glowImage = new NativeImage(baseImage.getWidth(), baseImage.getHeight(), true);
                }
            }

            if (glowLayerMeta != null) {
                glowLayerMeta.createImageMask(baseImage, glowImage);
            }
        } catch (IOException e) {
            AzureLib.LOGGER.warn("Resource failed to open for glowlayer meta: {}", this.glowLayer, e);
        }

        NativeImage mask = glowImage;

        if (mask == null)
            return null;

        return () -> {
            uploadSimple(getGlTextureId(), mask, blur, clamp);

            if (originalTexture instanceof DynamicTexture) {
                ((DynamicTexture) originalTexture).updateDynamicTexture();
            } else {
                uploadSimple(originalTexture.getGlTextureId(), baseImage, blur, clamp);
            }
        };
    }

    /**
     * Return a cached instance of the RenderType for the given texture for GeoGlowingLayer rendering.
     *
     * @param texture The texture of the resource to apply a glow layer to
     */
    public static void getRenderType(ResourceLocation texture) {
        GlowRenderType.setupEmissiveRender(texture);
    }
}