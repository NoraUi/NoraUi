package com.github.noraui.data;

import java.util.List;

import com.github.noraui.data.CommonDataProvider.ResultColumnNames;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.model.Model;

public interface DataProvider {

    enum type {
        EXCEL, CSV, DB, REST, GHERKIN, CONSOLE
    }

    void prepare(String scenario) throws TechnicalException;

    int getNbGherkinExample();

    /**
     * @param modelPackagesCsv
     *            The list of available package in which to search for a valid Model (in CSV format with ; separator).
     * @return An instance of Class that can be mapped with ExcelDataProvider data.
     * @throws TechnicalException
     *             if Wrong data file (IOException, EmptyDataFileContentException or WrongDataFileFormatException)
     */
    Class<Model> getModel(String modelPackagesCsv) throws TechnicalException;

    /**
     * set IndexData in case of serial data.
     *
     * @param indexData
     *            is a array of index in case of serial data.
     */
    void setIndexData(List<DataIndex> indexData);

    /**
     * get IndexData in case of serial data.
     *
     * @param dataLine
     *            is index of line in case of serial data.
     * @return noraui.data.DataIndex
     */
    DataIndex getIndexData(int dataLine);

    /**
     * Setter of dataInPath.
     *
     * @param dataInPath
     *            the new data in path
     */
    public void setDataInPath(String dataInPath);

    /**
     * Setter of dataOutPath.
     *
     * @param dataOutPath
     *            the new data out path
     */
    public void setDataOutPath(String dataOutPath);

    /**
     * Getter of resultName column name.
     *
     * @return
     *         the name of the result column
     */
    public String getResultColumnName();

    /**
     * Returns true if the given column name is part of authorized column names. See {@link ResultColumnNames} class.
     *
     * @param name
     *            the name to check
     * @return
     *         true if the name is authorized, false otherwise
     */
    public boolean isResultColumnNameAuthorized(String name);

}
