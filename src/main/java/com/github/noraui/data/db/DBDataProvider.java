/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.data.CommonDataProvider;
import com.github.noraui.data.DataInputProvider;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.exception.data.DatabaseException;
import com.github.noraui.utils.Constants;
import com.github.noraui.utils.Messages;

public class DBDataProvider extends CommonDataProvider implements DataInputProvider {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DBDataProvider.class);

    private static final String DB_DATA_PROVIDER_USED = "DB_DATA_PROVIDER_USED";
    private static final String DATABASE_ERROR_FORBIDDEN_WORDS_IN_QUERY = "DATABASE_ERROR_FORBIDDEN_WORDS_IN_QUERY";
    private String connectionUrl;
    private final String user;
    private final String password;

    private enum types {
        MYSQL, ORACLE, POSTGRE
    }

    public DBDataProvider(String type, String user, String password, String hostname, String port, String database) throws TechnicalException {
        super();
        this.user = user;
        this.password = password;
        try {
            if (types.MYSQL.toString().equals(type)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.connectionUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";
            } else if (types.ORACLE.toString().equals(type)) {
                Class.forName("oracle.jdbc.OracleDriver");
                this.connectionUrl = "jdbc:oracle:thin:@" + hostname + ":" + port + ":" + database;
            } else if (types.POSTGRE.toString().equals(type)) {
                this.connectionUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + database;
            } else {
                throw new DatabaseException(String.format(Messages.getMessage(DatabaseException.TECHNICAL_ERROR_MESSAGE_UNKNOWN_DATABASE_TYPE), type));
            }
        } catch (final Exception e) {
            LOGGER.error(Messages.getMessage(DatabaseException.TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION), e);
            throw new TechnicalException(Messages.getMessage(DatabaseException.TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION), e);
        }
        LOGGER.info(Messages.getMessage(DB_DATA_PROVIDER_USED), type);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.connectionUrl, this.user, this.password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        scenarioName = scenario;
        try {
            initColumns();
        } catch (final DatabaseException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws TechnicalException
     *             is thrown if you have a technical error (IOException on .sql file) in NoraUi.
     */
    @Override
    public int getNbLines() throws TechnicalException {
        String sqlRequest = "";
        try {
            final Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Constants.DEFAULT_ENDODING);
            sqlSanitized4readOnly(sqlRequest);
        } catch (final IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), e);
        }
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery(sqlRequest);) {
            return rs.last() ? rs.getRow() + 1 : 0;
        } catch (final SQLException e) {
            LOGGER.error("error DBDataProvider.getNbLines()", e);
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws TechnicalException
     *             is thrown if you have a technical error (IOException on .sql file) in NoraUi.
     */
    @Override
    public String readValue(String column, int line) throws TechnicalException {
        LOGGER.debug("readValue: column:[{}] and line:[{}] ", column, line);
        String sqlRequest;
        try {
            final Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Constants.DEFAULT_ENDODING);
            sqlSanitized4readOnly(sqlRequest);
        } catch (final IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), e);
        }
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sqlRequest); ResultSet rs = statement.executeQuery();) {
            if (line < 1) {
                return column;
            }
            while (rs.next() && rs.getRow() < line) {
            }
            LOGGER.debug("column: {}", column);
            return rs.getString(column);
        } catch (final SQLException e) {
            LOGGER.error("error DBDataProvider.readValue({}, {})", column, line, e);
            return "";
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws TechnicalException
     *             is thrown if you have a technical error (IOException on .sql file) in NoraUi.
     */
    @Override
    public String[] readLine(int line, boolean readResult) throws TechnicalException {
        String sqlRequest;
        try {
            final Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Constants.DEFAULT_ENDODING);
            sqlSanitized4readOnly(sqlRequest);
        } catch (final IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), e);
        }
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sqlRequest); ResultSet rs = statement.executeQuery();) {
            LOGGER.info("rs {}", rs);
            if (rs == null || "".equals(rs.getString(0))) {
                return null;
            } else {
                final String[] ret = readResult ? new String[columns.size()] : new String[columns.size() - 1];
                if (line == 0) {
                    for (int i = 0; i < ret.length; i++) {
                        ret[i] = columns.get(i);
                    }
                } else {
                    while (rs.next() && rs.getRow() < line) {
                    }
                    for (int i = 1; i <= ret.length; i++) {
                        ret[i - 1] = rs.getString(i);
                    }
                }
                return ret;
            }
        } catch (final SQLException e) {
            LOGGER.debug("In DBDataProvider, this catch aims for testing the end of provided data. DBDataProvider.readLine({}, {})", line, readResult, e);
            return null;
        }
    }

    private void initColumns() throws DatabaseException, TechnicalException {
        columns = new ArrayList<>();
        String sqlRequest;
        try {
            final Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Constants.DEFAULT_ENDODING);
            sqlSanitized4readOnly(sqlRequest);
        } catch (final IOException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), e);
        }
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sqlRequest); ResultSet rs = statement.executeQuery();) {
            if (rs.getMetaData().getColumnCount() < 1) {
                throw new DatabaseException("Input data is empty. No column have been found.");
            }
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columns.add(rs.getMetaData().getColumnLabel(i));
            }
        } catch (final SQLException e) {
            throw new TechnicalException(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), e);
        }
        resultColumnName = Messages.getMessage(ResultColumnNames.RESULT_COLUMN_NAME);
    }

    protected static void sqlSanitized4readOnly(String sqlInput) throws TechnicalException {
        final String[] forbiddenWords = { "DROP", "DELETE", "TRUNCATE", "UPDATE" };
        for (final String forbiddenWord : forbiddenWords) {
            if (sqlInput.toUpperCase().contains(forbiddenWord)) {
                throw new TechnicalException(Messages.format(Messages.getMessage(DATABASE_ERROR_FORBIDDEN_WORDS_IN_QUERY), sqlInput));
            }
        }
    }

}
