package dao;

import entity.MedicalHistory;
import entity.Patient;
import adt.ArrayListADT;
import adt.HashMapADT;
import java.awt.List;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class MedicalHistoryManagement {
    
    private static final String FILE_NAME = "medical_history.txt";
private HashMapADT<String, ArrayListADT<MedicalHistory>> historyMap = new HashMapADT<>(100);


    // Ensure file has header
    private void ensureFileHasHeader() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                String line = "+--------------+----------------------+----------------------+----------------------+";
                String format = "| %-12s | %-20s | %-20s | %-20s |%n";

                bw.write(line); bw.newLine();
                bw.write(String.format(format, "Patient ID", "Date & Time", "Diagnosis", "Treatment"));
                bw.newLine();
                bw.write(line); bw.newLine();

            } catch (IOException e) {
                System.out.println("Error writing header: " + e.getMessage());
            }
        }
    }

    
    // Add new history
    public void addHistory(MedicalHistory history) {
        if (history == null || history.getPatient() == null) return;

        String patientId = history.getPatient().getID();

        ArrayListADT<MedicalHistory> histories = historyMap.get(patientId);
        if (histories == null) {
            histories = new ArrayListADT<>();
            historyMap.put(patientId, histories);
        }
        histories.add(history);

        // Save all histories to file in table format
        saveHistoriesToFile();
    }

    private void saveHistoriesToFile() {
        ensureFileHasHeader();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            String line = "+--------------+----------------------+----------------------+----------------------+----------------------+----------------------+";
String format = "| %-12s | %-20s | %-20s | %-20s | %-20s | %-20s |%n";

bw.write(line); bw.newLine();
bw.write(String.format(format, "Patient ID", "Appointment ID", "Room", "Date & Time", "Diagnosis", "Treatment"));
bw.newLine();
bw.write(line); bw.newLine();

for (String patientId : historyMap.getKeys()) {
    ArrayListADT<MedicalHistory> histories = historyMap.get(patientId);
    for (MedicalHistory h : histories) {
        bw.write(String.format(format,
                h.getPatient() != null ? h.getPatient().getID() : "",
                h.getAppointment() != null ? h.getAppointment().getAppointmentID() : "",
                h.getAppointment() != null ? h.getAppointment().getRoomOnTheDay() : "",
                h.getDateTime(),
                h.getDiagnosis(),
                h.getTreatment()));
        bw.newLine();
    }
}
bw.write(line); bw.newLine();


        } catch (IOException e) {
            System.out.println("Error saving histories to file: " + e.getMessage());
        }
    }

public void loadHistories(HashMapADT<String, Patient> patientMap) {
   
    historyMap = new HashMapADT<>(100);
    try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
        String line;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("+") || line.startsWith("| Patient ID")) {
                continue; // skip borders and header
            }

            String[] parts = line.split("\\|");
            if (parts.length < 6) continue;

            String patientId = parts[1].trim();
            String dateTimeStr = parts[4].trim(); // date is in 4th column in your file
            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(dateTimeStr); // adjust format if needed
            } catch (Exception e) {
                System.out.println("Skipping invalid date: " + dateTimeStr);
                continue;
            }

            String diagnosis = parts[5].trim();
            String treatment = parts[6].trim();

            Patient p = patientMap.get(patientId);
            if (p == null) continue;

            MedicalHistory mh = new MedicalHistory(p, null, dateTime, diagnosis, treatment);

            ArrayListADT<MedicalHistory> histories = historyMap.get(patientId);
            if (histories == null) {
                histories = new ArrayListADT<>();
                historyMap.put(patientId, histories);
            }
            histories.add(mh);
        }

    } catch (IOException e) {
        System.out.println("Error loading histories: " + e.getMessage());
    }
}

    // Get histories by patient
    public ArrayListADT<MedicalHistory> getHistoryByPatientId(String patientId) {
        ArrayListADT<MedicalHistory> histories = historyMap.get(patientId);
        return histories != null ? histories : new ArrayListADT<>();
    }

    // Print histories as report
    public static void printHistories(ArrayListADT<MedicalHistory> histories) {
     
        if (histories == null || histories.size() == 0) {
         //   System.out.println("No medical history found.");
            return;
        }

        String format = "| %-12s | %-20s | %-20s | %-20s |%n";
        String line = "+--------------+----------------------+----------------------+----------------------+";

        System.out.println(line);
        System.out.printf(format, "Patient ID", "Date & Time", "Diagnosis", "Treatment");
        System.out.println(line);

        for (MedicalHistory h : histories) {
            System.out.printf(format,
                    h.getPatient() != null ? h.getPatient().getID() : "",
                    h.getDateTime(),
                    h.getDiagnosis(),
                    h.getTreatment());
        }
        System.out.println(line);
    }
}
