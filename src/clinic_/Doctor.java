package clinic_;

public class Doctor {
    private String fullName;
    private String specialization;
    private String department;
    private int workExperience;
    private String contactDetails;

    public Doctor() {}

    public Doctor(String fullName, String specialization, String department, int workExperience, String contactDetails) {
        this.fullName = fullName;
        this.specialization = specialization;
        this.department = department;
        this.workExperience = workExperience;
        this.contactDetails = contactDetails;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(int workExperience) {
        this.workExperience = workExperience;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    @Override
    public String toString() {
        return "clinic_.Doctor{" +
                "fullName='" + fullName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", department='" + department + '\'' +
                ", workExperience=" + workExperience +
                ", contactDetails='" + contactDetails + '\'' +
                '}';
    }
}
