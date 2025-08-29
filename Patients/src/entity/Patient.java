package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {

    private Appointment appointment;
    private String name;
    private String ID;
    private int age;
    private String identityCard;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String bloodType;
    private char gender;
    private String email;
    private String medicalHistory; // now a simple string
    private String priority;        // emergency, elderly, normal
    private int priorityLevel;      // lower number = higher priority
    private LocalDateTime registrationTime;
    private String status;          // active, archived, in queue
    private String state;

    public Patient() {
        this.registrationTime = LocalDateTime.now();
        this.status="active";
    }

    public Patient(String name, String ID, int age, String identityCard, LocalDate dateOfBirth,
                   String phoneNumber, String bloodType, char gender, String email,
                   String medicalHistory, String priority, int priorityLevel, String status, String state) {
        this.name = name;
        this.ID = ID;
        this.age = age;
        this.identityCard = identityCard;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
        this.gender = gender;
        this.email = email;
        this.medicalHistory = medicalHistory;
        this.priority = priority;
        this.priorityLevel = priorityLevel;
        this.status = status;
        this.state = state;
        this.registrationTime = LocalDateTime.now();
    }

    // Standard getters and setters
    public Appointment getAppointment() {
    return appointment;
}

public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getIdentityCard() { return identityCard; }
    public void setIdentityCard(String identityCard) { this.identityCard = identityCard; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public char getGender() { return gender; }
    public void setGender(char gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(int priorityLevel) { this.priorityLevel = priorityLevel; }

    public LocalDateTime getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(LocalDateTime registrationTime) { this.registrationTime = registrationTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    // CSV export
    public String toCSV() {
        return String.join(",",
                name, ID, String.valueOf(age), identityCard, dateOfBirth.toString(),
                phoneNumber, bloodType, String.valueOf(gender), email,
                medicalHistory != null ? medicalHistory : "",
                priority, String.valueOf(priorityLevel), status, state,
                registrationTime != null ? registrationTime.toString() : ""
        );
    }

    // CSV import
    public static Patient fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1); // keep empty strings
        if (parts.length < 14) return null;

        Patient patient = new Patient(
                parts[0], parts[1], Integer.parseInt(parts[2]), parts[3],
                LocalDate.parse(parts[4]), parts[5],
                parts[6], parts[7].charAt(0), parts[8],
                parts[9], parts[10], Integer.parseInt(parts[11]), parts[12], parts[13]
        );

        if (parts.length > 14 && !parts[14].isEmpty()) {
            patient.setRegistrationTime(LocalDateTime.parse(parts[14]));
        }

        return patient;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "name='" + name + '\'' +
                ", ID='" + ID + '\'' +
                ", age=" + age +
                ", identityCard='" + identityCard + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", priority='" + priority + '\'' +
                ", priorityLevel=" + priorityLevel +
                ", status='" + status + '\'' +
                ", state='" + state + '\'' +
                ", registrationTime=" + registrationTime +
                '}';
    }

}
