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
public class EffectedCellsModel extends Stringeable {
    private boolean isAllCells;
    private List<Integer> cells;

    public static EffectedCellsModel from(Map<?, ?> rawConfig) {
        final EffectedCellsModel mode_ = new EffectedCellsModel();

        if (rawConfig.containsKey("isAllCells")) {
            mode_.setIsAllCells(
                    CastUtil.safeCast(
                            rawConfig.get("isAllCells")
                            , Boolean.class
                    )
            );
        }

        if (rawConfig.containsKey("cells")) {
            final List<Integer> items = new ArrayList<>();
            final Object rawCells = rawConfig.get("cells");

            if (rawCells instanceof List<?> cells_) {
                for (Object cell_ : cells_) {
                    final Integer item_ = CastUtil.safeCast(cell_, Integer.class);

                    items.add(item_);
                }
            }

            mode_.setCells(items);
        }

        return mode_;
    }

    public boolean getIsAllCells() {
        return isAllCells;
    }

    public EffectedCellsModel setIsAllCells(boolean allCells) {
        isAllCells = allCells;
        return this;
    }

    public List<Integer> getCells() {
        return cells;
    }

    public EffectedCellsModel setCells(List<Integer> cells) {
        this.cells = cells;
        return this;
    }
}
