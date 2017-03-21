package noraui.data.excel;

import noraui.exception.TechnicalException;
import noraui.exception.data.EmptyDataFileContentException;
import noraui.exception.data.WrongDataFileFormatException;

public class OutputExcelDataProvider extends ExcelDataProvider {

    public OutputExcelDataProvider() {
        super();
        logger.info("Output data provider use is EXCEL");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        scenarioName = scenario;
        try {
            openOutputData();
            initColumns();
        } catch (EmptyDataFileContentException | WrongDataFileFormatException e) {
            logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION, e);
            System.exit(-1);
        }
    }
}
