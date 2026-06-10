import java.sql.*;

interface RegistrationManager {
    void enrollAtRiskStudents();
}

abstract class DatabaseConnectionProvider {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/edixo";
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

class EdixoRegistrationRepository
        extends DatabaseConnectionProvider
        implements RegistrationManager {

    private static final String FIND_AT_RISK = """
            SELECT s.student_id,
                   s.full_name
            FROM students s
            LEFT JOIN course_registrations cr
                ON s.student_id = cr.student_id
            WHERE cr.student_id IS NULL
            """;

    private static final String INSERT_COURSE = """
            INSERT INTO course_registrations
            (student_id, course_code, semester)
            VALUES (?, ?, ?)
            """;

    @Override
    public void enrollAtRiskStudents() {

        try (
                Connection conn = getConnection();
                PreparedStatement findStmt =
                        conn.prepareStatement(FIND_AT_RISK);
                PreparedStatement insertStmt =
                        conn.prepareStatement(INSERT_COURSE);
                ResultSet rs = findStmt.executeQuery()
        ) {

            int batchCount = 0;

            while (rs.next()) {

                long studentId =
                        rs.getLong("student_id");

                insertStmt.setLong(1, studentId);
                insertStmt.setString(2, "Orientation 101");
                insertStmt.setString(3, "Fall 2026");

                insertStmt.addBatch();
                batchCount++;

                if (batchCount % 1000 == 0) {
                    insertStmt.executeBatch();
                }
            }

            insertStmt.executeBatch();

            System.out.println(
                    "At-risk students enrolled successfully."
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class Main {

    public static void main(String[] args) {

        RegistrationManager manager =
                new EdixoRegistrationRepository();

        manager.enrollAtRiskStudents();
    }
}