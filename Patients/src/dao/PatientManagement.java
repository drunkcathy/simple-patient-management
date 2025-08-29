package dao;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import entity.Patient;
import adt.ArrayListADT;
import adt.HashMapADT;
import adt.PriorityQueueADT;
import adt.QueueADT;
import adt.TreeMapADT;
import java.time.ZoneOffset;
import utility.PatientPriorityComparator;

public class PatientManagement {

    //COLOR TEXTT
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m ";
              public static final String CYAN = "\\u001B[36m";
              
    private ArrayListADT<Patient> patients;
    private QueueADT<Patient> waitingQueue;
    private TreeMapADT<Long,Patient>arrivalMap=new TreeMapADT<>();
    private PriorityQueueADT<Patient> serviceQueue=new PriorityQueueADT(new PatientPriorityComparator());
    private final String fileName;

    
   public PatientManagement(String fileName) {
    this.fileName = fileName;
    patients = new ArrayListADT<>();
    waitingQueue = new QueueADT<>();
    loadPatients();

    // Populate waitingQueue and serviceQueue
    for (Patient p : patients) {
        addPatientToQueues(p);
    }
}

private void addPatientToQueues(Patient p) {
    if (p.getRegistrationTime() == null)
        p.setRegistrationTime(LocalDateTime.now());

    long epoch = p.getRegistrationTime().toEpochSecond(ZoneOffset.UTC);
    arrivalMap.put(epoch, p);
    waitingQueue.offer(p);
    serviceQueue.offer(p); // for priority
}
public void displayQueue() {
    if (arrivalMap.isEmpty()) {
        System.out.println("Patients waiting for consultation: (none)");
        return;
    }

    System.out.println("Patients waiting for consultation:");
    System.out.printf("%-4s %-15s %-25s %-20s %-10s%n", "No.", "ID", "Name", "Arrival Time", "Priority");
    System.out.println("---------------------------------------------------------------------");

    int i = 1;
    for (Patient p : arrivalMap.values()) {
        String arrival = p.getRegistrationTime() != null ? p.getRegistrationTime().toString() : "N/A";
        System.out.printf("%-4d %-15s %-25s %-20s %-10s%n", i++, p.getID(), p.getName(), arrival, p.getPriority());
    }
}

    public PatientManagement() {
        this("patients.txt");
    }

    public ArrayListADT<Patient> getAllPatients() {
        return patients;
    }

    public boolean registerPatient(Patient patient) {
        if (findByIdOrPhone(patient.getID()) != null) {
            return false;
        }
        patient.setRegistrationTime(LocalDateTime.now());
        patients.add(patient);
        waitingQueue.offer(patient);
        savePatients();
        return true;
    }
    
    public void loadQueueFromArrivalMap() {
    waitingQueue.clear();
    for (Patient p : arrivalMap.values()) {
        waitingQueue.offer(p);
    }
}


