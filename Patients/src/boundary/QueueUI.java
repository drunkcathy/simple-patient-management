package boundary;

import dao.PatientManagement;
import dao.QueueManagement;
import entity.Patient;

import java.util.Scanner;

public class QueueUI {
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    private final QueueManagement queueManagement;
    private final PatientManagement patientManagement;
    private final Scanner scanner = new Scanner(System.in);

    public QueueUI() {
        // Use your actual PatientManagement here (loads patients from file)
        patientManagement = new PatientManagement();
        queueManagement = new QueueManagement(patientManagement);
    }

    public void start() {
        int choice;
        do {
            System.out.println("\n===== CLINIC QUEUE MENU =====");
            System.out.println("1. Load queue from patient database");
            System.out.println("2. Add patient to queue");
            System.out.println("3. View current queue");
            System.out.println("4. Serve next patient");
            System.out.println("5. Peek next patient");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.println(RED+"Please enter a valid number."+RESET);
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> loadQueue();
                case 2 -> addPatient();
                case 3 -> queueManagement.updateStatus();
                case 4 -> servePatient();
                case 5 -> peekPatient();
                case 6 -> System.out.println("Exiting Queue UI...");
                default -> System.out.println(RED+"Invalid choice. Try again."+RESET);
            }
        } while (choice != 6);
    }

    private void loadQueue() {
        queueManagement.loadQueueFromDatabase();
        System.out.println("Queue loaded from patient database.");
        queueManagement.updateStatus();
    }

    private void addPatient() {
        System.out.print("Enter patient ID: ");
        String id = scanner.nextLine();
        Patient patient = patientManagement.getPatient(id);
        if (patient != null) {
            queueManagement.addPatient(patient);
            System.out.println("Patient added to queue.");
            queueManagement.updateStatus();
        } else {
            System.out.println(RED+"Patient not found."+RESET);
        }
    }

    private void servePatient() {
        Patient served = queueManagement.serveNextPatient();
        if (served != null) {
            System.out.println("Serving: " + served.getName() + " (" + served.getID() + ")");
        } else {
            System.out.println("Queue is empty.");
        }
    }

    private void peekPatient() {
        Patient next = queueManagement.peekNextPatient();
        if (next != null) {
            System.out.println("Next up: " + next.getName() + " (" + next.getID() + ")");
        } else {
            System.out.println("Queue is empty.");
        }
    }

  
}
