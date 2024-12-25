/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.render.textures;

import com.mojang.realmsclient.util.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.render.textures.meta.AzureAnimationMetadataSection;
import mod.azure.azurelib.render.textures.utils.Frame;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for {@link SimpleTexture SimpleTexture} implementation allowing for casual use of animated non-atlas textures
 */
public class AnimatableTexture extends SimpleTexture {
    private AnimationContents animationContents = null;
    private boolean isAnimated = false;

    public AnimatableTexture(final ResourceLocation location) {
        super(location);
    }

    @Override
    public void loadTexture(IResourceManager manager) throws IOException {
        this.deleteGlTexture();

        try (IResource resource = manager.getResource(this.textureLocation)) {
            TextureMetadataSection simpleTextureMeta = new TextureMetadataSection(false, false);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(resource.getInputStream());
            BufferedImage animitedImage = new BufferedImage(bufferedimage.getWidth(), bufferedimage.getHeight(), bufferedimage.getType());

            if (resource.getMetadata("texture") != null && resource.getMetadata("animation") != null ) {
                // Retrieve metadata for the texture section
                TextureMetadataSection textureMeta = resource.getMetadata("texture");
                if (textureMeta != null) {
                    simpleTextureMeta = textureMeta;
                }

                // Retrieve metadata for the animation section
                AnimationMetadataSection animMeta = resource.getMetadata("animation");
                if (animMeta != null) {
                    this.animationContents = new AnimationContents(animitedImage,
                            (AzureAnimationMetadataSection) animMeta);
                } else {
                    this.animationContents = null;
                }

                if (this.animationContents != null) {
                    if (!this.animationContents.isValid()) {
                        return;
                    }
                    this.isAnimated = true;

                    TextureUtil.allocateTexture(getGlTextureId(), this.animationContents.frameSize.first(),
                            this.animationContents.frameSize.second());
                    TextureUtil.uploadTextureImageSub(getGlTextureId(), animitedImage, this.animationContents.frameSize.first(),
                            this.animationContents.frameSize.second(), false, false);
                }
            }
        } catch (RuntimeException exception) {
            AzureLib.LOGGER.warn("Failed reading metadata of: {}", this.textureLocation, exception);
        }
    }

    /**
     * Returns whether the texture found any valid animation metadata when loading.
     * <p>
     * If false, then this is no different to a standard {@link SimpleTexture}
     */
    public boolean isAnimated() {
        return this.isAnimated;
    }

    public static void setAndUpdate(ResourceLocation texturePath, int frameTick) {
        ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(texturePath);

        if (texture instanceof ITextureObject)
            ((AnimatableTexture) texture).setAnimationFrame(frameTick);
    }

    public void setAnimationFrame(int tick) {
        if (this.animationContents != null)
            this.animationContents.animatedTexture.setCurrentFrame(tick);
    }

    class AnimationContents {
        private final Pair<Integer, Integer> frameSize;
        private final Texture animatedTexture;

        private AnimationContents(BufferedImage image, AzureAnimationMetadataSection animMeta) {
            this.frameSize = animMeta.getFrameSize(image.getWidth(), image.getHeight());
            this.animatedTexture = generateAnimatedTexture(image, animMeta);
        }

        private boolean isValid() {
            return this.animatedTexture != null;
        }

        private Texture generateAnimatedTexture(BufferedImage image, AzureAnimationMetadataSection animMeta) {
            if (!RenderUtils.isMultipleOf(image.getWidth(), this.frameSize.first()) || !RenderUtils.isMultipleOf(
                    image.getHeight(), this.frameSize.second())) {
                AzureLib.LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}",
                        AnimatableTexture.this.textureLocation, image.getWidth(), image.getHeight(), this.frameSize.first(),
                        this.frameSize.second());

                return null;
            }

            int columns = image.getWidth() / this.frameSize.first();
            int rows = image.getHeight() / this.frameSize.second();
            int frameCount = columns * rows;
            List<Frame> frames = new ObjectArrayList<>();

            animMeta.forEachFrame((frame, frameTime) -> frames.add(new Frame(frame, frameTime)));

            if (frames.isEmpty()) {
                for (int frame = 0; frame < frameCount; ++frame) {
                    frames.add(new Frame(frame, animMeta.getDefaultFrameTime()));
                }
            } else {
                int index = 0;
                IntSet unusedFrames = new IntOpenHashSet();

                for (Frame frame : frames) {
                    if (frame.time <= 0) {
                        AzureLib.LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}",
                                AnimatableTexture.this.textureLocation, index, frame.time);
                        unusedFrames.add(frame.index);
                    } else if (frame.index < 0 || frame.index >= frameCount) {
                        AzureLib.LOGGER.warn("Invalid frame index on sprite {} frame {}: {}",
                                AnimatableTexture.this.textureLocation, index, frame.index);
                        unusedFrames.add(frame.index);
                    }

