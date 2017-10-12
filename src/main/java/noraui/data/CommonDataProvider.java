package noraui.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import noraui.annotation.Column;
import noraui.exception.TechnicalException;
import noraui.model.Model;
import noraui.utils.Messages;

public abstract class CommonDataProvider implements DataProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CommonDataProvider.class);

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
        for (DataIndex id : indexData) {
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
            String[] packages = modelPackagesCsv.split(";");
            try {
                if (packages.length > 0) {
                    Set<Class<?>> returnedClasses;
                    for (String p : packages) {
                        returnedClasses = getClasses(p);
                        for (Class<?> c : returnedClasses) {
                            if (Model.class.isAssignableFrom(c)) {
                                boolean mappingOK = false;
                                for (Field f : c.getDeclaredFields()) {
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
            } catch (Exception e) {
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
     * {@inheritDoc}
     */
    @Override
    public boolean isResultColumnNameAuthorized(String name) {
        return AUTHORIZED_NAMES_FOR_RESULT_COLUMN.contains(name);
    }

    private Set<Class<?>> getClasses(String packageName) {
        return new Reflections(packageName, new SubTypesScanner(false)).getSubTypesOf(Object.class);
    }

}
