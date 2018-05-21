/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.data.CommonDataProvider;
import com.github.noraui.data.DataInputProvider;
import com.github.noraui.data.DataOutputProvider;
import com.github.noraui.exception.HttpServiceException;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.EmptyDataFileContentException;
import com.github.noraui.exception.data.WebServicesException;
import com.github.noraui.service.HttpService;
import com.github.noraui.service.impl.HttpServiceImpl;
import com.github.noraui.utils.Messages;
import com.google.gson.Gson;

public class RestDataProvider extends CommonDataProvider implements DataInputProvider, DataOutputProvider {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(RestDataProvider.class);

    private static final String REST_DATA_PROVIDER_USED = "REST_DATA_PROVIDER_USED";
    private static final String REST_DATA_PROVIDER_WRITING_IN_REST_WS_ERROR_MESSAGE = "REST_DATA_PROVIDER_WRITING_IN_REST_WS_ERROR_MESSAGE";
    private static final String NORAUI_API = "/noraui/api/";
    private static final String COLUMN = "/column/";
    private static final String LINE = "/line/";

    private final String norauiWebServicesApi;

    private HttpService httpService;

    public enum types {
        JSON, XML
    }

    /**
     * Constructor of REST DataProvider.
     *
     * @param type
     *            (JSON or XML).
     * @param host
     *            is host of REST Web Services.
     * @param port
     *            is port of REST Web Services.
     * @throws WebServicesException
     *             if technical Exception occurred but for a Web Services.
     */
    public RestDataProvider(String type, String host, String port) throws WebServicesException {
        logger.info(Messages.getMessage(REST_DATA_PROVIDER_USED));
        this.norauiWebServicesApi = host + ":" + port + NORAUI_API;
        this.httpService = new HttpServiceImpl();
        if (!types.JSON.toString().equals(type) && !types.XML.toString().equals(type)) {
            throw new WebServicesException(String.format(Messages.getMessage(WebServicesException.TECHNICAL_ERROR_MESSAGE_UNKNOWN_WEB_SERVICES_TYPE), type));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        scenarioName = scenario;
        try {
            initColumns();
        } catch (final EmptyDataFileContentException e) {
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
            return Integer.parseInt(httpService.get(this.norauiWebServicesApi, scenarioName + "/nbLines")) + 1;
        } catch (TechnicalException | NumberFormatException | HttpServiceException e) {
            logger.error("getNbLines error", e);
            return 0;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readValue(String column, int line) {
        final String url = this.norauiWebServicesApi + scenarioName + COLUMN + (columns.indexOf(column) + 1) + LINE + line;
        try {
            return httpService.get(url);
        } catch (TechnicalException | NumberFormatException | HttpServiceException e) {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] readLine(int line, boolean readResult) {
        logger.debug("readLine at line {}", line);
        try {
            final String url = this.norauiWebServicesApi + scenarioName + LINE + line;
            final Row row = new Gson().fromJson(httpService.get(url), DataModel.class).getRows().get(0);
            final List<String> l = row.getColumns();
            final String[] response = l.toArray(new String[l.size() + 1]);
            response[l.size()] = String.valueOf(row.getErrorStepIndex());
            return response;
        } catch (TechnicalException | NumberFormatException | HttpServiceException e) {
            logger.error("readLine error at line {}", line, e);
            return null;
        }
    }

    private void initColumns() throws EmptyDataFileContentException {
        final String url = this.norauiWebServicesApi + scenarioName + "/columns";
        logger.debug("initColumns with this url: [{}]", url);
        try {
            columns = new Gson().fromJson(httpService.get(url), DataModel.class).getColumns();
            resultColumnName = Messages.getMessage(ResultColumnNames.RESULT_COLUMN_NAME);
            columns.add(resultColumnName);
            if (columns.size() < 2) {
                throw new EmptyDataFileContentException(Messages.getMessage(EmptyDataFileContentException.EMPTY_DATA_FILE_CONTENT_ERROR_MESSAGE));
            }
        } catch (TechnicalException | NumberFormatException | HttpServiceException e) {
            logger.error("initColumns error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeValue(String column, int line, String value) {
        logger.info("Writing: [{}] at line [{}] in column [{}]", value, line, column);
        final int colIndex = columns.indexOf(column);
        final String url = this.norauiWebServicesApi + scenarioName + COLUMN + colIndex + LINE + line;
        logger.info("url: [{}]", url);
        try {
            final DataModel dataModel = new Gson().fromJson(httpService.post(url, value), DataModel.class);
            if (resultColumnName.equals(column)) {
                if (value.equals(dataModel.getRows().get(line - 1).getResult())) {
                    logger.info(Messages.getMessage(REST_DATA_PROVIDER_WRITING_IN_REST_WS_ERROR_MESSAGE), column, line, value);
                }
            } else {
                if (value.equals(dataModel.getRows().get(line - 1).getColumns().get(colIndex - 1))) {
                    logger.info(Messages.getMessage(REST_DATA_PROVIDER_WRITING_IN_REST_WS_ERROR_MESSAGE), column, line, value);
                }
            }
        } catch (TechnicalException | NumberFormatException | HttpServiceException e) {
            logger.error("writeValue error", e);
        }
    }

    /**
     * HttpService setter for mock
     *
     * @param httpService
     *            The HTTP Service
     */
    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

}