                    index++;
                }

                if (!unusedFrames.isEmpty())
                    AzureLib.LOGGER.warn("Unused frames in sprite {}: {}", AnimatableTexture.this.textureLocation,
                            Arrays.toString(unusedFrames.toArray()));
            }

            return frames.size() <= 1 ? null : new Texture(image, frames.toArray(new Frame[0]), columns,
                    animMeta.isInterpolatedFrames(), this.frameSize.first(), this.frameSize.second(), getGlTextureId());
        }

        class Texture implements AutoCloseable {
            private final BufferedImage baseImage;
            private final Frame[] frames;
            private final int framePanelSize;
            private final boolean interpolating;
            private final BufferedImage interpolatedFrame;
            private final int totalFrameTime;
            private int frameFirst;
            private int frameLast;
            private int id;

            private int currentFrame;
            private int currentSubframe;

            private Texture(BufferedImage baseImage, Frame[] frames, int framePanelSize, boolean interpolating, int frameFirst, int frameLast, int id) {
                this.baseImage = baseImage;
                this.frames = frames;
                this.framePanelSize = framePanelSize;
                this.interpolating = interpolating;
                this.frameFirst = frameFirst;
                this.frameLast = frameLast;
                this.id = id;
                this.interpolatedFrame = interpolating ? new BufferedImage(frameFirst,
                        frameLast, 1) : null;
                int time = 0;

                for (Frame frame : this.frames) {
                    time += frame.time;
                }

                this.totalFrameTime = time;
            }

            private int getFrameX(int frameIndex) {
                return frameIndex % this.framePanelSize;
            }

            private int getFrameY(int frameIndex) {
                return frameIndex / this.framePanelSize;
            }

            public void setCurrentFrame(int ticks) {
                ticks %= this.totalFrameTime;

                if (ticks == this.currentSubframe)
                    return;

                int lastSubframe = this.currentSubframe;
                int lastFrame = this.currentFrame;
                int time = 0;

                for (Frame frame : this.frames) {
                    time += frame.time;

                    if (ticks < time) {
                        this.currentFrame = frame.index;
                        this.currentSubframe = ticks % frame.time;

                        break;
                    }
                }

                if (this.currentFrame != lastFrame && this.currentSubframe == 0) {
                        TextureUtil.allocateTexture(this.id,
                                this.frameFirst,
                                this.frameLast);
                    TextureUtil.uploadTextureImageSub(this.id, null, getFrameX(this.currentFrame) * this.frameFirst, getFrameY(this.currentFrame) * this.frameLast, false, false);
                } else if (this.currentSubframe != lastSubframe && this.interpolating) {
                    this.generateInterpolatedFrame();
                }
            }

            private void generateInterpolatedFrame() {
                Frame frame = this.frames[this.currentFrame];
                double frameProgress = 1 - (double) this.currentSubframe / (double) frame.time;
                int nextFrameIndex = this.frames[(this.currentFrame + 1) % this.frames.length].index;

                if (frame.index != nextFrameIndex) {
                    for (int y = 0; y < this.interpolatedFrame.getHeight(); ++y) {
                        for (int x = 0; x < this.interpolatedFrame.getWidth(); ++x) {
                            int prevFramePixel = getPixel(frame.index, x, y);
                            int nextFramePixel = getPixel(nextFrameIndex, x, y);
                            int blendedRed = interpolate(frameProgress, prevFramePixel >> 16 & 255,
                                    nextFramePixel >> 16 & 255);
                            int blendedGreen = interpolate(frameProgress, prevFramePixel >> 8 & 255,
                                    nextFramePixel >> 8 & 255);
                            int blendedBlue = interpolate(frameProgress, prevFramePixel & 255, nextFramePixel & 255);

                            this.interpolatedFrame.setRGB(x, y,
                                    prevFramePixel & -16777216 | blendedRed << 16 | blendedGreen << 8 | blendedBlue);
                        }
                    }

                    TextureUtil.allocateTexture(this.id,
                            this.frameFirst, this.frameLast);
                    TextureUtil.uploadTextureImageSub(this.id, null, this.frameFirst,
                            this.frameLast, false, false);
                }
            }

            private int getPixel(int frameIndex, int x, int y) {
                return this.baseImage.getRGB(x + getFrameX(frameIndex) * this.frameFirst, y + getFrameY(frameIndex) * this.frameLast);
            }

            private int interpolate(double frameProgress, double prevColour, double nextColour) {
                return (int) (frameProgress * prevColour + (1 - frameProgress) * nextColour);
            }

            @Override
            public void close() {
            }
        }
    }
}