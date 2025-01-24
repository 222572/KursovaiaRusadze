package clinic_;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {

    private Connection connection;

    public MedicalRecordDAO(Connection connection) {
        this.connection = connection;
    }

    public void addMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        String getDoctorIdQuery = "SELECT id FROM doctors WHERE full_name = ?";
        String getPatientIdQuery = "SELECT id FROM patients WHERE full_name = ?";
        int doctorId = -1;
        int patientId = -1;
        try (PreparedStatement pstDoctor = connection.prepareStatement(getDoctorIdQuery);
             PreparedStatement pstPatient = connection.prepareStatement(getPatientIdQuery)) {
            pstDoctor.setString(1, medicalRecord.getDoctorName());
            pstPatient.setString(1, medicalRecord.getPatientName());
            try (ResultSet rsDoctor = pstDoctor.executeQuery();
                 ResultSet rsPatient = pstPatient.executeQuery()) {
                if (rsDoctor.next()) {
                    doctorId = rsDoctor.getInt("id");
                }
                if (rsPatient.next()) {
                    patientId = rsPatient.getInt("id");
                }
            }
        }
        if (doctorId != -1 && patientId != -1) {
            String query = "INSERT INTO medical_records (doctor_id, patient_id, visit_date, diagnosis, treatment) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setInt(1, doctorId);
                pst.setInt(2, patientId);
                pst.setTimestamp(3, Timestamp.valueOf(medicalRecord.getVisitDate()));
                pst.setString(4, medicalRecord.getDiagnosis());
                pst.setString(5, medicalRecord.getTreatment());
                connection.setAutoCommit(false);
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("Медицинская Запись не была добавлена.");
                } else {
                    System.out.println("Медицинская Запись успешно добавлена.");
                }
                connection.commit();
            } catch (SQLException e) {
                System.out.println("Ошибка при добавлении данных: " + e.getMessage());
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } else {
            System.out.println("Не удалось найти врача или пациента по имени.");
        }
    }

    public List<MedicalRecord> getMedicalRecordsByPatientName(String patientName) {
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        Connection connection = null;
        PreparedStatement findPatientIdStatement = null;
        PreparedStatement findMedicalRecordsStatement = null;
        PreparedStatement findDoctorNameStatement = null;
        ResultSet patientIdResultSet = null;
        ResultSet medicalRecordsResultSet = null;
        ResultSet doctorNameResultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            String findPatientIdQuery = "SELECT id FROM patients WHERE full_name = ?";
            findPatientIdStatement = connection.prepareStatement(findPatientIdQuery);
            findPatientIdStatement.setString(1, patientName);
            patientIdResultSet = findPatientIdStatement.executeQuery();

            if (patientIdResultSet.next()) {
                int patientId = patientIdResultSet.getInt("id");

                String findMedicalRecordsQuery = """
                SELECT doctor_id, visit_date, diagnosis, treatment 
                FROM medical_records 
                WHERE patient_id = ? 
                ORDER BY visit_date DESC
            """;
                findMedicalRecordsStatement = connection.prepareStatement(findMedicalRecordsQuery);
                findMedicalRecordsStatement.setInt(1, patientId);
                medicalRecordsResultSet = findMedicalRecordsStatement.executeQuery();

                while (medicalRecordsResultSet.next()) {
                    int doctorId = medicalRecordsResultSet.getInt("doctor_id");
                    String visitDate = medicalRecordsResultSet.getString("visit_date");
                    String diagnosis = medicalRecordsResultSet.getString("diagnosis");
                    String treatment = medicalRecordsResultSet.getString("treatment");

                    String findDoctorNameQuery = "SELECT full_name FROM doctors WHERE id = ?";
                    findDoctorNameStatement = connection.prepareStatement(findDoctorNameQuery);
                    findDoctorNameStatement.setInt(1, doctorId);
                    doctorNameResultSet = findDoctorNameStatement.executeQuery();

                    String doctorName = doctorNameResultSet.next() ? doctorNameResultSet.getString("full_name") : "Unknown";

                    medicalRecords.add(new MedicalRecord(doctorName, patientName, visitDate, diagnosis, treatment));
                }
            } else {
                System.out.println("Пациент с именем " + patientName + " не найден.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (doctorNameResultSet != null) doctorNameResultSet.close();
                if (medicalRecordsResultSet != null) medicalRecordsResultSet.close();
                if (findMedicalRecordsStatement != null) findMedicalRecordsStatement.close();
                if (patientIdResultSet != null) patientIdResultSet.close();
                if (findPatientIdStatement != null) findPatientIdStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return medicalRecords;
    }

    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        String query = "UPDATE medical_records SET doctor_id = ?, diagnosis = ?, treatment = ? " +
                "WHERE patient_id = (SELECT id FROM patients WHERE full_name = ?) " +
                "AND visit_date = CAST(? AS TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int patientId = getPatientIdByName(medicalRecord.getPatientName());
            if (patientId == -1) {
                throw new SQLException("Пациент не найден.");
            }

            int doctorId = getDoctorIdByName(medicalRecord.getDoctorName());
            if (doctorId == -1) {
                throw new SQLException("Врач не найден.");
            }

            stmt.setInt(1, doctorId);
            stmt.setString(2, medicalRecord.getDiagnosis());
            stmt.setString(3, medicalRecord.getTreatment());
            stmt.setString(4, medicalRecord.getPatientName());
            stmt.setString(5, medicalRecord.getVisitDate());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Медицинская запись успешно обновлена.");
            } else {
                System.out.println("Запись не найдена для обновления.");
            }
        }
    }

    private int getPatientIdByName(String patientName) throws SQLException {
        String query = "SELECT id FROM patients WHERE full_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    private int getDoctorIdByName(String doctorName) throws SQLException {
        String query = "SELECT id FROM doctors WHERE full_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctorName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }


    public void deleteMedicalRecord(String patientName, String visitDate) throws SQLException {
        String query = "DELETE FROM medical_records WHERE patient_id = (SELECT id FROM patients WHERE full_name = ?) " +
                "AND visit_date = CAST(? AS TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int patientId = getPatientIdByName(patientName);
            if (patientId == -1) {
                throw new SQLException("Пациент не найден.");
            }

            stmt.setString(1, patientName);
            stmt.setString(2, visitDate);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Медицинская запись успешно удалена.");
            } else {
                System.out.println("Запись не найдена для удаления.");
            }
        }
    }
}

