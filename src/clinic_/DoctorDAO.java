package clinic_;

import java.sql.*;

public class DoctorDAO {

    private Connection connection;

    public DoctorDAO(Connection connection) {
        this.connection = connection;
    }

    public void addDoctor(Doctor doctor) throws SQLException {
        String query = "INSERT INTO doctors (full_name, specialization, department, work_experience, contact_details) " +
                "VALUES ( ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, doctor.getFullName());
            pst.setString(2, doctor.getSpecialization());
            pst.setString(3, doctor.getDepartment());
            pst.setInt(4, doctor.getWorkExperience());
            pst.setString(5, doctor.getContactDetails());
            pst.executeUpdate();
        }
    }

    public Doctor getDoctorByName(String full_name) throws SQLException {
        String query = "SELECT * FROM doctors WHERE full_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, full_name);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapToDoctor(rs);
                }
            }
        }
        return null;
    }

    public ResultSet getAllDoctors() throws SQLException {
        String query = "SELECT * FROM doctors";
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    public void updateDoctor(Doctor doctor) throws SQLException {
        String query = "UPDATE doctors SET specialization = ?, department = ?, work_experience = ?, " +
                "contact_details = ? WHERE full_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, doctor.getSpecialization());
            pst.setString(2, doctor.getDepartment());
            pst.setInt(3, doctor.getWorkExperience());
            pst.setString(4, doctor.getContactDetails());
            pst.setString(5, doctor.getFullName());
            pst.executeUpdate();
        }
    }

    public void deleteDoctor(String full_name) throws SQLException {
        String query = "DELETE FROM doctors WHERE full_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, full_name);
            pst.executeUpdate();
        }
    }

    private Doctor mapToDoctor(ResultSet rs) throws SQLException {
        String fullName = rs.getString("full_name");
        String specialization = rs.getString("specialization");
        String department = rs.getString("department");
        int workExperience = rs.getInt("work_experience");
        String contactDetails = rs.getString("contact_details");

        return new Doctor(fullName, specialization, department, workExperience, contactDetails);
    }
}

