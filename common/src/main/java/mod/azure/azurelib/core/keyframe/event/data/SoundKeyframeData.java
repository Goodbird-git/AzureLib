/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core.keyframe.event.data;

import mod.azure.azurelib.core.keyframe.Keyframe;

import java.util.Objects;

/**
 * Sound {@link Keyframe} instruction holder
 */
public class SoundKeyframeData extends KeyFrameData {

    private final String sound;

    public SoundKeyframeData(Double startTick, String sound) {
        super(startTick);

        this.sound = sound;
    }

    /**
     * Gets the sound id given by the {@link Keyframe} instruction from the {@code animation.json}
     */
    public String getSound() {
        return this.sound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartTick(), this.sound);
    }
}
