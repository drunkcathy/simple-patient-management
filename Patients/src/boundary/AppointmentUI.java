package boundary;

import java.util.Scanner;
import entity.Patient;
import dao.AppointmentManagement;
import adt.HashMapADT;
import entity.Appointment;

public class AppointmentUI {
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    private final AppointmentManagement am;
    private final HashMapADT<String, Patient> patients;
    private final Scanner scanner;

    public AppointmentUI(AppointmentManagement am, HashMapADT<String, Patient> patients) {
        this.am = am;
        this.patients = patients;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choice;
        do {
            showMenu();
            System.out.print(GREEN+"Enter choice: "+RESET);
            while (!scanner.hasNextInt()) {
                System.out.print(RED+"Invalid input! Enter a number: "+RESET);
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> am.bookAppointment();
                case 2 -> viewAppointments();
                case 3 -> am.cleanAppointmentFile(); // optional cleanup
                case 0 -> System.out.println("Exiting Appointment UI.");
                default -> System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    private void showMenu() {
        System.out.println("\n=== Appointment Management ===");
        System.out.println("1. Book Appointment");
        System.out.println("2. View All Appointments");
        
        System.out.println("3. Clean Appointment File");
        System.out.println("0. Exit");
    }

   /* private void bookAppointment() {
        System.out.println("\n--- Book Appointment ---");
        
        System.out.print("Enter Patient ID: ");
        String patientID = scanner.nextLine().trim();

        Patient patient = patients.get(patientID);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        am.bookAppointment();
        System.out.println("Appointment booked successfully for " + patient.getName());
    }*/

   public void viewAppointments() {
    System.out.println("\n--- All Appointments ---");
    am.loadAppointments();
    adt.ArrayListADT<Appointment> list = am.getAllAppointments();
    am.viewAppointment(list); 
}

}
