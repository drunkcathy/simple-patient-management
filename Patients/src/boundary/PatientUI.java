package boundary;

import dao.PatientManagement;
import entity.Patient;
import utility.DateOfBirthExtraction;
import utility.IDGenerator;

import java.time.LocalDate;
import java.util.Scanner;

public class PatientUI {

    private final PatientManagement pm;
    private final Scanner sc;
    private final IDGenerator idGen;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";


    public PatientUI() {
   
        this.pm = new PatientManagement();
        this.sc = new Scanner(System.in);
        this.idGen = new IDGenerator("P-", 1000, 9999); //start with mac num =999;//
    }

    public void start() {
        
        int choice;
        do {
            System.out.println("\n===== PATIENT MANAGEMENT MENU =====");
            System.out.println("1. Register Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Search Patient by ID/Phone");
            System.out.println("4. Update Patient");
            System.out.println("5. Remove Patient");
            System.out.println("6. View Waiting Queue");
            System.out.println("7. Serve Next Patient");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            while (!sc.hasNextInt()) {
                System.out.println(BLUE+"Please enter a number."+ANSI_RESET);
                sc.next();
            }
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> registerPatient();
                case 2 -> pm.printPatientsTable();
                case 3 -> searchPatient();
                case 4 -> updatePatient();
                case 5 -> removePatient();
                case 6 -> pm.displayQueue();
                case 7 -> {
                   Patient nextPatient = pm.serveNextPatient();
        if (nextPatient != null) {
            System.out.println("Now serving: " + nextPatient.getName() + " (" + nextPatient.getID() + ")");
        } else {
            System.out.println("Queue is empty.");
        }
    }
          
                case 8 -> System.out.println("Existing....");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 8);
    }

