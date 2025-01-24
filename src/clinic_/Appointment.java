package clinic_;

public class Appointment {
    private String doctorName;
    private String patientName;
    private String appointmentDateTime;
    private int roomNumber;
    private String status;

       public Appointment(String doctorName, String patientName, String appointmentDateTime, int roomNumber, String status) {
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.appointmentDateTime = appointmentDateTime;
        this.roomNumber = roomNumber;
        this.status = status;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    @Override
    public String toString() {
        return "Appointment{" +
                "doctorName='" + doctorName + '\'' +
                ", patientName='" + patientName + '\'' +
                ", appointmentDateTime='" + appointmentDateTime + '\'' +
                ", roomNumber=" + roomNumber +
                ", status='" + status + '\'' +
                '}';
    }
}