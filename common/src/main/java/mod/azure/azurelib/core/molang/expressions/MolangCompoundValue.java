/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core.molang.expressions;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.core.molang.LazyVariable;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * An extension of the {@link MolangValue} class, allowing for compound expressions.
 */
public class MolangCompoundValue extends MolangValue {

    public final List<MolangValue> values = new ObjectArrayList<>();

    public final Map<String, LazyVariable> locals = new Object2ObjectOpenHashMap<>();

    public MolangCompoundValue(MolangValue baseValue) {
        super(baseValue);

        this.values.add(baseValue);
    }

    @Override
    public double get() {
        double value = 0;

        for (MolangValue molangValue : this.values) {
            value = molangValue.get();
        }

        return value;
    }

    @Override
    public String toString() {
        StringJoiner builder = new StringJoiner("; ");

        for (MolangValue molangValue : this.values) {
            builder.add(molangValue.toString());

            if (molangValue.isReturnValue())
                break;
        }

        return builder.toString();
    }
}
