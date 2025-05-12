package tech.nmhillusion.n2mix.helper.log;

import tech.nmhillusion.n2mix.type.Stringeable;

import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-05-12
 */
public class StateModel extends Stringeable {
    private String name;
    private String title;
    private List<Item> items;

    public String getName() {
        return name;
    }

    public StateModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public StateModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public StateModel setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
