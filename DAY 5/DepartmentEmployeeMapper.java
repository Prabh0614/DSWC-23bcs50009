import java.sql.*;
import java.util.*;

class Employee {

    private int employeeId;
    private String employeeName;

    public Employee(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    @Override
    public String toString() {
        return employeeName + " (" + employeeId + ")";
    }
}

class Department {

    private int departmentId;
    private String departmentName;
    private List<Employee> employees = new ArrayList<>();

    public Department(int departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    @Override
    public String toString() {
        return departmentName + " -> " + employees;
    }
}

public class DepartmentEmployeeMapper {

    private static final String URL =
            "jdbc:mysql://localhost:3306/companydb";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {

        String sql = """
                SELECT d.dept_id,
                       d.dept_name,
                       e.emp_id,
                       e.emp_name
                FROM Departments d
                INNER JOIN Employees e
                    ON d.dept_id = e.dept_id
                ORDER BY d.dept_id
                """;

        Map<Integer, Department> departmentGraph =
                new HashMap<>();

        try (
                Connection conn =
                        DriverManager.getConnection(
                                URL,
                                USER,
                                PASSWORD
                        );

                PreparedStatement pstmt =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        pstmt.executeQuery()
        ) {

            while (rs.next()) {

                int deptId =
                        rs.getInt("dept_id");

                String deptName =
                        rs.getString("dept_name");

                int empId =
                        rs.getInt("emp_id");

                String empName =
                        rs.getString("emp_name");

                Department department =
                        departmentGraph.get(deptId);

                if (department == null) {

                    department = new Department(
                            deptId,
                            deptName
                    );

                    departmentGraph.put(
                            deptId,
                            department
                    );
                }

                department.getEmployees().add(
                        new Employee(
                                empId,
                                empName
                        )
                );
            }

            departmentGraph.values()
                    .forEach(System.out::println);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}