    void registerPatient() {
        boolean addMore;
    do {
        addMore = false;
        
        System.out.print(" Enter Name: ");
        String name = sc.nextLine().toUpperCase();

String ic; 
while (true) {
    System.out.print("IC No (12 digits, no spaces): ");
    ic = sc.nextLine().trim();

    // Basic format check
    if (ic.length() < 12) {
        System.out.println(ANSI_RED + "Invalid IC format. Must be at least 12 digits." + ANSI_RESET);
        continue;
    }

    // Duplicate IC check
    if (pm.isICExists(ic)) {
        System.out.println(ANSI_RED + "This IC number is already registered to another patient." + ANSI_RESET);
        continue;
    }

    break; 
}
        LocalDate dob = DateOfBirthExtraction.extractDOB(ic);
        String genderStr = DateOfBirthExtraction.extractGender(ic);
        String stateStr = DateOfBirthExtraction.extractState(ic);
        int age = DateOfBirthExtraction.extractAge(ic);

        String lastId = pm.getLastID();
int lastNum = 0;

if (lastId != null) {
    lastNum = Integer.parseInt(lastId.replaceAll("\\D", "")); 
}

IDGenerator idGen = new IDGenerator("P-", lastNum + 1, 9999);

// Generate
String patientID = idGen.generateID();
System.out.println("Generated Patient ID: " + patientID);

String phone;
while (true) {
    System.out.print("Phone Number : ");
    phone = sc.nextLine().trim();

    if (!phone.matches("^(\\d{10,11}|\\d{3}-\\d{7,8}|\\d{3}-\\d{3}-\\d{4})$")) {
        System.out.println(ANSI_RED + "Wrong format. Must be 10 or 11 digits, dashes optional." + ANSI_RESET);
        continue;
    }

    if (pm.getPatient(phone) != null) {
        System.out.println(ANSI_RED + "This phone number is already registered to another patient." + ANSI_RESET);
        continue;
    }
    // Passed both checks
    break;
}
        String bloodType;
        while(true){
            System.out.print("Blood Type: ");
        bloodType = sc.nextLine();
        if(!bloodType.matches("^(A|B|AB|O)[+-]$")){
            System.out.println(ANSI_RED +" Enter again ( A,B,O,AB ) !"+ANSI_RESET );
            continue;
        }
        break;
        }
        
        String email;
while(true){
    System.out.print("Email: ");
    email = sc.nextLine().trim();
    
    if(!email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")) {
        System.out.println(ANSI_RED +"Enter again XXXXXXX@domain.com !" + ANSI_RESET );
        continue;
    }

    if(pm.isEmailExists(email)) {
        System.out.println(ANSI_RED + "This email is already registered to another patient." + ANSI_RESET);
        continue;
    }

    break;
}

        System.out.print("Medical Sickness: ");
        String medHistory = sc.nextLine().toUpperCase();
        
        //lower = higher priority
        int priority;
while (true) {
    System.out.print("Priority (1-Emergency , 2-Elderly , 3-Normal): ");
    if (sc.hasNextInt()) {
        priority = sc.nextInt();
        sc.nextLine(); 
        if (priority == 1 || priority == 2 || priority ==3) {
            break; // valid
        }
    } else {
        sc.nextLine(); // clear invalid input
    }
    System.out.println(ANSI_RED +"Enter again (1 or 2 or 3 only) !"+ANSI_RESET);
}
        // Create Patient object
        Patient p = new Patient();
        p.setName(name);
        p.setIdentityCard(ic);
        p.setAge(age);
        p.setDateOfBirth(dob);
        p.setGender(genderStr.charAt(0));
        p.setState(stateStr);
        p.setID(patientID);
        p.setPhoneNumber(phone);
        p.setBloodType(bloodType);
        p.setEmail(email);
        p.setMedicalHistory(medHistory);
        
        String priorityStr = (priority == 1) ? "Emergency" : 
                     (priority == 2) ? "Elderly" : "Normal";
p.setPriority(priorityStr);
        
        p.setStatus("active");
        p.setRegistrationTime(java.time.LocalDateTime.now());

        if (pm.registerPatient(p)) {
            System.out.println(BLUE+" \n                  Patient registered successfully.                "+ANSI_RESET);
       
         System.out.print("Do you want to add another patient? (Y/N): ");
            String again = sc.nextLine().trim();
            if (again.equalsIgnoreCase("Y")) {
                addMore = true;
            }
        } else {
            System.out.println(ANSI_RED+"Patient with same ID, IC, phone, or email already exists."+ANSI_RESET);
        }

    } while (addMore);
 }

void searchPatient() {
    System.out.print("Enter Patient ID or Phone: ");
    String key = sc.nextLine();
    
    Patient p = pm.getPatient(key);

    if (p != null) {
        printPatientTable(p);
    } else {
        System.out.println(ANSI_RED + "Patient not found." + ANSI_RESET);
    }
}

private void printPatientTable(Patient p) {
    String border = "+--------------------+------------------------------------------+";
    System.out.println(BLUE + border + ANSI_RESET);
    System.out.printf(BLUE + "| %-18s | %-40s |\n" + ANSI_RESET, "d e t a i l s", "v a l u e");
    System.out.println(BLUE + border + ANSI_RESET);

    printRow("Name", p.getName());
    printRow("ID", p.getID());
    printRow("Age", String.valueOf(p.getAge()));
    printRow("IC No", p.getIdentityCard());
    printRow("Date of Birth", String.valueOf(p.getDateOfBirth()));
    printRow("Phone", p.getPhoneNumber());
    printRow("Blood Type", p.getBloodType());
    printRow("Gender", String.valueOf(p.getGender()));
    printRow("Email", p.getEmail());
    printRow("Medical History", p.getMedicalHistory());
    printRow("Priority", p.getPriority());
    printRow("Status", p.getStatus());
    printRow("State", p.getState());
    printRow("Reg Time", String.valueOf(p.getRegistrationTime()));

    System.out.println(BLUE + border + ANSI_RESET);
}

private void printRow(String field, String value) {

    if (value == null) value = "";
    if (value.length() > 40) {
        value = value.substring(0, 37) + "...";
    }
    System.out.printf("| %-18s | %-40s |\n", field, value);
}


    void updatePatient() {
        System.out.print("Enter ID of patient to update: ");
        String id = sc.nextLine();
        
        Patient existing = pm.getPatient(id);
        if (existing == null) {
            System.out.println(ANSI_RED+"Patient not found."+ANSI_RESET);
            return;
        }
        System.out.println("Leave a field blank to keep current value.");

        System.out.print("Name (" + existing.getName() + "): ".toUpperCase());
        String name = sc.nextLine();
        if (!name.isEmpty()) existing.setName(name);

        System.out.print("Phone (" + existing.getPhoneNumber() + "): ");
        String phone = sc.nextLine();
        if (!phone.isEmpty()) existing.setPhoneNumber(phone);

        System.out.print("Email (" + existing.getEmail() + "): ");
        String email = sc.nextLine();
        if (!email.isEmpty()) existing.setEmail(email);

        pm.updatePatient(existing);
        System.out.println(BLUE+"Patient updated."+ANSI_RESET);
    }

    void removePatient() {
        System.out.print("Enter Patient ID or Phone to remove: ");
        
        
        String key = sc.nextLine();
            String removeDashes = key.replace("[^0-9]", "");

        if (pm.removePatient(key)) {
            System.out.println(BLUE+"Patient removed."+ANSI_RESET);
        } else {
            System.out.println("Patient not found.");
        }
    }

   

}
