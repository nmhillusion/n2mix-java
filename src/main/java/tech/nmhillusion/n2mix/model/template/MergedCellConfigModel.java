package tech.nmhillusion.n2mix.model.template;

import tech.nmhillusion.n2mix.type.Stringeable;
import tech.nmhillusion.n2mix.util.CastUtil;

import java.util.Map;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-01-06
 */
public class MergedCellConfigModel extends Stringeable {
    private int fromColumnNum;
    private int toColumnNum;
    private int rowCount;

    public static MergedCellConfigModel from(Map<?, ?> rawConfig) {
        final MergedCellConfigModel model_ = new MergedCellConfigModel();

        if (rawConfig.containsKey("fromColumnNum")) {
            final int fromColumnNum = CastUtil.safeCast(
                    rawConfig.get("fromColumnNum")
                    , Integer.class
            );

            model_.setFromColumnNum(fromColumnNum);
        }

        if (rawConfig.containsKey("toColumnNum")) {
            final int toColumnNum = CastUtil.safeCast(
                    rawConfig.get("toColumnNum")
                    , Integer.class
            );

            model_.setToColumnNum(toColumnNum);
        }

        if (rawConfig.containsKey("rowCount")) {
            final int rowCount = CastUtil.safeCast(
                    rawConfig.get("rowCount")
                    , Integer.class
            );

            model_.setRowCount(rowCount);
        }

        return model_;
    }

    public int getFromColumnNum() {
        return fromColumnNum;
    }

    public MergedCellConfigModel setFromColumnNum(int fromColumnNum) {
        this.fromColumnNum = fromColumnNum;
        return this;
    }

    public int getToColumnNum() {
        return toColumnNum;
    }

    public MergedCellConfigModel setToColumnNum(int toColumnNum) {
        this.toColumnNum = toColumnNum;
        return this;
    }

    public int getRowCount() {
        return rowCount;
    }

    public MergedCellConfigModel setRowCount(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }
}
