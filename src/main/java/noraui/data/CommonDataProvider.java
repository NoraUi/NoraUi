package noraui.data;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import noraui.annotation.Column;
import noraui.exception.TechnicalException;
import noraui.model.Model;

public abstract class CommonDataProvider implements DataProvider {

    protected static final Logger logger = Logger.getLogger(CommonDataProvider.class);

    protected String dataInPath;
    protected String dataOutPath;
    protected List<DataIndex> indexData;
    protected String scenarioName;
    protected List<String> columns;

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
                logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION, e);
                throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION, e);
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

    private Set<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        return new Reflections(packageName, new SubTypesScanner(false)).getSubTypesOf(Object.class);
    }

}
