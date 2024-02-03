package tech.nmhillusion.n2mix.model.template;

import org.apache.poi.ss.usermodel.BorderStyle;
import tech.nmhillusion.n2mix.type.Stringeable;
import tech.nmhillusion.n2mix.type.function.ThrowableVoidFunction;
import tech.nmhillusion.n2mix.util.CastUtil;

import java.util.Map;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-01-06
 */
public class BorderConfigModel extends Stringeable {
    private BorderStyle borderStyleForAll;
    private BorderStyle topBorderStyle;
    private BorderStyle bottomBorderStyle;
    private BorderStyle leftBorderStyle;
    private BorderStyle rightBorderStyle;
    private EffectedCellsModel effectedCells;

    private static BorderStyle parseBorderStyleValue(Object rawValue) {
        final String rawBorderStyle = CastUtil.safeCast(
                rawValue
                , String.class
        );
        final BorderStyle borderStyle = BorderStyle.valueOf(
                rawBorderStyle
        );

        return borderStyle;
    }

    private static void actionOnBorderStyle(Map<?, ?> rawConfig, String borderTypeKey, ThrowableVoidFunction<BorderStyle> actionFunc) throws Throwable {
        if (rawConfig.containsKey(borderTypeKey)) {
            final BorderStyle borderStyle_ = parseBorderStyleValue(
                    rawConfig.get(borderTypeKey)
            );
            actionFunc.throwableVoidApply(borderStyle_);
        }
    }

    public static BorderConfigModel from(Map<?, ?> rawConfig) throws Throwable {
        final BorderConfigModel model_ = new BorderConfigModel();

        actionOnBorderStyle(rawConfig, "borderStyleForAll", model_::setBorderStyleForAll);
        actionOnBorderStyle(rawConfig, "topBorderStyle", model_::setTopBorderStyle);
        actionOnBorderStyle(rawConfig, "bottomBorderStyle", model_::setBottomBorderStyle);
        actionOnBorderStyle(rawConfig, "leftBorderStyle", model_::setLeftBorderStyle);
        actionOnBorderStyle(rawConfig, "rightBorderStyle", model_::setRightBorderStyle);

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

    public BorderStyle getBorderStyleForAll() {
        return borderStyleForAll;
    }

    public BorderConfigModel setBorderStyleForAll(BorderStyle borderStyleForAll) {
        this.borderStyleForAll = borderStyleForAll;
        return this;
    }

    public BorderStyle getTopBorderStyle() {
        return topBorderStyle;
    }

    public BorderConfigModel setTopBorderStyle(BorderStyle topBorderStyle) {
        this.topBorderStyle = topBorderStyle;
        return this;
    }

    public BorderStyle getBottomBorderStyle() {
        return bottomBorderStyle;
    }

    public BorderConfigModel setBottomBorderStyle(BorderStyle bottomBorderStyle) {
        this.bottomBorderStyle = bottomBorderStyle;
        return this;
    }

    public BorderStyle getLeftBorderStyle() {
        return leftBorderStyle;
    }

    public BorderConfigModel setLeftBorderStyle(BorderStyle leftBorderStyle) {
        this.leftBorderStyle = leftBorderStyle;
        return this;
    }

    public BorderStyle getRightBorderStyle() {
        return rightBorderStyle;
    }

    public BorderConfigModel setRightBorderStyle(BorderStyle rightBorderStyle) {
        this.rightBorderStyle = rightBorderStyle;
        return this;
    }

    public EffectedCellsModel getEffectedCells() {
        return effectedCells;
    }

    public BorderConfigModel setEffectedCells(EffectedCellsModel effectedCells) {
        this.effectedCells = effectedCells;
        return this;
    }
}
