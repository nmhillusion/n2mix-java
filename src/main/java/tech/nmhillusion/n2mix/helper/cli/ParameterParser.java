package tech.nmhillusion.n2mix.helper.cli;

import tech.nmhillusion.n2mix.model.cli.ParameterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-06-05
 */
public abstract class ParameterParser {
    public static List<ParameterModel> parse(String[] args) {
        final List<ParameterModel> parameterModels = new ArrayList<>();

        ParameterModel parameterModel = null;
        ParameterType lastParameterType = null;

        int argsLength = args.length;
        for (int argIdx = 0; argIdx < argsLength; ++argIdx) {
            final String arg_ = args[argIdx];

            ParameterType currentParameterType = null;
            if (arg_.startsWith("-")) {
                currentParameterType = ParameterType.NAME;
            } else {
                currentParameterType = ParameterType.VALUE;
            }

            if (null == parameterModel) {
                parameterModel = new ParameterModel();
            }


            if (ParameterType.NAME == lastParameterType) {
                if (ParameterType.VALUE == currentParameterType) {
                    parameterModel.setValue(arg_);
                    parameterModels.add(parameterModel);
                    parameterModel = null;
                } else if (ParameterType.NAME == currentParameterType) {
                    parameterModels.add(parameterModel);
                    parameterModel = new ParameterModel().setName(arg_);
                }
            } else if (ParameterType.VALUE == lastParameterType) {
                if (ParameterType.VALUE == currentParameterType) {
                    parameterModels.add(new ParameterModel().setValue(arg_));
                    parameterModel = null;
                }

                if (ParameterType.NAME == currentParameterType) {
                    parameterModel.setName(arg_);
                }
            } else if (null == lastParameterType) {
                if (ParameterType.VALUE == currentParameterType) {
                    parameterModels.add(new ParameterModel().setValue(arg_));
                    parameterModel = null;
                } else if (ParameterType.NAME == currentParameterType) {
                    parameterModel.setName(arg_);
                }
            }

            if (argsLength - 1 == argIdx) {
                if (null != parameterModel) {
                    parameterModels.add(parameterModel);
                }
            }

            lastParameterType = currentParameterType;
        }


        return parameterModels;
    }

    private enum ParameterType {
        NAME,
        VALUE
    }
}
