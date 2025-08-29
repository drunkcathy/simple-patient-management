package entity;

import java.time.LocalDateTime;

public class MedicalHistory {
    private Patient patient;   
    private Appointment appointment;
    private LocalDateTime dateTime;
    private String diagnosis;
    private String treatment;

    public MedicalHistory(Patient patient,Appointment appointment, LocalDateTime dateTime, String diagnosis, String treatment) {
        this.patient = patient;
        this.appointment=appointment;
        this.dateTime = dateTime;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    // Getters
    public Patient getPatient() { return patient; }
    public Appointment getAppointment(){ return appointment ;}
    public LocalDateTime getDateTime() { return dateTime; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }

    // Setters
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setAppointment (Appointment appointment) {this.appointment=appointment; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    @Override
    public String toString() {
        return "Date & Time: " + (dateTime != null ? dateTime.toString() : "N/A") +
               " | Appointment :" + (appointment !=null? appointment.getAppointmentID() : "N/A")+
               " | Patient: " + (patient != null ? patient.getName() : "N/A") +
               " | Diagnosis: " + diagnosis +
               " | Treatment: " + treatment;
    }
}
