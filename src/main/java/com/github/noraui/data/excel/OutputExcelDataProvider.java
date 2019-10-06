/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.excel;

import org.slf4j.Logger;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.EmptyDataFileContentException;
import com.github.noraui.exception.data.WrongDataFileFormatException;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Messages;

@Loggable
public class OutputExcelDataProvider extends ExcelDataProvider {

    static Logger log;

    private static final String EXCEL_OUTPUT_DATA_PROVIDER_USED = "EXCEL_OUTPUT_DATA_PROVIDER_USED";

    public OutputExcelDataProvider() {
        super();
        log.info(Messages.getMessage(EXCEL_OUTPUT_DATA_PROVIDER_USED));
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
            log.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
            System.exit(-1);
        }
    }
}
