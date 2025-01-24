package clinic_;

public class Patient {
    private String fullName;
    private String birthDate;
    private String policyNumber;
    private String address;
    private String contactDetails;

    public Patient(String fullName, String birthDate, String policyNumber, String address, String contactDetails) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.policyNumber = policyNumber;
        this.address = address;
        this.contactDetails = contactDetails;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "clinic_.Patient{" +
                "fullName='" + fullName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                ", address='" + address + '\'' +
                ", contactDetails='" + contactDetails + '\'' +
                '}';
    }
}
