package noraui.data.csv;

import static noraui.utils.Constants.DEFAULT_ENDODING;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import noraui.data.CommonDataProvider;
import noraui.data.DataInputProvider;
import noraui.data.DataOutputProvider;
import noraui.exception.TechnicalException;
import noraui.exception.data.EmptyDataFileContentException;
import noraui.exception.data.WrongDataFileFormatException;
import noraui.utils.Messages;

public class CsvDataProvider extends CommonDataProvider implements DataInputProvider, DataOutputProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CsvDataProvider.class);

    public static final String CSV_TYPE = "csv";
    public static final char CSV_CHAR_SEPARATOR = ';';
    public static final String CSV_SEPARATOR = String.valueOf(CSV_CHAR_SEPARATOR);
    private static final String CSV_DATA_PROVIDER_USED = "CSV_DATA_PROVIDER_USED";
    private static final String CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE = "CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE";

    public CsvDataProvider() {
        super();
        logger.info(Messages.getMessage(CSV_DATA_PROVIDER_USED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) {
        scenarioName = scenario;
        try {
            initColumns();
        } catch (IOException | EmptyDataFileContentException | WrongDataFileFormatException e) {
            logger.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
            System.exit(-1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbLines() {
        try {
            CSVReader reader = openInputData();
            return reader.readAll().size();
        } catch (IOException e) {
            return 0;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFailedResult(int line, String value) {
        logger.debug("WriteFailedResult => line:{} value:{}", line, value);
        writeValue(resultColumnName, line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeSuccessResult(int line) {
        logger.debug("Write Success result => line:{}", line);
        writeValue(resultColumnName, line, Messages.getMessage(Messages.SUCCESS_MESSAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeWarningResult(int line, String value) throws TechnicalException {
        logger.debug("writeWarningResult => line:{} value:{}", line, value);
        writeValue(resultColumnName, line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeDataResult(String column, int line, String value) {
        logger.debug("writeDataResult => column:{} line:{} value:{}", column, line, value);
        writeValue(column, line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readValue(String column, int line) throws TechnicalException {
        int colIndex = columns.indexOf(column);
        try {
            CSVReader reader = openInputData();
            return reader.readAll().get(line)[colIndex];
        } catch (IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] readLine(int line, boolean readResult) {
        logger.debug("readLine at line {}", line);
        try {
            CSVReader reader = openInputData();
            List<String[]> a = reader.readAll();
            if (line >= a.size()) {
                return null;
            }
            String[] row = a.get(line);
            if ("".equals(row[0])) {
                return null;
            } else {
                String[] ret = readResult ? new String[columns.size()] : new String[columns.size() - 1];
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = row[i];
                }
                return ret;
            }
        } catch (IOException e) {
            logger.error("error CsvDataProvider.readLine()", e);
            return null;
        }
    }

    private void initColumns() throws EmptyDataFileContentException, WrongDataFileFormatException, IOException {
        columns = new ArrayList<>();
        CSVReader reader = openInputData();
        String[] headers = reader.readNext();
        for (String header : headers) {
            if (!"".equals(header)) {
                columns.add(header);
            }
        }
        reader.close();
        if (columns.size() < 2) {
            throw new EmptyDataFileContentException(Messages.getMessage(EmptyDataFileContentException.EMPTY_DATA_FILE_CONTENT_ERROR_MESSAGE));
        }
        resultColumnName = columns.get(columns.size() - 1);
        if (!isResultColumnNameAuthorized(resultColumnName)) {
            throw new WrongDataFileFormatException(String.format(Messages.getMessage(WrongDataFileFormatException.WRONG_RESULT_COLUMN_NAME_ERROR_MESSAGE), AUTHORIZED_NAMES_FOR_RESULT_COLUMN));
        }
    }

    private void writeValue(String column, int line, String value) {
        logger.debug("Writing: [{}] at line [{}] in column [{}]", value, line, column);
        int colIndex = columns.indexOf(column);
        CSVReader reader;
        try {
            reader = openOutputData();
            List<String[]> csvBody = reader.readAll();
            csvBody.get(line)[colIndex] = value;
            reader.close();
            writeValue(column, line, value, csvBody);
        } catch (IOException e1) {
            logger.error(Messages.getMessage(CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE), column, line, value, e1);
        }
    }

    private void writeValue(String column, int line, String value, List<String[]> csvBody) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(new File(dataOutPath + scenarioName + "." + CSV_TYPE)), CSV_CHAR_SEPARATOR);) {
            writer.writeAll(csvBody);
            writer.flush();
        } catch (IOException e) {
            logger.error(Messages.getMessage(CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE), column, line, value, e);
        }
    }

    private CSVReader openInputData() throws FileNotFoundException, UnsupportedEncodingException {
        return new CSVReader(new InputStreamReader(new FileInputStream(dataInPath + scenarioName + "." + CSV_TYPE), DEFAULT_ENDODING), CSV_CHAR_SEPARATOR);
    }

    private CSVReader openOutputData() throws FileNotFoundException, UnsupportedEncodingException {
        return new CSVReader(new InputStreamReader(new FileInputStream(dataOutPath + scenarioName + "." + CSV_TYPE), DEFAULT_ENDODING), CSV_CHAR_SEPARATOR);
    }

}
