package noraui.data.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import noraui.data.CommonDataProvider;
import noraui.data.DataInputProvider;
import noraui.data.DataOutputProvider;
import noraui.exception.TechnicalException;
import noraui.exception.data.EmptyDataFileContentException;
import noraui.exception.data.WrongDataFileFormatException;
import noraui.utils.Constants;
import noraui.utils.Messages;

public abstract class ExcelDataProvider extends CommonDataProvider implements DataInputProvider, DataOutputProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ExcelDataProvider.class);

    private static final String EXCEL_DATA_PROVIDER_WRONG_CELL_TYPE_ERROR_MESSAGE = "EXCEL_DATA_PROVIDER_WRONG_CELL_TYPE_ERROR_MESSAGE";
    private static final String EXCEL_DATA_PROVIDER_SAVE_FILE_ERROR_MESSAGE = "EXCEL_DATA_PROVIDER_SAVE_FILE_ERROR_MESSAGE";
    private Workbook workbook;
    private String dataOutExtension;

    private CellStyle styleSuccess;
    private CellStyle styleFailed;
    private CellStyle styleWarning;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbLines() throws TechnicalException {
        int count = 0;
        final Iterator<Row> rowsIterator = workbook.getSheetAt(0).iterator();
        Row row;
        while (rowsIterator.hasNext()) {
            row = rowsIterator.next();
            if (!"".equals(readCell(row.getCell(0)))) {
                count++;
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFailedResult(int line, String value) throws TechnicalException {
        logger.debug("writeFailedResult => line:{} value:{}", line, value);
        writeValue(resultColumnName, line, value, styleFailed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeSuccessResult(int line) throws TechnicalException {
        logger.debug("writeSuccessResult => line:{}", line);
        writeValue(resultColumnName, line, Messages.getMessage(Messages.SUCCESS_MESSAGE), styleSuccess);
    }

    @Override
    public void writeWarningResult(int line, String value) throws TechnicalException {
        logger.debug("writeWarningResult => line:{}", line);
        writeValue(resultColumnName, line, value, styleWarning);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeDataResult(String column, int line, String value) throws TechnicalException {
        logger.debug("writeDataResult => column:{} line:{} value:{}", column, line, value);
        writeValue(column, line, value, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readValue(String column, int line) throws TechnicalException {
        final int colIndex = columns.indexOf(column);
        final Sheet sheet = workbook.getSheetAt(0);
        final Row row = sheet.getRow(line);
        final Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return "";
        } else {
            return readCell(cell);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] readLine(int line, boolean readResult) throws TechnicalException {
        final Sheet sheet = workbook.getSheetAt(0);
        final Row row = sheet.getRow(line);
        if (row == null || "".equals(readCell(row.getCell(0)))) {
            return null;
        } else {
            final String[] ret = readResult ? new String[columns.size()] : new String[columns.size() - 1];
            Cell cell;
            for (int i = 0; i < ret.length; i++) {
                if ((cell = row.getCell(i)) == null) {
                    ret[i] = "";
                } else {
                    ret[i] = readCell(cell);
                }
            }
            return ret;
        }
    }

    /**
     * @throws EmptyDataFileContentException
     *             if data is empty
     * @throws WrongDataFileFormatException
     *             if data is wrong
     */
    protected void initColumns() throws EmptyDataFileContentException, WrongDataFileFormatException {
        columns = new ArrayList<>();
        final Sheet sheet = workbook.getSheetAt(0);
        final Row row = sheet.getRow(0);
        Cell cell;
        for (int i = 0; (cell = row.getCell(i)) != null; i++) {
            columns.add(cell.getStringCellValue());
        }
        if (columns.size() < 2) {
            throw new EmptyDataFileContentException(Messages.getMessage(EmptyDataFileContentException.EMPTY_DATA_FILE_CONTENT_ERROR_MESSAGE));
        }
        resultColumnName = columns.get(columns.size() - 1);
        if (!isResultColumnNameAuthorized(resultColumnName)) {
            throw new WrongDataFileFormatException(String.format(Messages.getMessage(WrongDataFileFormatException.WRONG_RESULT_COLUMN_NAME_ERROR_MESSAGE), AUTHORIZED_NAMES_FOR_RESULT_COLUMN));
        }
    }

    /**
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    protected void openInputData() throws TechnicalException {
        final String dataInExtension = validExtension(dataInPath);
        try (FileInputStream fileIn = new FileInputStream(dataInPath + scenarioName + "." + dataInExtension);) {
            initWorkbook(fileIn, dataInExtension);
        } catch (final IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
        }
    }

    /**
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    void openOutputData() throws TechnicalException {
        this.dataOutExtension = validExtension(dataOutPath);
        try (FileInputStream fileOut = new FileInputStream(dataOutPath + scenarioName + "." + dataOutExtension);) {
            initWorkbook(fileOut, dataOutExtension);
        } catch (final IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
        }

        styleSuccess = workbook.createCellStyle();
        final Font fontSuccess = workbook.createFont();
        fontSuccess.setColor(HSSFColor.GREEN.index);
        styleSuccess.setFont(fontSuccess);

        styleFailed = workbook.createCellStyle();
        final Font fontFailed = workbook.createFont();
        fontFailed.setColor(HSSFColor.RED.index);
        styleFailed.setFont(fontFailed);

        styleWarning = workbook.createCellStyle();
        final Font fontWarning = workbook.createFont();
        fontWarning.setColor(HSSFColor.ORANGE.index);
        styleWarning.setFont(fontWarning);
    }

    private void initWorkbook(FileInputStream stream, String extension) throws IOException {
        switch (extension) {
            case "xlsx":
                workbook = new XSSFWorkbook(stream);
                break;
            case "xlsm":
                workbook = new XSSFWorkbook(stream);
                break;
            case "xls":
                workbook = new HSSFWorkbook(stream);
                break;
            default:
                throw new IOException();
        }
    }

    /**
     * Valid Excel extension: xls, xlsx, or xlsm.
     *
     * @param dataPath
     *            path of all files from input/output folder
     * @return unique extension if right
     * @throws TechnicalException
     *             If you are using Excel as a dataProvider, you must choose one of the following formats: xls, xlsx, or xlsm.
     */
    private String validExtension(String dataPath) throws TechnicalException {
        final Set<String> extensions = new HashSet<>();
        for (final File file : new File(dataPath).listFiles()) {
            if (FilenameUtils.getBaseName(file.getName()).equals(scenarioName) && FilenameUtils.getExtension(file.getName()).startsWith("xls")) {
                extensions.add(FilenameUtils.getExtension(file.getName()));
            }
        }
        if (extensions.size() != 1) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_EXPECTED_EXCEL_EXTENTION_ERROR));
        }
        return extensions.iterator().next();
    }

    /**
     * @param column
     * @param line
     * @param value
     * @param style
     */
    private void writeValue(String column, int line, String value, CellStyle style) {
        logger.debug("Writing: [{}] at line [{}] in column [{}]", value, line, column);
        final int colIndex = columns.indexOf(column);
        final Sheet sheet = workbook.getSheetAt(0);
        final Row row = sheet.getRow(line);
        Cell cell = row.getCell(colIndex);
        if (cell != null) {
            row.removeCell(cell);
        }
        cell = row.createCell(colIndex);
        cell.setCellStyle(style);
        cell.setCellValue(value);
        saveOpenExcelFile();
    }

    /**
     * @param cell
     * @return
     */
    private String readCell(Cell cell) {
        String txt = "";
        if (cell != null) {
            logger.debug("readCellByType with type: {}" + cell.getCellType());
            txt = readCellByType(cell, cell.getCellType());
        }
        return txt.trim();
    }

    /**
     * readCellByType is used because CELL_TYPE_FORMULA can be CELL_TYPE_NUMERIC, CELL_TYPE_NUMERIC(date) or CELL_TYPE_STRING.
     *
     * @param cellcell
     *            read (line and column)
     * @param type
     *            CELL_TYPE_FORMULA can be CELL_TYPE_NUMERIC, CELL_TYPE_NUMERIC(date) or CELL_TYPE_STRING
     * @return a string with the data (evalued if CELL_TYPE_FORMULA)
     */
    private String readCellByType(Cell cell, int type) {
        String txt = "";
        if (cell != null) {
            switch (type) {
                case Cell.CELL_TYPE_NUMERIC:
                    txt = dateOrNumberProcessing(cell);
                    break;
                case Cell.CELL_TYPE_STRING:
                    txt = String.valueOf(cell.getRichStringCellValue());
                    logger.debug("CELL_TYPE_STRING: {}", txt);
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    txt = readCellByType(cell, cell.getCachedFormulaResultType());
                    logger.debug("CELL_TYPE_FORMULA: {}", txt);
                    break;
                case Cell.CELL_TYPE_BLANK:
                    logger.debug("CELL_TYPE_BLANK (we do nothing)");
                    break;
                default:
                    logger.error(EXCEL_DATA_PROVIDER_WRONG_CELL_TYPE_ERROR_MESSAGE, type);
                    break;
            }
        }
        return txt;
    }

    /**
     * @param cell
     * @return
     */
    private String dateOrNumberProcessing(Cell cell) {
        String txt;
        if (DateUtil.isCellDateFormatted(cell)) {
            final DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
            txt = String.valueOf(formatter.format(cell.getDateCellValue()));
            logger.debug("CELL_TYPE_NUMERIC (date): {}", txt);
        } else {
            txt = String.valueOf(cell.getNumericCellValue());
            logger.debug("CELL_TYPE_NUMERIC: {}", txt);
        }
        return txt;
    }

    private void saveOpenExcelFile() {
        try (FileOutputStream fileOut = new FileOutputStream(dataOutPath + scenarioName + "." + this.dataOutExtension);) {
            workbook.write(fileOut);
        } catch (final IOException e) {
            logger.error(Messages.getMessage(EXCEL_DATA_PROVIDER_SAVE_FILE_ERROR_MESSAGE), e);
        }
    }

}
