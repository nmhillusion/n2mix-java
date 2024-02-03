package tech.nmhillusion.n2mix.model.template;

import tech.nmhillusion.n2mix.type.Stringeable;
import tech.nmhillusion.n2mix.util.CastUtil;

import java.awt.*;
import java.util.Map;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-01-06
 */
public class FillShapeConfigModel extends Stringeable {
    private Color backgroundColor;
    private EffectedCellsModel effectedCells;

    public static FillShapeConfigModel from(Map<?, ?> rawConfig) {
        final FillShapeConfigModel model_ = new FillShapeConfigModel();

        if (rawConfig.containsKey("backgroundColor")) {
            final String rawBackgroundColor = CastUtil.safeCast(
                    rawConfig.get("backgroundColor")
                    , String.class
            );

            final Color backgroundColor = Color.decode(rawBackgroundColor);

            model_.setBackgroundColor(backgroundColor);
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

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public FillShapeConfigModel setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public EffectedCellsModel getEffectedCells() {
        return effectedCells;
    }

    public FillShapeConfigModel setEffectedCells(EffectedCellsModel effectedCells) {
        this.effectedCells = effectedCells;
        return this;
    }
}
