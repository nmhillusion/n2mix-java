package tech.nmhillusion.n2mix.helper.cli;

import tech.nmhillusion.n2mix.model.cli.ParameterModel;
import tech.nmhillusion.n2mix.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-06-05
 */
public abstract class ParameterParser {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("-{1,2}[a-zA-Z0-9_]+");

    private static String getParameterName(String arg) {
        if (arg.startsWith("-")) {
            return arg.replaceAll("-", "");
        } else {
            return null;
        }
    }

    public static List<ParameterModel> parse(String[] args) {
        final List<ParameterModel> parameterModels = new ArrayList<>();

        ParameterModel parameterModel = null;
        ParameterType lastParameterType = null;

        int argsLength = args.length;
        for (int argIdx = 0; argIdx < argsLength; ++argIdx) {
            final String arg_ = args[argIdx];

            String parameterName = null;

            ParameterType currentParameterType = null;
            if (arg_.startsWith("-")) {
                if (!VALID_NAME_PATTERN.matcher(arg_).matches()) {
                    throw new IllegalArgumentException("Parameter name is invalid: " + arg_);
                }

                currentParameterType = ParameterType.NAME;
                parameterName = getParameterName(arg_);
                removeExistedParameters(parameterModels, parameterName);
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
                    parameterModel = new ParameterModel().setName(parameterName);
                }
            } else if (ParameterType.VALUE == lastParameterType) {
                if (ParameterType.VALUE == currentParameterType) {
                    parameterModels.add(new ParameterModel().setValue(arg_));
                    parameterModel = null;
                }

                if (ParameterType.NAME == currentParameterType) {
                    parameterModel.setName(parameterName);
                }
            } else if (null == lastParameterType) {
                if (ParameterType.VALUE == currentParameterType) {
                    parameterModels.add(new ParameterModel().setValue(arg_));
                    parameterModel = null;
                } else if (ParameterType.NAME == currentParameterType) {
                    parameterModel.setName(parameterName);
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

    private static void removeExistedParameters(List<ParameterModel> parameterModels, String parameterName) {
        parameterModels.removeIf(parameterModel -> StringUtil.trimWithNull(parameterName).equals(parameterModel.getName()));
    }

    private enum ParameterType {
        NAME,
        VALUE
    }
}
