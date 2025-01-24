package clinic_;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class PatientDAO {
    private Connection connection;
    public PatientDAO(Connection connection) {
        this.connection = connection;
    }

    public void addPatient(Patient patient) throws SQLException {
        String query = "INSERT INTO patients (full_name, birth_date, policy_number, address, contact_details) " +
                "VALUES (?, cast(? as DATE), ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, patient.getFullName());
            pst.setString(2, patient.getBirthDate());
            pst.setString(3, patient.getPolicyNumber());
            pst.setString(4, patient.getAddress());
            pst.setString(5, patient.getContactDetails());
            pst.executeUpdate();
        }
    }

    public Patient getPatientByName(String full_name) throws SQLException {
        String query = "SELECT * FROM patients WHERE full_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, full_name);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapToPatient(rs);
                }
            }
        }
        return null;
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Patient patient = mapToPatient(rs);
                patients.add(patient);
            }
        }
        return patients;
    }

    public void updatePatient(Patient patient) throws SQLException {
        String query = "UPDATE patients SET birth_date = cast(? as DATE), policy_number = ?, address = ?, contact_details = ? " +
                "WHERE full_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, patient.getBirthDate()); // birth_date
            pst.setString(2, patient.getPolicyNumber()); // policy_number
            pst.setString(3, patient.getAddress()); // address
            pst.setString(4, patient.getContactDetails()); // contact_details
            pst.setString(5, patient.getFullName()); // full_name (WHERE)
            pst.executeUpdate();
        }
    }

    public void deletePatient(String full_name) throws SQLException {
        String query = "DELETE FROM patients WHERE full_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, full_name);
            pst.executeUpdate();
        }
    }

    private Patient mapToPatient(ResultSet rs) throws SQLException {
        String fullName = rs.getString("full_name");
        String birthDate = rs.getString("birth_date");
        String policyNumber = rs.getString("policy_number");
        String address = rs.getString("address");
        String contactDetails = rs.getString("contact_details");

        return new Patient(fullName, birthDate, policyNumber, address, contactDetails);
    }
}

