package com.github.noraui.data.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.EmptyDataFileContentException;
import com.github.noraui.exception.data.WrongDataFileFormatException;
import com.github.noraui.utils.Messages;

public class OutputExcelDataProvider extends ExcelDataProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(OutputExcelDataProvider.class);

    private static final String EXCEL_OUTPUT_DATA_PROVIDER_USED = "EXCEL_OUTPUT_DATA_PROVIDER_USED";

    public OutputExcelDataProvider() {
        super();
        logger.info(Messages.getMessage(EXCEL_OUTPUT_DATA_PROVIDER_USED));
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
            logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
            System.exit(-1);
        }
    }
}
