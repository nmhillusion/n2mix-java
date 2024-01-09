package tech.nmhillusion.n2mix.model.template;

import tech.nmhillusion.n2mix.type.Stringeable;
import tech.nmhillusion.n2mix.util.CastUtil;

import java.util.Map;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-01-06
 */
public class FontConfigModel extends Stringeable {
    private boolean isBold;
    private String fontFamily;
    private int fontSize;
    private EffectedCellsModel effectedCells;

    public static FontConfigModel from(Map<?, ?> rawConfig) {
        final FontConfigModel model_ = new FontConfigModel();

        if (rawConfig.containsKey("isBold")) {
            final boolean isBold = CastUtil.safeCast(
                    rawConfig.get("isBold")
                    , Boolean.class
            );

            model_.setIsBold(isBold);
        }

        if (rawConfig.containsKey("fontFamily")) {
            final String fontFamily = CastUtil.safeCast(
                    rawConfig.get("fontFamily")
                    , String.class
            );

            model_.setFontFamily(fontFamily);
        }

        if (rawConfig.containsKey("fontSize")) {
            final int fontSize = CastUtil.safeCast(
                    rawConfig.get("fontSize")
                    , int.class
            );

            model_.setFontSize(fontSize);
        }

        if (rawConfig.containsKey("effectedCells")) {
            final Object effectedCells_ = rawConfig.get("effectedCells");
            if (effectedCells_ instanceof Map<?, ?>) {
                model_.setEffectedCells(
                        EffectedCellsModel.from((Map<?, ?>) effectedCells_)
                );
            }
        }

        return model_;
    }

    public boolean getIsBold() {
        return isBold;
    }

    public FontConfigModel setIsBold(boolean bold) {
        isBold = bold;
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public FontConfigModel setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public FontConfigModel setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public EffectedCellsModel getEffectedCells() {
        return effectedCells;
    }

    public FontConfigModel setEffectedCells(EffectedCellsModel effectedCells) {
        this.effectedCells = effectedCells;
        return this;
    }
}
