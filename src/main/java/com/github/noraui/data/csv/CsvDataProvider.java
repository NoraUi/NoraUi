/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.csv;

import static com.github.noraui.utils.Constants.DEFAULT_ENDODING;

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

import com.github.noraui.data.CommonDataProvider;
import com.github.noraui.data.DataInputProvider;
import com.github.noraui.data.DataOutputProvider;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.EmptyDataFileContentException;
import com.github.noraui.exception.data.WrongDataFileFormatException;
import com.github.noraui.utils.Messages;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class CsvDataProvider extends CommonDataProvider implements DataInputProvider, DataOutputProvider {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvDataProvider.class);

    public static final String CSV_TYPE = "csv";
    public static final char CSV_CHAR_SEPARATOR = ';';
    public static final char CSV_CHAR_QUOTE = Character.MIN_VALUE;
    public static final char CSV_CHAR_ESCAPE = '\\';
    public static final String CSV_CHAR_LINEEND = "\n";
    public static final String CSV_SEPARATOR = String.valueOf(CSV_CHAR_SEPARATOR);
    private static final String CSV_DATA_PROVIDER_USED = "CSV_DATA_PROVIDER_USED";
    private static final String CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE = "CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE";

    public CsvDataProvider() {
        super();
        LOGGER.info(Messages.getMessage(CSV_DATA_PROVIDER_USED));
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
            LOGGER.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
            System.exit(-1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbLines() {
        try {
            final CSVReader reader = openInputData();
            return reader.readAll().size();
        } catch (final IOException e) {
            return 0;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readValue(String column, int line) throws TechnicalException {
        final int colIndex = columns.indexOf(column);
        try {
            final CSVReader reader = openInputData();
            return reader.readAll().get(line)[colIndex];
        } catch (final IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] readLine(int line, boolean readResult) {
        LOGGER.debug("readLine at line {}", line);
        try {
            final CSVReader reader = openInputData();
            final List<String[]> a = reader.readAll();
            if (line >= a.size()) {
                return null;
            }
            final String[] row = a.get(line);
            if ("".equals(row[0])) {
                return null;
            } else {
                final String[] ret = readResult ? new String[columns.size()] : new String[columns.size() - 1];
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = row[i];
                }
                return ret;
            }
        } catch (final IOException e) {
            LOGGER.error("error CsvDataProvider.readLine()", e);
            return null;
        }
    }

    private void initColumns() throws EmptyDataFileContentException, WrongDataFileFormatException, IOException {
        columns = new ArrayList<>();
        final CSVReader reader = openInputData();
        final String[] headers = reader.readNext();
        for (final String header : headers) {
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
            throw new WrongDataFileFormatException(String.format(Messages.getMessage(WrongDataFileFormatException.WRONG_RESULT_COLUMN_NAME_ERROR_MESSAGE), ResultColumnNames.getAuthorizedNames()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeValue(String column, int line, String value) {
        LOGGER.debug("Writing: [{}] at line [{}] in column [{}]", value, line, column);
        final int colIndex = columns.indexOf(column);
        CSVReader reader;
        try {
            reader = openOutputData();
            final List<String[]> csvBody = reader.readAll();
            csvBody.get(line)[colIndex] = value;
            reader.close();
            writeValue(column, line, value, csvBody);
        } catch (final IOException e1) {
            LOGGER.error(Messages.getMessage(CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE), column, line, value, e1);
        }
    }

    private void writeValue(String column, int line, String value, List<String[]> csvBody) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(new File(dataOutPath + scenarioName + "." + CSV_TYPE)), CSV_CHAR_SEPARATOR, CSV_CHAR_QUOTE, CSV_CHAR_ESCAPE, CSV_CHAR_LINEEND);) {
            writer.writeAll(csvBody);
            writer.flush();
        } catch (final IOException e) {
            LOGGER.error(Messages.getMessage(CSV_DATA_PROVIDER_WRITING_IN_CSV_ERROR_MESSAGE), column, line, value, e);
        }
    }

    private CSVReader openInputData() throws FileNotFoundException, UnsupportedEncodingException {
        return new CSVReaderBuilder(new InputStreamReader(new FileInputStream(dataInPath + scenarioName + "." + CSV_TYPE), DEFAULT_ENDODING))
                .withCSVParser(new CSVParserBuilder().withSeparator(CSV_CHAR_SEPARATOR).build()).build();
    }

    private CSVReader openOutputData() throws FileNotFoundException, UnsupportedEncodingException {
        return new CSVReaderBuilder(new InputStreamReader(new FileInputStream(dataOutPath + scenarioName + "." + CSV_TYPE), DEFAULT_ENDODING))
                .withCSVParser(new CSVParserBuilder().withSeparator(CSV_CHAR_SEPARATOR).build()).build();
    }

}
