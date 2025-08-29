package boundary;

import adt.HashMapADT;
import static boundary.MenuUI.PatientBanner.printBanner;
import dao.AppointmentManagement;
import dao.PatientManagement;
import entity.Patient;
import adt.HashMapADT;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class MenuUI {

    //title 
      public static final String YELLOW = "\u001B[33m";
          public static final String RESET = "\u001B[0m";
    
    public static final String RED= "\\u001B[31m";
  
          
    PatientManagement pm;
    AppointmentManagement am;
    HashMapADT<String, Patient> patients;
    AppointmentUI a;
    PatientUI p;
    QueueUI q;
    MedicalHistoryUI mh;

    Scanner sc = new Scanner(System.in);

    // Constructor
    public MenuUI() {
        pm = new PatientManagement();
        patients = pm.getPatientsMap();
        am = new AppointmentManagement(patients);
        a = new AppointmentUI(am, patients);
        p = new PatientUI();
        q = new QueueUI();
        mh = new MedicalHistoryUI(am);
    }

    public void printMenu() {
        printBanner("PATIENT");
        int option;
        do {
            System.out.println("===========================================================");
            System.out.println("                      MENU                                 ");
            System.out.println("===========================================================");
            System.out.println("1. Patients Module");
            System.out.println("2. Queue Management");
            System.out.println("3. Appointment Management");
            System.out.println("4. Medical History Management");
            System.out.println("0. Exit");
            System.out.print(YELLOW +"Enter your option: "+ RESET);

            while (!sc.hasNextInt()) {
                System.out.println("Please enter a number.");
                sc.next();
            }
            option = sc.nextInt();
            sc.nextLine(); 

            switch (option) {
    case 1 -> p.start();  
    case 2 -> q.start();
    case 3 -> a.start(); 
    case 4 -> mh.start();
    case 0 -> System.out.println("Exiting system...");
    default -> System.out.println("Invalid option. Try again.");
}

            
        } while (option != 0);
    }
    

public class PatientBanner {
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String[] COLORS = {
        "\u001B[31m", // Red
        "\u001B[33m", // Yellow
        "\u001B[32m", // Green
        "\u001B[36m", // Cyan
        "\u001B[34m", // Blue
        "\u001B[35m", // Magenta
        "\u001B[97m"  // Bright White
    };

    // Now using your own custom HashMapADT
    private static final HashMapADT<Character, String[]> LETTERS = new HashMapADT<>(100);

    static {
        LETTERS.put('P', new String[]{
            "███████ ",
            "██   ██ ",
            "██   ██ ",
            "██████  ",
            "██      ",
            "██      ",
            "██      "
        });
        LETTERS.put('A', new String[]{
            " █████  ",
            "██   ██ ",
            "██   ██ ",
            "███████ ",
            "██   ██ ",
            "██   ██ ",
            "██   ██ "
        });
        LETTERS.put('T', new String[]{
            "████████",
            "   ██   ",
            "   ██   ",
            "   ██   ",
            "   ██   ",
            "   ██   ",
            "   ██   "
        });
        LETTERS.put('I', new String[]{
            "████████",
            "   ██   ",
            "   ██   ",
            "   ██   ",
            "   ██   ",
            "   ██   ",
            "████████"
        });
        LETTERS.put('E', new String[]{
            "████████",
            "██      ",
            "██      ",
            "██████  ",
            "██      ",
            "██      ",
            "████████"
        });
        LETTERS.put('N', new String[]{
            "██   ██ ",
            "███  ██ ",
            "████ ██ ",
            "██ ███  ",
            "██  ███ ",
            "██   ██ ",
            "██   ██ "
        });
        // reuse T for last letter
    }

    public static void printBanner(String word) {
        String upper = word.toUpperCase(Locale.ROOT);
        int rows = 7;

        for (int r = 0; r < rows; r++) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < upper.length(); i++) {
                char ch = upper.charAt(i);
                String[] glyph = LETTERS.get(ch); // using our HashMapADT get()
                if (glyph == null) continue;
                String color = COLORS[i % COLORS.length];
                line.append(color).append(BOLD).append(glyph[r]).append(RESET).append("  ");
            }
            System.out.println(line);
        }
    }
}


}


