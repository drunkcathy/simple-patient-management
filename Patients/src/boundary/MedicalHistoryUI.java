package boundary;
import adt.ArrayListADT;
import dao.AppointmentManagement;
import dao.MedicalHistoryManagement;
import dao.PatientManagement;
import entity.MedicalHistory;
import entity.Patient;
import entity.Appointment;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MedicalHistoryUI {
    
public static final String PURPLE ="\u001B[35m";
public static final String RED ="\u001B[31m";
public static final String RESET ="\u001B[33m";
    private final AppointmentManagement am;
    private final MedicalHistoryManagement mhm;
    private final PatientManagement pm;
    private final Scanner scanner;

    public MedicalHistoryUI(AppointmentManagement am) {
    this.pm = new PatientManagement();
    this.mhm = new MedicalHistoryManagement();
    this.mhm.loadHistories(pm.getPatientsMap());
    this.scanner = new Scanner(System.in);
    this.am = am; 
}


    public void start() {
        int choice;
        do {
            System.out.println("\n===== MEDICAL HISTORY MENU =====");
            System.out.println("1. Add Medical History");
            System.out.println("2. View Patient Medical Histories");
            System.out.println("3. Load All Histories From File");
            System.out.println("4. Exit");
            System.out.print(PURPLE+"Enter choice: "+RESET);

            while (!scanner.hasNextInt()) {
                System.out.println(RED+"Please enter a valid number."+RESET);
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1 -> addHistory();
                case 2 -> viewHistories();
                case 3 -> reloadHistories();
                case 4 -> System.out.println("Exiting Medical History UI...");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 4);
    }

private void addHistory() {
    System.out.print("Enter Patient ID: ");
    String patientId = scanner.nextLine().trim();

    ArrayListADT<MedicalHistory> histories = mhm.getHistoryByPatientId(patientId);

// ✅ Call static method to print the table
MedicalHistoryManagement.printHistories(histories);

    Patient patient = pm.getPatient(patientId);
    if (patient == null) {
        System.out.println(RED+"Patient not found."+RESET);
        return;
    }

   Appointment appointment;
while (true) {
    System.out.print("Enter Appointment ID: ");
    String appID = scanner.nextLine().trim();

    appointment = am.getAppointmentByID(appID); 
    if (appointment == null) {
        System.out.println(RED+"Appointment ID does not exist. Try again."+RESET);
    } else if (!appointment.getPatient().getID().equals(patientId)) {
        System.out.println(RED+"This appointment does not belong to the patient. Try again."+RESET);
    } else {
        break;
    }
}


    System.out.print("Enter Diagnosis: ");
    String diagnosis = scanner.nextLine().trim();

    System.out.print("Enter Treatment: ");
    String treatment = scanner.nextLine().trim();

    LocalDateTime dateTime = LocalDateTime.now();

    // Create and add medical history
    MedicalHistory history = new MedicalHistory(patient, appointment, dateTime, diagnosis, treatment);
    mhm.addHistory(history);
    System.out.println(PURPLE+"Medical history added successfully."+RESET);
}


    private void viewHistories() {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine().trim();
        var histories = mhm.getHistoryByPatientId(patientId);
        MedicalHistoryManagement.printHistories(histories);
    }

   private void reloadHistories() {
    mhm.loadHistories(pm.getPatientsMap()); // reload from file
    System.out.println("Histories reloaded from file.");

    // loop all patients and print their histories
    for (Patient patient : pm.getPatientsMap().values()) {
        var histories = mhm.getHistoryByPatientId(patient.getID());
        MedicalHistoryManagement.printHistories(histories);
    }
}

  
}
