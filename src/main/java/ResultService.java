import java.sql.*;

public class ResultService {

    private static final String DB_NAME = "calc_data";

    private static String getEnv(String key, String defaultVal) {
        String val = System.getenv(key);
        return (val == null || val.isEmpty()) ? defaultVal : val;
    }

    // Load MariaDB driver
    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getDatabaseUrl() {
        String host = getEnv("DB_HOST", "localhost");
        String port = getEnv("DB_PORT", "3307");
        return "jdbc:mariadb://" + host + ":" + port + "/" + DB_NAME +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String getDbUser() {
        return getEnv("DB_USER", "root");
    }

    private static String getDbPassword() {
        return getEnv("DB_PASSWORD", "Test12");
    }

    private static void ensureTablesExist(Statement stmt) throws SQLException {
        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS calc_results (
                id INT AUTO_INCREMENT PRIMARY KEY,
                number1 DOUBLE NOT NULL,
                number2 DOUBLE NOT NULL,
                sum_result DOUBLE NOT NULL,
                product_result DOUBLE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS subtraction_results (
                id INT AUTO_INCREMENT PRIMARY KEY,
                number1 DOUBLE NOT NULL,
                number2 DOUBLE NOT NULL,
                difference_result DOUBLE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS division_results (
                id INT AUTO_INCREMENT PRIMARY KEY,
                number1 DOUBLE NOT NULL,
                number2 DOUBLE NOT NULL,
                quotient_result DOUBLE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    public static void saveResult(double n1, double n2, double sum, double product) {
        String dbUrl = getDatabaseUrl();
        try (Connection conn = DriverManager.getConnection(dbUrl, getDbUser(), getDbPassword());
             Statement stmt = conn.createStatement()) {

            ensureTablesExist(stmt);

            String insert = "INSERT INTO calc_results (number1, number2, sum_result, product_result) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setDouble(1, n1);
                ps.setDouble(2, n2);
                ps.setDouble(3, sum);
                ps.setDouble(4, product);
                ps.executeUpdate();
            }
            System.out.println("✅ Sum/Product saved: " + n1 + ", " + n2 + " → Sum=" + sum + ", Product=" + product);

        } catch (SQLException e) {
            System.err.println("❌ Failed to save result to DB: " + dbUrl);
            e.printStackTrace();
        }
    }

    public static void saveSubtraction(double n1, double n2, double difference) {
        String dbUrl = getDatabaseUrl();
        try (Connection conn = DriverManager.getConnection(dbUrl, getDbUser(), getDbPassword());
             Statement stmt = conn.createStatement()) {

            ensureTablesExist(stmt);

            String insert = "INSERT INTO subtraction_results (number1, number2, difference_result) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setDouble(1, n1);
                ps.setDouble(2, n2);
                ps.setDouble(3, difference);
                ps.executeUpdate();
            }
            System.out.println("✅ Subtraction saved: " + n1 + " - " + n2 + " = " + difference);

        } catch (SQLException e) {
            System.err.println("❌ Failed to save subtraction to DB: " + dbUrl);
            e.printStackTrace();
        }
    }

    public static void saveDivision(double n1, double n2, double quotient) {
        String dbUrl = getDatabaseUrl();
        try (Connection conn = DriverManager.getConnection(dbUrl, getDbUser(), getDbPassword());
             Statement stmt = conn.createStatement()) {

            ensureTablesExist(stmt);

            String insert = "INSERT INTO division_results (number1, number2, quotient_result) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setDouble(1, n1);
                ps.setDouble(2, n2);
                ps.setDouble(3, quotient);
                ps.executeUpdate();
            }
            System.out.println("✅ Division saved: " + n1 + " / " + n2 + " = " + quotient);

        } catch (SQLException e) {
            System.err.println("❌ Failed to save division to DB: " + dbUrl);
            e.printStackTrace();
        }
    }
}