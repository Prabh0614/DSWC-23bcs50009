import java.sql.*;

interface QueueWorker {
    void processNextJob();
}

abstract class EnterpriseConnectionFactory {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/enterprise_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}

class EnterpriseJobQueueWorker
        extends EnterpriseConnectionFactory
        implements QueueWorker {

    @Override
    public void processNextJob() {

        String selectJob = """
                SELECT bj.job_id,
                       d.dept_name
                FROM background_jobs bj
                INNER JOIN departments d
                    ON bj.dept_id = d.dept_id
                WHERE bj.status = ?
                  AND d.dept_name = ?
                ORDER BY bj.created_at
                LIMIT 1
                FOR UPDATE SKIP LOCKED
                """;

        String updateJob = """
                UPDATE background_jobs
                SET status = ?
                WHERE job_id = ?
                """;

        Connection conn = null;

        try {

            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement selectStmt =
                         conn.prepareStatement(selectJob)) {

                selectStmt.setString(1, "PENDING");
                selectStmt.setString(2, "Engineering");

                try (ResultSet rs =
                             selectStmt.executeQuery()) {

                    if (rs.next()) {

                        long jobId =
                                rs.getLong("job_id");

                        System.out.println(
                                "Processing Job ID: "
                                        + jobId
                        );

                        try (PreparedStatement updateStmt =
                                     conn.prepareStatement(updateJob)) {

                            updateStmt.setString(
                                    1,
                                    "PROCESSING"
                            );

                            updateStmt.setLong(
                                    2,
                                    jobId
                            );

                            updateStmt.executeUpdate();
                        }
                    } else {
                        System.out.println(
                                "No pending jobs found."
                        );
                    }
                }
            }

            conn.commit();

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
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

public class EnterpriseJobQueue {

    public static void main(String[] args) {

        QueueWorker worker =
                new EnterpriseJobQueueWorker();

        worker.processNextJob();
    }
}