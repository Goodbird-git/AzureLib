/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core.math;

/**
 * Variable class This class is responsible for providing a mutable {@link IValue} which can be modifier during runtime
 * and still getting referenced in the expressions parsed by {@link MathBuilder}. But in practice, it's simply returns
 * stored value and provides a method to modify it.
 */
public class Variable implements IValue {

    private String name;

    private double value;

    public Variable(String name, double value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Set the value of this variable
     */
    public void set(double value) {
        this.value = value;
    }

    @Override
    public double get() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
