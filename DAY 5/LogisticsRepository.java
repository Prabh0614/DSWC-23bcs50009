import java.sql.*;

interface ReportGenerator {
    void printDelayedReport();
}

abstract class DatabaseRepository {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/cargologix";
    private static final String USER =
            "postgres";
    private static final String PASSWORD =
            "password";

    protected Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}

class LogisticsRepositoryImpl
        extends DatabaseRepository
        implements ReportGenerator {

    private static final String SQL =
            "SELECT s.shipment_id, " +
            "c.company_name, " +
            "s.dispatch_date " +
            "FROM shipments s " +
            "INNER JOIN couriers c " +
            "ON s.courier_id = c.courier_id " +
            "WHERE s.status = ? " +
            "ORDER BY s.dispatch_date DESC";

    @Override
    public void printDelayedReport() {

        try (
                Connection conn = getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL)
        ) {

            ps.setString(1, "DELAYED");

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    long shipmentId =
                            rs.getLong(
                                    "shipment_id");

                    String companyName =
                            rs.getString(
                                    "company_name");

                    Date dispatchDate =
                            rs.getDate(
                                    "dispatch_date");

                    System.out.println(
                            shipmentId + " | "
                                    + companyName
                                    + " | "
                                    + dispatchDate
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class LogisticsRepository {

    public static void main(String[] args) {

        ReportGenerator report =
                new LogisticsRepositoryImpl();

        report.printDelayedReport();
    }
}