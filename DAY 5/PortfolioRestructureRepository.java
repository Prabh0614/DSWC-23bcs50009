import java.sql.*;

interface PortfolioManager {
    void restructurePortfolio(long investorId);
}

abstract class FinancialDatabaseConfig {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/firedb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

class PortfolioRepositoryImpl extends FinancialDatabaseConfig
        implements PortfolioManager {

    @Override
    public void restructurePortfolio(long investorId) {

        String aggregationQuery =
                "SELECT h.asset_class, SUM(h.current_value) AS total_value " +
                "FROM investors i " +
                "INNER JOIN holdings h ON i.investor_id = h.investor_id " +
                "WHERE i.investor_id = ? " +
                "GROUP BY h.asset_class";

        String reduceDebt =
                "UPDATE holdings SET current_value = current_value - ? " +
                "WHERE investor_id = ? AND asset_class = 'Debt'";

        String increaseEquity =
                "UPDATE holdings SET current_value = current_value + ? " +
                "WHERE investor_id = ? AND asset_class = 'Equity'";

        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps =
                    conn.prepareStatement(aggregationQuery);

            ps.setLong(1, investorId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println(
                        rs.getString("asset_class")
                                + " : "
                                + rs.getDouble("total_value"));
            }

            PreparedStatement debtStmt =
                    conn.prepareStatement(reduceDebt);

            debtStmt.setDouble(1, 5000.0);
            debtStmt.setLong(2, investorId);
            debtStmt.executeUpdate();

            PreparedStatement equityStmt =
                    conn.prepareStatement(increaseEquity);

            equityStmt.setDouble(1, 5000.0);
            equityStmt.setLong(2, investorId);
            equityStmt.executeUpdate();

            conn.commit();

            rs.close();
            ps.close();
            debtStmt.close();
            equityStmt.close();

        } catch (Exception e) {

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

        } finally {

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

public class PortfolioRestructureRepository {

    public static void main(String[] args) {

        PortfolioManager manager =
                new PortfolioRepositoryImpl();

        manager.restructurePortfolio(101L);
    }
}