package mod.azure.azurelib.resource;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSectionSerializer;

public class AzureAnimationMetadataSection extends AnimationMetadataSection {
	public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
	public static final String SECTION_NAME = "animation";
	public static final int DEFAULT_FRAME_TIME = 1;
	public static final int UNKNOWN_SIZE = -1;
	public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
		public Pair<Integer, Integer> getFrameSize(int p_119054_, int p_119055_) {
			return Pair.of(p_119054_, p_119055_);
		}
	};
	private final List<AnimationFrame> frames;
	private final int frameWidth;
	private final int frameHeight;
	private final int defaultFrameTime;
	private final boolean interpolatedFrames;

	public AzureAnimationMetadataSection(List<AnimationFrame> list, int frameWidth, int frameHeight, int defaultFrameTime, boolean interpolatedFrames) {
        super(list, frameWidth, frameHeight, defaultFrameTime, interpolatedFrames);
        this.frames = list;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.defaultFrameTime = defaultFrameTime;
		this.interpolatedFrames = interpolatedFrames;
	}

	private static boolean isDivisionInteger(int p_119034_, int p_119035_) {
		return p_119034_ / p_119035_ * p_119035_ == p_119034_;
	}

	public Pair<Integer, Integer> getFrameSize(int p_119028_, int p_119029_) {
		Pair<Integer, Integer> pair = this.calculateFrameSize(p_119028_, p_119029_);
		int i = pair.getFirst();
		int j = pair.getSecond();
		if (isDivisionInteger(p_119028_, i) && isDivisionInteger(p_119029_, j)) {
			return pair;
		} else {
			throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", p_119028_, p_119029_, i, j));
		}
	}

	private Pair<Integer, Integer> calculateFrameSize(int p_119040_, int p_119041_) {
		if (this.frameWidth != -1) {
			return this.frameHeight != -1 ? Pair.of(this.frameWidth, this.frameHeight) : Pair.of(this.frameWidth, p_119041_);
		} else if (this.frameHeight != -1) {
			return Pair.of(p_119040_, this.frameHeight);
		} else {
			int i = Math.min(p_119040_, p_119041_);
			return Pair.of(i, i);
		}
	}

	public int getFrameHeight(int p_119027_) {
		return this.frameHeight == -1 ? p_119027_ : this.frameHeight;
	}

	public int getFrameWidth(int p_119032_) {
		return this.frameWidth == -1 ? p_119032_ : this.frameWidth;
	}

	public int getDefaultFrameTime() {
		return this.defaultFrameTime;
	}

	public boolean isInterpolatedFrames() {
		return this.interpolatedFrames;
	}

	public void forEachFrame(AzureAnimationMetadataSection.FrameOutput p_174862_) {
		for (AnimationFrame animationframe : this.frames) {
			p_174862_.accept(animationframe.getIndex(), animationframe.getTime());
		}

	}

	public interface FrameOutput {
		void accept(int p_174864_, int p_174865_);
	}
}