    private void loadPatients() {
    File file = new File(fileName);
    if (!file.exists()) {
        System.out.println("No patient file found. Starting fresh.");
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("+") || line.startsWith("| Name") || line.startsWith("-")) continue;
            if (!line.startsWith("|")) continue;

            line = line.substring(1, line.length() - 1); 

            String[] parts = line.split("\\|", -1); 

            try {
                Patient patient = new Patient();
                patient.setName(parts.length > 0 ? parts[0].trim() : "");
                patient.setIdentityCard(parts.length > 1 ? parts[1].trim() : "");
                patient.setAge(parts.length > 2 ? Integer.parseInt(parts[2].trim()) : 0);
                patient.setDateOfBirth(parts.length > 3 ? LocalDate.parse(parts[3].trim()) : null);
                patient.setState(parts.length > 4 ? parts[4].trim() : "");
                patient.setID(parts.length > 5 ? parts[5].trim() : "");
                patient.setPhoneNumber(parts.length > 6 ? parts[6].trim() : "");
                patient.setBloodType(parts.length > 7 ? parts[7].trim() : "");
                patient.setGender(parts.length > 8 && !parts[8].trim().isEmpty() ? parts[8].trim().charAt(0) : 'U');
                patient.setEmail(parts.length > 9 ? parts[9].trim() : "");
                patient.setMedicalHistory(parts.length > 10 ? parts[10].trim() : "");
                patient.setStatus(parts.length > 11 ? parts[11].trim() : "active");
                patient.setPriority(parts.length > 12 ? parts[12].trim() : "");
                if (parts.length > 13 && !parts[13].trim().isEmpty()) {
                    patient.setRegistrationTime(LocalDateTime.parse(parts[13].trim()));
                }

                patients.add(patient);

            } catch (Exception e) {
                System.err.println("Skipping invalid line: " + line);
            }
        }
    } catch (IOException ioe) {
        System.err.println("Error loading patients: " + ioe.getMessage());
    }
}

    private void savePatients() {
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            String headerFormat = "| %-25s | %-15s | %-3s | %-12s | %-18s | %-6s | %-12s | %-9s | %-6s | %-30s | %-23s | %-7s | %-10s | %-20s |%n";
            String dataFormat   = "| %-25s | %-15s | %-3d | %-12s | %-18s | %-6s | %-12s | %-9s | %-6s | %-30s | %-23s | %-7s | %-10s | %-20s |%n";
            String line = "+----------------------+-----------------+-----+--------------+--------------------------+--------+--------------+-----------+--------+------------------------------------+----------------------+---------+------------+---------------------+";

            // Top border
            bw.write(line);
            bw.newLine();

            // Header row
            bw.write(String.format(headerFormat,
                    "Name", "IC NO", "Age", "D.O.B", "State", "ID", "Phone",
                    "BloodType", "Gender", "Email", "Medical History",
                    "Status", "Priority", "Reg Time"));
            bw.newLine(); 
            bw.write(line);
            bw.newLine();

            // Data rows
        for (Patient p : patients) {
    

    bw.write(String.format(dataFormat,
            p.getName(),
            p.getIdentityCard(),
            p.getAge(),
            p.getDateOfBirth(),
            p.getState(),
            p.getID(),
            p.getPhoneNumber(),
            p.getBloodType(),
            p.getGender(),
            p.getEmail(),
            p.getMedicalHistory(),             
            p.getStatus(),
            p.getPriority(),
            p.getRegistrationTime() != null ? p.getRegistrationTime().toString() : ""
    ));
}
            // Bbottom border of the system
            bw.write(line);
            bw.newLine();

        } catch (IOException ioe) {
            System.err.println("Error saving patients: " + ioe.getMessage());
        }
    }

    public void printPatientsTable() {
    String headerFormat = "| %-25s | %-15s | %-3s | %-12s | %-18s | %-6s | %-12s | %-9s | %-6s | %-28s | %-23s | %-7s | %-10s | %-20s |%n";
    String dataFormat   = "| %-25s | %-15s | %-3d | %-12s | %-18s | %-6s | %-12s | %-9s | %-6s | %-28s | %-23s | %-7s | %-10s | %-20s |%n";
    String line = "+----------------------+-----------------+-----+--------------+------------------+--------+--------------+-----------+--------+-----------------------------------+----------------------+---------+------------+---------------------+";

    System.out.println(line);
    System.out.printf(headerFormat,
            "Name", "IC NO", "Age", "D.O.B", "State", "ID", "Phone",
            "BloodType", "Gender", "Email", "Medical Problem",
            "Status", "Priority", "Reg Time");
    System.out.println(line);

    for (Patient p : patients) {
        System.out.printf(dataFormat,
                p.getName(),
                p.getIdentityCard(),
                p.getAge(),
                p.getDateOfBirth(),
                p.getState(),
                p.getID(),
                p.getPhoneNumber(),
                p.getBloodType(),
                p.getGender(),
                p.getEmail(),
                p.getMedicalHistory(),
                p.getStatus(),
                p.getPriority(),
                p.getRegistrationTime() != null ? p.getRegistrationTime().toString() : "");
    }
    System.out.println(line);
}


    public Patient getPatient(String patientId) {
        return findByIdOrPhone(patientId);
    }
    
    public Patient findByIdOrPhone(String key) {
    if (key == null) return null;

    String trimmedKey = key.trim();

    for (Patient p : patients) {
        // for phone to remove the dashes and all stuff
        String phone = p.getPhoneNumber().replaceAll("[^0-9]", "");
        String normalizedKeyPhone = trimmedKey.replaceAll("[^0-9]", "");

        // IID keep dash
        if (p.getID().equalsIgnoreCase(trimmedKey)) {
            return p;
        }

        // normal comparizon
        if (!normalizedKeyPhone.isEmpty() && phone.equals(normalizedKeyPhone)) {
            return p;
        }
    }
    return null;
}

  
  public void addToQueue(String patientId){
      Patient patient = findByIdOrPhone(patientId);
      if(patient!=null){
          waitingQueue.offer(patient);
      }
  }

  public Patient serveNextPatient(){
      if(serviceQueue.isEmpty())return null;
      Patient p= serviceQueue.poll();
      long epoch =p.getRegistrationTime().toEpochSecond(ZoneOffset.UTC);
      arrivalMap.remove(epoch);
      waitingQueue.remove(p);
      return p;
  }
    //check for duplicates
 public boolean isICExists(String ic) {
    for (Patient p : patients) {
        if (p.getIdentityCard().equalsIgnoreCase(ic)) {
            return true;
        }
    }
    return false;
}

public boolean isEmailExists(String email) {
    for (Patient p : patients) {
        if (p.getEmail().equalsIgnoreCase(email)) {
            return true;
        }
    }
    return false;
}


    public void updatePatient(Patient updatedPatient) {
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            if (p.getID().equalsIgnoreCase(updatedPatient.getID())) {
                patients.set(i, updatedPatient);
                savePatients();
                return;
            }
        }
    }

    public boolean removePatient(String key) {
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            if (p.getID().equalsIgnoreCase(key) ||
                p.getPhoneNumber().equalsIgnoreCase(key)) {
                patients.remove(i);
                savePatients();
                return true;
            }
        }
        return false;
    }

    public HashMapADT<String, Patient> getPatientsMap() {
        HashMapADT<String, Patient> map = new HashMapADT<>(1000);
        for (Patient p : patients) {
            map.put(p.getID(), p);
        }
        return map;
    }
    
 public String getLastID() {
    String lastID = null;
    int maxNum = 0;

    try (Scanner scFile = new Scanner(new File(fileName))) {
        while (scFile.hasNextLine()) {
            String line = scFile.nextLine().trim();

            // skip empty lines, borders, and header
            if (line.isEmpty()) continue;
            if (line.startsWith("+")) continue;
            if (line.startsWith("| Name")) continue;
            if (!line.startsWith("|")) continue;

            line = line.substring(1, line.length() - 1);
            String[] parts = line.split("\\|");
            if (parts.length < 6) continue;

            String id = parts[5].trim(); 
            if (id.matches("[A-Za-z]-?\\d+")) {
                int num = Integer.parseInt(id.replaceAll("\\D", ""));
                if (num > maxNum) {
                    maxNum = num;
                    lastID = id;
                }
            }
        }
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + e.getMessage());
    }

    return lastID;
}

}
