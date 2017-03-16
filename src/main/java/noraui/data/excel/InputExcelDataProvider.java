package noraui.data.excel;

import noraui.exception.TechnicalException;
import noraui.exception.data.EmptyDataFileContentException;
import noraui.exception.data.WrongDataFileFormatException;

public class InputExcelDataProvider extends ExcelDataProvider {

    public InputExcelDataProvider() {
        super();
        logger.info("Input data provider use is EXCEL");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        scenarioName = scenario;
        try {
            openInputData();
            initColumns();
        } catch (EmptyDataFileContentException | WrongDataFileFormatException e) {
            logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION, e);
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION, e);
        }
    }

}
