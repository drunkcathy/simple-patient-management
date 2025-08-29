package entity;

import java.time.LocalDateTime;
import entity.Patient;

public class Appointment {
    
    private Patient patient; 
    private String appointmentID;
    private LocalDateTime appointmentTime; 
    private String roomOnTheDay;
    private String reasonToCheck;
    private LocalDateTime nextCheckUp; 
    private String rescheduledFrom;
    
    public Appointment() {
        
    }

    public Appointment(Patient patient, String appointmentID, LocalDateTime appointmentTime, 
                      String roomOnTheDay, String reasonToCheck, LocalDateTime nextCheckUp,String rescheduledFrom) {
        this.appointmentID = appointmentID;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.roomOnTheDay = roomOnTheDay;
        this.reasonToCheck = reasonToCheck;
        this.nextCheckUp = nextCheckUp;
        this.rescheduledFrom=rescheduledFrom;
    }

      public String getRescheduledFrom() {
        return rescheduledFrom;
    }

    public void setRescheduledFrom(String rescheduledFrom) {
        this.rescheduledFrom = rescheduledFrom;
    }
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getRoomOnTheDay() {
        return roomOnTheDay;
    }

    public void setRoomOnTheDay(String roomOnTheDay) {
        this.roomOnTheDay = roomOnTheDay;
    }

    public String getReasonToCheck() {
        return reasonToCheck;
    }

    public void setReasonToCheck(String reasonToCheck) {
        this.reasonToCheck = reasonToCheck;
    }

    public LocalDateTime getNextCheckUp() {
        return nextCheckUp;
    }

    public void setNextCheckUp(LocalDateTime nextCheckUp) {
        this.nextCheckUp = nextCheckUp;
    }

    @Override
    public String toString() {
        return String.format("Appointment[%s] Patient: %s | Date: %s | Room: %s | Reason: %s | Next Check-Up: %s",
                appointmentID, patient, appointmentTime, roomOnTheDay, reasonToCheck, nextCheckUp);
    }
}
