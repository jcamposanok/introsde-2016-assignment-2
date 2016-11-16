package introsde.rest.ehealth.util;

import java.util.StringTokenizer;


public class HerokuDatabaseUrlParser {
    // String format = "postgres://username:password@host:port/database_name";

    private String userName;
    private String password;
    private String host;
    private String port;
    private String databaseName;
    private String dbVendor;
    private String jdbcUrl;


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDbVendor() {
        return dbVendor;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }


    public HerokuDatabaseUrlParser(String herokuDatabaseUrl) {
        analyse(herokuDatabaseUrl);
    }

    private void analyse(String databaseUrl) {
        StringTokenizer st = new StringTokenizer(databaseUrl, ":@/");
        dbVendor = st.nextToken(); //if DATABASE_URL is set
        if ("postgres".equals(dbVendor)) {
            userName = st.nextToken();
            password = st.nextToken();
            host = st.nextToken();
            port = st.nextToken();
            databaseName = st.nextToken();
            jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", host, port, databaseName);
        } else {
            // Consider the URL as valid jdbc
            dbVendor = st.nextToken();
            jdbcUrl = databaseUrl;
            userName = "sa";
        }
    }

}
