package noraui.data.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import noraui.data.CommonDataProvider;
import noraui.data.DataInputProvider;
import noraui.data.DataOutputProvider;
import noraui.data.DataProvider;
import noraui.exception.TechnicalException;
import noraui.exception.data.EmptyDataFileContentException;
import noraui.exception.data.WebServicesException;
import noraui.utils.Messages;

public class RestDataProvider extends CommonDataProvider implements DataInputProvider, DataOutputProvider {

    private static final String NORAUI_API = "/noraui/api/";

    private String norauiWebServicesApi;

    private RestTemplate restTemplate;

    private enum types {
        JSON, XML
    }

    public RestDataProvider(String type, String host, String port) throws WebServicesException {
        logger.info("Data provider used is REST");
        this.norauiWebServicesApi = host + ":" + port + NORAUI_API;
        if (!types.JSON.toString().equals(type) && !types.XML.toString().equals(type)) {
            throw new WebServicesException(String.format(WebServicesException.TECHNICAL_ERROR_MESSAGE_UNKNOWN_WEB_SERVICES_TYPE, type));
        }
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        scenarioName = scenario;
        try {
            initColumns();
        } catch (EmptyDataFileContentException e) {
            logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION, e);
            System.exit(-1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbLines() {
        final String uri = this.norauiWebServicesApi + scenarioName + "/nbLines";
        return restTemplate.getForObject(uri, Integer.class) + 1;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFailedResult(int line, String value) {
        logger.debug(String.format("Write Failed result => line:%d value:%s", line, value));
        writeValue(resultColumnName, line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeSuccessResult(int line) {
        logger.debug(String.format("Write Success result => line:%d", line));
        writeValue(resultColumnName, line, Messages.SUCCESS_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeWarningResult(int line, String value) throws TechnicalException {
        logger.debug(String.format("Write Warning result => line:%d value:%s", line, value));
        writeValue(resultColumnName, line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeDataResult(String column, int line, String value) {
        logger.debug(String.format("Write Data result => column:%s line:%d value:%s", column, line, value));
        writeValue(column, line, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readValue(String column, int line) {
        final String uri = this.norauiWebServicesApi + scenarioName + "/column/" + (columns.indexOf(column) + 1) + "/line/" + line;
        ResponseEntity<String> entity = restTemplate.getForEntity(uri, String.class);
        if (HttpStatus.NO_CONTENT.equals(entity.getStatusCode())) {
            return "";
        }
        return entity.getBody();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] readLine(int line, boolean readResult) {
        logger.debug("readLine at line " + line);
        final String uri = this.norauiWebServicesApi + scenarioName + "/line/" + line;
        Row row = restTemplate.getForObject(uri, DataModel.class).getRows().get(0);
        List<String> l = row.getColumns();
        String[] response = l.toArray(new String[l.size() + 1]);
        response[l.size()] = String.valueOf(row.getErrorStepIndex());
        return response;
    }

    private void initColumns() throws EmptyDataFileContentException {
        final String uri = this.norauiWebServicesApi + scenarioName + "/columns";
        columns = restTemplate.getForObject(uri, DataModel.class).getColumns();
        resultColumnName = DataProvider.AUTHORIZED_NAMES_FOR_RESULT_COLUMN.get(0);
        columns.add(resultColumnName);
        if (columns.size() < 2) {
            throw new EmptyDataFileContentException("Input data file is empty or only result column is provided.");
        }
    }

    private void writeValue(String column, int line, String value) {
        logger.debug("Writing: " + value + " at line " + line + " in column '" + column + "'");
        int colIndex = columns.indexOf(column);
        final String uri = this.norauiWebServicesApi + scenarioName + "/column/" + colIndex + "/line/" + line;
        DataModel dataModel = restTemplate.patchForObject(uri, value, DataModel.class);
        if (resultColumnName.equals(column)) {
            if (value.equals(dataModel.getRows().get(line - 1).getResult())) {
                logger.error("writeValue in REST Web services => column: " + column + " line:" + line + " value:" + value);
            }
        } else {
            if (value.equals(dataModel.getRows().get(line - 1).getColumns().get(colIndex - 1))) {
                logger.error("writeValue in REST Web services => column: " + column + " line:" + line + " value:" + value);
            }
        }
    }

}
