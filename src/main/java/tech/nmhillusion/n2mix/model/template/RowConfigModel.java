package tech.nmhillusion.n2mix.model.template;

import tech.nmhillusion.n2mix.type.Stringeable;
import tech.nmhillusion.n2mix.util.CastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-01-06
 */
public class RowConfigModel extends Stringeable {
    private List<FontConfigModel> fontConfigList;
    private List<MergedCellConfigModel> mergedCellConfigList;
    private List<BorderConfigModel> borderConfigList;
    private List<FillShapeConfigModel> fillShapeConfig;
    private boolean isFitContentColumns;

    public static RowConfigModel from(Map<?, ?> rawConfig) throws Throwable {
        final RowConfigModel model_ = new RowConfigModel();

        if (rawConfig.containsKey("isFitContentColumns")) {
            final boolean isFitContentColumns = CastUtil.safeCast(
                    rawConfig.get("isFitContentColumns")
                    , boolean.class
            );

            model_.setIsFitContentColumns(isFitContentColumns);
        }

        if (rawConfig.containsKey("font")) {
            if (rawConfig.get("font") instanceof List<?> rawConfigList) {
                final List<FontConfigModel> configList = new ArrayList<>();
                for (Object conf_ : rawConfigList) {
                    if (conf_ instanceof Map<?, ?>) {
                        configList.add(
                                FontConfigModel.from((Map<?, ?>) conf_)
                        );
                    }
                }
                model_.setFontConfigList(configList);
            }
        }

        if (rawConfig.containsKey("mergedCell")) {
            if (rawConfig.get("mergedCell") instanceof List<?> rawConfigList) {
                final List<MergedCellConfigModel> configList = new ArrayList<>();
                for (Object conf_ : rawConfigList) {
                    if (conf_ instanceof Map<?, ?>) {
                        configList.add(
                                MergedCellConfigModel.from((Map<?, ?>) conf_)
                        );
                    }
                }
                model_.setMergedCellConfigList(configList);
            }
        }

        if (rawConfig.containsKey("border")) {
            if (rawConfig.get("border") instanceof List<?> rawConfigList) {
                final List<BorderConfigModel> configList = new ArrayList<>();
                for (Object conf_ : rawConfigList) {
                    if (conf_ instanceof Map<?, ?>) {
                        configList.add(
                                BorderConfigModel.from((Map<?, ?>) conf_)
                        );
                    }
                }
                model_.setBorderConfigList(configList);
            }
        }

        if (rawConfig.containsKey("fillShape")) {
            if (rawConfig.get("fillShape") instanceof List<?> rawConfigList) {
                final List<FillShapeConfigModel> configList = new ArrayList<>();
                for (Object conf_ : rawConfigList) {
                    if (conf_ instanceof Map<?, ?>) {
                        configList.add(
                                FillShapeConfigModel.from((Map<?, ?>) conf_)
                        );
                    }
                }
                model_.setFillShapeConfig(configList);
            }
        }

        return model_;
    }

    public List<FontConfigModel> getFontConfigList() {
        return fontConfigList;
    }

    public RowConfigModel setFontConfigList(List<FontConfigModel> fontConfigList) {
        this.fontConfigList = fontConfigList;
        return this;
    }

    public List<MergedCellConfigModel> getMergedCellConfigList() {
        return mergedCellConfigList;
    }

    public RowConfigModel setMergedCellConfigList(List<MergedCellConfigModel> mergedCellConfigList) {
        this.mergedCellConfigList = mergedCellConfigList;
        return this;
    }

    public List<BorderConfigModel> getBorderConfigList() {
        return borderConfigList;
    }

    public RowConfigModel setBorderConfigList(List<BorderConfigModel> borderConfigList) {
        this.borderConfigList = borderConfigList;
        return this;
    }

    public List<FillShapeConfigModel> getFillShapeConfig() {
        return fillShapeConfig;
    }

    public RowConfigModel setFillShapeConfig(List<FillShapeConfigModel> fillShapeConfig) {
        this.fillShapeConfig = fillShapeConfig;
        return this;
    }

    public boolean getIsFitContentColumns() {
        return isFitContentColumns;
    }

    public RowConfigModel setIsFitContentColumns(boolean fitContentColumns) {
        isFitContentColumns = fitContentColumns;
        return this;
    }
}
