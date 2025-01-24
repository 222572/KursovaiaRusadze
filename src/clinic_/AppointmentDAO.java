package clinic_;

import java.sql.*;

public class AppointmentDAO {

    private Connection connection;

    public AppointmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void addAppointment(Appointment appointment) throws SQLException {
        String getDoctorIdQuery = "SELECT id FROM doctors WHERE full_name = ?";
        String getPatientIdQuery = "SELECT id FROM patients WHERE full_name = ?";
        int doctorId = -1;
        int patientId = -1;
        try (PreparedStatement pstDoctor = connection.prepareStatement(getDoctorIdQuery);
             PreparedStatement pstPatient = connection.prepareStatement(getPatientIdQuery)) {
            pstDoctor.setString(1, appointment.getDoctorName());
            pstPatient.setString(1, appointment.getPatientName());
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
            String query = "INSERT INTO appointments_schedule (doctorid, patientid, appointment_datetime, room_number, status) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setInt(1, doctorId);
                pst.setInt(2, patientId);
                pst.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
                pst.setInt(4, appointment.getRoomNumber());
                pst.setString(5, appointment.getStatus());
                connection.setAutoCommit(false);
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected == 0) {
                    System.out.println("Запись не была добавлена.");
                } else {
                    System.out.println("Запись успешно добавлена.");
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


 public Appointment getAppointment(String doctorName, String appointmentDateTime) throws SQLException {
     String getDoctorIdQuery = "SELECT id FROM doctors WHERE full_name = ?";
     int doctorId = -1;
     try (PreparedStatement pst = connection.prepareStatement(getDoctorIdQuery)) {
         pst.setString(1, doctorName);
         try (ResultSet rs = pst.executeQuery()) {
             if (rs.next()) {
                 doctorId = rs.getInt("id");
             }
         }
     }
     if (doctorId == -1) {
         return null;
     }
     String query = "SELECT a.doctorid, a.patientid, a.appointment_datetime, a.room_number, a.status, " +
             "d.full_name AS doctor_name, p.full_name AS patient_name " +
             "FROM appointments_schedule a " +
             "JOIN doctors d ON a.doctorid = d.id " +
             "JOIN patients p ON a.patientid = p.id " +
             "WHERE a.doctorid = ? AND a.appointment_datetime = CAST(? AS TIMESTAMP)";
     try (PreparedStatement pst = connection.prepareStatement(query)) {
         pst.setInt(1, doctorId);
         pst.setString(2, appointmentDateTime);
         try (ResultSet rs = pst.executeQuery()) {
             if (rs.next()) {
                 return mapToAppointment(rs);
             }
         }
     }
     return null;
 }


    public void updateAppointment(Appointment appointment) throws SQLException {
        String query = "UPDATE appointments_schedule SET patientid = (SELECT id FROM patients WHERE full_name = ?), " +
                "room_number = ?, status = ? WHERE doctorid = (SELECT id FROM doctors WHERE full_name = ?) " +
                "AND appointment_datetime = CAST(? AS TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int doctorId = getDoctorIdByName(appointment.getDoctorName());
            if (doctorId == -1) {
                throw new SQLException("Врач не найден.");
            }

            int patientId = getPatientIdByName(appointment.getPatientName());
            if (patientId == -1) {
                throw new SQLException("Пациент не найден.");
            }
            stmt.setString(1, appointment.getPatientName());
            stmt.setInt(2, appointment.getRoomNumber());
            stmt.setString(3, appointment.getStatus());
            stmt.setString(4, appointment.getDoctorName());
            stmt.setString(5, appointment.getAppointmentDateTime());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Запись успешно обновлена.");
            } else {
                System.out.println("Запись не найдена для обновления.");
            }
        }
    }

    // Метод для поиска ID врача по имени
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

    public void deleteAppointment(String doctorName, String appointmentDateTime) throws SQLException {
        String doctorIdQuery = "SELECT id FROM doctors WHERE full_name = ?";
        int doctorId = -1;

        try (PreparedStatement pstDoctor = connection.prepareStatement(doctorIdQuery)) {
            pstDoctor.setString(1, doctorName);
            ResultSet doctorResult = pstDoctor.executeQuery();
            if (doctorResult.next()) {
                doctorId = doctorResult.getInt("id");
            }
        }

        if (doctorId == -1) {
            throw new SQLException("Doctor not found");
        }

        String query = "DELETE FROM appointments_schedule WHERE doctorid = ? AND appointment_datetime = CAST(? AS TIMESTAMP)";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, doctorId);
            pst.setString(2, appointmentDateTime);
            pst.executeUpdate();
        }
    }

    private Appointment mapToAppointment(ResultSet rs) throws SQLException {
        String doctorName = rs.getString("doctor_name");
        String patientName = rs.getString("patient_name");
        String appointmentDateTime = rs.getString("appointment_datetime");
        int roomNumber = rs.getInt("room_number");
        String status = rs.getString("status");

        return new Appointment(doctorName, patientName, appointmentDateTime, roomNumber, status);
    }
}
