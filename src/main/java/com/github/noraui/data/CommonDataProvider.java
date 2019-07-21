/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.annotation.Column;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.model.Model;
import com.github.noraui.utils.Messages;

public abstract class CommonDataProvider implements DataProvider {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDataProvider.class);

    protected String dataInPath;
    protected String dataOutPath;
    protected List<DataIndex> indexData;
    protected String scenarioName;
    protected List<String> columns;
    protected String resultColumnName;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbGherkinExample() {
        if (indexData != null) {
            return indexData.size();
        } else {
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIndexData(List<DataIndex> indexDataIn) {
        indexData = new ArrayList<>(indexDataIn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataIndex getIndexData(int dataLine) {
        for (final DataIndex id : indexData) {
            if (id.getDataLine() == dataLine) {
                return id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<Model> getModel(String modelPackagesCsv) throws TechnicalException {
        if (modelPackagesCsv != null && !"".equals(modelPackagesCsv)) {
            final String[] packages = modelPackagesCsv.split(";");
            try {
                if (packages.length > 0) {
                    Set<Class<?>> returnedClasses;
                    LOGGER.debug("packages length is {}", packages.length);
                    for (final String p : packages) {
                        returnedClasses = getClasses(p);
                        LOGGER.debug("package [{}] return {} classes", p, returnedClasses.size());
                        for (final Class<?> c : returnedClasses) {
                            if (Model.class.isAssignableFrom(c)) {
                                boolean mappingOK = false;
                                for (final Field f : c.getDeclaredFields()) {
                                    if (f.isAnnotationPresent(Column.class)) {
                                        mappingOK = columns.contains(f.getAnnotation(Column.class).name());
                                    }
                                }
                                if (mappingOK) {
                                    return (Class<Model>) c;
                                }
                            }
                        }
                    }
                }
                return null;
            } catch (final Exception e) {
                throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
            }
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataInPath(String dataInPath) {
        this.dataInPath = dataInPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataOutPath(String dataOutPath) {
        this.dataOutPath = dataOutPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResultColumnName() {
        return resultColumnName;
    }

    /**
     * Writes a fail result
     *
     * @param line
     *            The line number
     * @param value
     *            The value
     */
    public void writeFailedResult(int line, String value) {
        LOGGER.debug("Write Failed result => line:{} value:{}", line, value);
        writeValue(resultColumnName, line, value);
    }

    /**
     * Writes a success result
     *
     * @param line
     *            The line number
     */
    public void writeSuccessResult(int line) {
        LOGGER.debug("Write Success result => line:{}", line);
        writeValue(resultColumnName, line, Messages.getMessage(Messages.SUCCESS_MESSAGE));
    }

    /**
     * Writes a warning result
     *
     * @param line
     *            The line number
     * @param value
     *            The value
     */
    public void writeWarningResult(int line, String value) {
        LOGGER.debug("Write Warning result => line:{} value:{}", line, value);
        writeValue(resultColumnName, line, value);
    }

    /**
     * Writes some data as result.
     *
     * @param column
     *            The column name
     * @param line
     *            The line number
     * @param value
     *            The data value
     */
    public void writeDataResult(String column, int line, String value) {
        LOGGER.debug("Write Data result => column:{} line:{} value:{}", column, line, value);
        writeValue(column, line, value);
    }

    /**
     * Defines how to write values. This method is a stub and must be overridden by children classes.
     *
     * @param column
     *            The column name
     * @param line
     *            The line number
     * @param value
     *            The value to write
     */
    protected void writeValue(String column, int line, String value) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResultColumnNameAuthorized(String name) {
        return ResultColumnNames.AUTHORIZED_NAMES.contains(name);
    }

    private Set<Class<?>> getClasses(String packageName) {
        return new Reflections(packageName, new SubTypesScanner(false)).getSubTypesOf(Object.class);
    }

    /**
     * Class that manages available column names used in Data Providers.
     *
     * @author Nicolas HALLOUIN
     */
    protected static class ResultColumnNames {
        public static final String RESULT_COLUMN_NAME = "RESULT_COLUMN_NAME";
        private static final List<String> AUTHORIZED_NAMES = new ArrayList<>(Arrays.asList("Résultat", "Result"));

        private ResultColumnNames() {
        }

        public static List<String> getAuthorizedNames() {
            return AUTHORIZED_NAMES;
        }
    }

}
