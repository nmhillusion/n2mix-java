package tech.nmhillusion.n2mix.model.cli;

import tech.nmhillusion.n2mix.type.Stringeable;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-06-05
 */
public class ParameterModel extends Stringeable {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public ParameterModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ParameterModel setValue(String value) {
        this.value = value;
        return this;
    }
}
