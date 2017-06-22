package noraui.data.db;

import java.io.IOException;
import java.nio.charset.Charset;
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

import noraui.data.CommonDataProvider;
import noraui.data.DataInputProvider;
import noraui.exception.TechnicalException;
import noraui.exception.data.DatabaseException;
import noraui.utils.Constants;

public class DBDataProvider extends CommonDataProvider implements DataInputProvider {

    private String connectionUrl;
    private String user;
    private String password;
    private String hostname;
    private String port;
    private String database;

    private enum types {
        MYSQL, ORACLE, POSTGRE
    }

    public DBDataProvider(String type, String user, String password, String hostname, String port, String database) throws TechnicalException {
        super();
        this.user = user;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        try {
            if (types.MYSQL.toString().equals(type)) {
                Class.forName("com.mysql.jdbc.Driver");
                connectionUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
            } else if (types.ORACLE.toString().equals(type)) {
                Class.forName("oracle.jdbc.OracleDriver");
                connectionUrl = "jdbc:oracle:thin:@" + hostname + ":" + port + ":" + database;
            } else if (types.POSTGRE.toString().equals(type)) {
                Class.forName("org.postgresql.Driver");
                connectionUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + database;
            } else {
                throw new DatabaseException(String.format(DatabaseException.TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION, type));
            }
        } catch (Exception e) {
            logger.error(DatabaseException.TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION, e);
            throw new TechnicalException(DatabaseException.TECHNICAL_ERROR_MESSAGE_DATABASE_EXCEPTION, e);
        }
        logger.info("dataProvider used is DB (" + type + ")");
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl, user, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(String scenario) throws TechnicalException {
        scenarioName = scenario;
        try {
            initColumns();
        } catch (DatabaseException e) {
            logger.error(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE_DATA_IOEXCEPTION, e);
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
            Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Charset.forName(Constants.DEFAULT_ENDODING));
            sqlSanitized4readOnly(sqlRequest);
        } catch (IOException e) {
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
        }
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery(sqlRequest);) {
            return rs.last() ? rs.getRow() + 1 : 0;
        } catch (SQLException e) {
            logger.error("getNbLines()" + e.getMessage(), e);
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
        String sqlRequest;
        try {
            Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Charset.forName(Constants.DEFAULT_ENDODING));
            sqlSanitized4readOnly(sqlRequest);
        } catch (IOException e) {
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
        }
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sqlRequest); ResultSet rs = statement.executeQuery();) {
            if (line < 1) {
                return column;
            }
            while (rs.next() && rs.getRow() < line) {
            }
            return rs.getString(column);
        } catch (SQLException e) {
            logger.error("readValue(" + column + ", " + line + ")" + e.getMessage(), e);
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
            Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Charset.forName(Constants.DEFAULT_ENDODING));
            sqlSanitized4readOnly(sqlRequest);
        } catch (IOException e) {
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
        }
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sqlRequest); ResultSet rs = statement.executeQuery();) {
            String[] ret = readResult ? new String[columns.size()] : new String[columns.size() - 1];
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
        } catch (SQLException e) {
            logger.debug("In DBDataProvider, is it a catch used for tested end of data. readLine(" + line + ", " + readResult + ")" + e.getMessage(), e);
            return null;
        }
    }

    private void initColumns() throws DatabaseException, TechnicalException {
        columns = new ArrayList<>();
        String sqlRequest;
        try {
            Path file = Paths.get(dataInPath + scenarioName + ".sql");
            sqlRequest = new String(Files.readAllBytes(file), Charset.forName(Constants.DEFAULT_ENDODING));
            sqlSanitized4readOnly(sqlRequest);
        } catch (IOException e) {
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
        }
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sqlRequest); ResultSet rs = statement.executeQuery();) {
            if (rs.getMetaData().getColumnCount() < 1) {
                throw new DatabaseException("Input data is empty. No column have been found.");
            }
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columns.add(rs.getMetaData().getColumnLabel(i));
            }
        } catch (SQLException e) {
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + e.getMessage(), e);
        }
    }

    protected static void sqlSanitized4readOnly(String sqlInput) throws TechnicalException {
        String[] forbiddenWords = { "DROP", "DELETE", "TRUNCATE", "UPDATE" };
        for (String forbiddenWord : forbiddenWords) {
            if (sqlInput.toUpperCase().contains(forbiddenWord)) {
                throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + "Your sql file contains a forbidden word for queries. (Read only autorized): " + sqlInput);
            }
        }
    }

}
