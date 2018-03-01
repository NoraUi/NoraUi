/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.EmptyDataFileContentException;
import com.github.noraui.exception.data.WrongDataFileFormatException;
import com.github.noraui.utils.Messages;

public class InputExcelDataProvider extends ExcelDataProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(InputExcelDataProvider.class);

    private static final String EXCEL_INPUT_DATA_PROVIDER_USED = "EXCEL_INPUT_DATA_PROVIDER_USED";

    public InputExcelDataProvider() {
        super();
        logger.info(Messages.getMessage(EXCEL_INPUT_DATA_PROVIDER_USED));
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
            logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
            System.exit(-1);
        }
    }

}
