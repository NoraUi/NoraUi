/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data;

import com.github.noraui.exception.TechnicalException;

public interface DataInputProvider extends DataProvider {

    /**
     * Gets the number of readable lines of current opened file.
     * If a line does not contain 'ID' column of if it is empty, then the line is not taken into account.
     *
     * @return number of opened input file
     * @throws TechnicalException
     *             if Wrong data file (IOException, EmptyDataFileContentException or WrongDataFileFormatException)
     */
    int getNbLines() throws TechnicalException;

    /**
     * Reads a cell of opened input file.
     *
     * @param column
     *            index of the column to read
     * @param line
     *            index of the line to read
     * @return a String with the value of cell
     * @throws TechnicalException
     *             if Wrong data file (IOException, EmptyDataFileContentException or WrongDataFileFormatException)
     */
    String readValue(String column, int line) throws TechnicalException;

    /**
     * @param line
     *            id of the line to read (line=0 for reading headers ; line=1 for reading 1st line, ...)
     * @param readResult
     *            true if result must be returned, false otherwise
     * @return all content of read line split by cell in a String array
     * @throws TechnicalException
     *             if Wrong data file (IOException, EmptyDataFileContentException or WrongDataFileFormatException)
     */
    String[] readLine(int line, boolean readResult) throws TechnicalException;

}
