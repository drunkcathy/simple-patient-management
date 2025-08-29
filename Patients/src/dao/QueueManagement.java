package dao;

import entity.Patient;
import entity.Appointment;
import adt.PriorityQueueADT;
import adt.ArrayListADT;
import adt.TreeMapADT;
import utility.PatientPriorityComparator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class QueueManagement {

    private final PriorityQueueADT<Patient> serviceQueue;
   private long nanoIncrement = 0;
    
    TreeMapADT<Long, Patient> arrivalMap = new TreeMapADT<>();

    private final String filePath = "queue.txt";

    private final PatientManagement pm;
    private final AppointmentManagement am;

    public QueueManagement(PatientManagement pm) {
        this.pm = pm;
        this.am = new AppointmentManagement(pm.getPatientsMap()); // create default AM
        this.serviceQueue = new PriorityQueueADT<>(new PatientPriorityComparator());
    }

    public void addPatient(Patient p) {
    if (!serviceQueue.getAllItems().contains(p)) {
        serviceQueue.enqueue(p);

        // Record arrival timestamp in TreeMap
        LocalDateTime registrationTime = p.getRegistrationTime();
        if (registrationTime != null) {
            long epoch = registrationTime.toEpochSecond(ZoneOffset.UTC) * 1_000_000_000 + nanoIncrement++;
            arrivalMap.put(epoch, p);
        }
    }
}


    public boolean isQueueEmpty() {
        return serviceQueue.isEmpty();
    }

    // Peek next patient without dequeuing
    public Patient peekNextPatient() {
        return serviceQueue.peek();
    }

    public Patient serveNextPatient() {
        if (serviceQueue.isEmpty()) return null;
        Patient current = serviceQueue.dequeue();

        // Record arrival
        if (current.getRegistrationTime() != null) {
            arrivalMap.put(current.getRegistrationTime().toEpochSecond(ZoneOffset.UTC), current);
        }

        // Update status: current is NOW SERVING
        updateStatus();

        return current;
    }

    public void registerAndQueuePatient(Patient p) {
        addPatient(p);
        updateStatus();   
    }

    // Load all patients from database into queue (without duplicates)
    public void loadQueueFromDatabase() {
        serviceQueue.clear();
        ArrayListADT<Patient> patients = pm.getAllPatients();
        ArrayListADT<Appointment> appointments = am.getAllAppointments();

        if (patients.isEmpty()) return;

        for (Patient p : patients) {
            // Attach upcoming appointment if exists
            Appointment upcoming = null;
            for (Appointment a : appointments) {
                if (a.getPatient().getID().equals(p.getID()) &&
                    a.getAppointmentTime().isAfter(java.time.LocalDateTime.now())) {
                    if (upcoming == null || a.getAppointmentTime().isBefore(upcoming.getAppointmentTime())) {
                        upcoming = a;
                    }
                }
            }
            if (upcoming != null) p.setAppointment(upcoming);
            serviceQueue.enqueue(p);
        }
    }

public void updateStatus() {
    clearConsole();
    String line = "============================================================";
    System.out.println(line);
    System.out.println(centerText("     CLINIC QUEUE STATUS     ", line.length()));
    System.out.println(line);

    ArrayListADT<Patient> queueList = serviceQueue.getAllItems();
    Patient nowServing = queueList.isEmpty() ? null : queueList.get(0);
    Patient nextUp    = queueList.size() > 1 ? queueList.get(1) : null;

    System.out.printf("%-15s: %s%n", "NOW SERVING",
            (nowServing != null ? formatPatient(nowServing) : "(none)"));
    System.out.printf("%-15s: %s%n", "NEXT UP",
            (nextUp != null ? formatPatient(nextUp) : "(none)"));

    System.out.println(line);

    // ----- ARRIVAL ORDER -----
    System.out.println(centerText("Arrival Order", line.length()));
    System.out.println(line);
    if (arrivalMap.isEmpty()) {
        System.out.println("(none)");
    } else {
        printPatientTable(arrivalMap.values(), null);
    }

    System.out.println(line);

    // ----- SERVICE ORDER -----
    System.out.println(centerText("Service Order", line.length()));
    System.out.println(line);
    printPatientTable(queueList, nowServing);

    saveToFile(nowServing, nextUp, queueList);
}


// Save with edit = true to keep history
private void saveToFile(Patient nowServing, Patient nextUp, List<Patient> queueList) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
        writer.write("=== Queue Snapshot @ " + java.time.LocalDateTime.now() + " ===\n");
        writer.write("NOW SERVING: " + (nowServing != null ? formatPatient(nowServing) : "(none)") + "\n");
        writer.write("NEXT UP    : " + (nextUp != null ? formatPatient(nextUp) : "(none)") + "\n");
        
        writer.write("\nService Queue:\n");
        if (queueList.isEmpty()) {
            writer.write("(none)\n");
        } else {
            int i = 1;
            for (Patient p : queueList) {
                writer.write(i++ + ". " + formatPatient(p) + "\n");
            }
        }

        writer.write("\nArrival Order:\n");
        if (arrivalMap.isEmpty()) {
            writer.write("(none)\n");
        } else {
            int i = 1;
            for (Patient p : arrivalMap.values()) {
                writer.write(i++ + ". " + formatPatient(p) + "\n");
            }
        }

        writer.write("\n\n"); // blank line between snapshots
    } catch (IOException e) {
        System.out.println("rror writing queue file: " + e.getMessage());
    }
}

    // Helpers
    private String formatPatient(Patient p) {
        return String.format("%s (%s) - %s", p.getName(), p.getID(), p.getPriority());
    }

    private void printPatientTable(Iterable<Patient> patients, Patient exclude) {
        Set<Patient> unique = new LinkedHashSet<>();
        patients.forEach(p -> { if (exclude == null || !p.equals(exclude)) unique.add(p); });

        if (unique.isEmpty()) {
            System.out.println("(none)");
            return;
        }

        System.out.printf("%-4s %-20s %-10s %-10s%n", "No.", "Name", "ID", "Priority");
        System.out.println("------------------------------------------------------------");

        int i = 1;
        for (Patient p : unique) {
            System.out.printf("%-4d %-20s %-10s %-10s%n", i++, p.getName(), p.getID(), p.getPriority());
        }
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    private void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception ignored) {}
    }
}
