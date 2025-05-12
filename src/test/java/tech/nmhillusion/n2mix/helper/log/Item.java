package tech.nmhillusion.n2mix.helper.log;

import tech.nmhillusion.n2mix.type.Stringeable;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-05-12
 */
public class Item extends Stringeable {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Item setValue(String value) {
        this.value = value;
        return this;
    }
}
