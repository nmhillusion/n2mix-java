package tech.nmhillusion.n2mix.model.template;

import tech.nmhillusion.n2mix.type.Stringeable;

import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-01-06
 */
public class RowTemplateModel extends Stringeable {
    private List<String> content;
    private RowConfigModel config;

    public List<String> getContent() {
        return content;
    }

    public RowTemplateModel setContent(List<String> content) {
        this.content = content;
        return this;
    }

    public RowConfigModel getConfig() {
        return config;
    }

    public RowTemplateModel setConfig(RowConfigModel config) {
        this.config = config;
        return this;
    }
}
