package tech.nmhillusion.n2mix.helper.cli;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.n2mix.model.cli.ParameterModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-06-05
 */
class ParameterParserTest {

    @Test
    void parseSimpleCommand() {
        final String[] args = new String[]{
                "-h"
        };

        final List<ParameterModel> parameterModels = ParameterParser.parse(args);

        assertEquals(1, parameterModels.size());
        assertEquals("-h", parameterModels.get(0).getName());
        assertNull(parameterModels.get(0).getValue());
    }

    @Test
    void parseSimpleCommand2() {
        final String[] args = new String[]{
                "/opt/data/abc.yml"
        };

        final List<ParameterModel> parameterModels = ParameterParser.parse(args);

        assertEquals(1, parameterModels.size());
        assertNull(parameterModels.get(0).getName());
        assertEquals("/opt/data/abc.yml", parameterModels.get(0).getValue());
    }

    @Test
    void parseCommandWithValue() {
        final String[] args = new String[]{
                "-t", "200"
        };

        final List<ParameterModel> parameterModels = ParameterParser.parse(args);

        assertEquals(1, parameterModels.size());
        assertEquals("-t", parameterModels.get(0).getName());
        assertEquals("200", parameterModels.get(0).getValue());
    }

    @Test
    void parseCommandWithTwoValues() {
        final String[] args = new String[]{
                "-t", "200", "-p", "300"
        };

        final List<ParameterModel> parameterModels = ParameterParser.parse(args);

        assertEquals(2, parameterModels.size());
        assertEquals("-t", parameterModels.get(0).getName());
        assertEquals("200", parameterModels.get(0).getValue());
        assertEquals("-p", parameterModels.get(1).getName());
        assertEquals("300", parameterModels.get(1).getValue());
    }

    @Test
    void parseCommandWithThreeValues() {
        final String[] args = new String[]{
                "-t", "200", "-p", "300", "-h"
        };

        final List<ParameterModel> parameterModels = ParameterParser.parse(args);

        assertEquals(3, parameterModels.size());
        assertEquals("-t", parameterModels.get(0).getName());
        assertEquals("200", parameterModels.get(0).getValue());
        assertEquals("-p", parameterModels.get(1).getName());
        assertEquals("300", parameterModels.get(1).getValue());
        assertEquals("-h", parameterModels.get(2).getName());
        assertNull(parameterModels.get(2).getValue());
    }

    @Test
    void parseCommandWithThreeValues2() {
        final String[] args = new String[]{
                "-t", "200", "-h", "-p", "300"
        };

        final List<ParameterModel> parameterModels = ParameterParser.parse(args);

        assertEquals(3, parameterModels.size());
        assertEquals("-t", parameterModels.get(0).getName());
        assertEquals("200", parameterModels.get(0).getValue());
        assertEquals("-h", parameterModels.get(1).getName());
        assertNull(parameterModels.get(1).getValue());
        assertEquals("-p", parameterModels.get(2).getName());
        assertEquals("300", parameterModels.get(2).getValue());
    }

    @Test
    void parseCommandWithThreeValues3() {
        final String[] args = new String[]{
                "-t", "200", "-h", "-p", "300", "abc.yml"
        };

        final List<ParameterModel> parameterModels = ParameterParser.parse(args);

        assertEquals(4, parameterModels.size());
        assertEquals("-t", parameterModels.get(0).getName());
        assertEquals("200", parameterModels.get(0).getValue());
        assertEquals("-h", parameterModels.get(1).getName());
        assertNull(parameterModels.get(1).getValue());
        assertEquals("-p", parameterModels.get(2).getName());
        assertEquals("300", parameterModels.get(2).getValue());
        assertNull(parameterModels.get(3).getName());
        assertEquals("abc.yml", parameterModels.get(3).getValue());
    }
}