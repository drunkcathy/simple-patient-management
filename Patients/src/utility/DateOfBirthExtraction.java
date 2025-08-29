package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateOfBirthExtraction {

    private final String ic;
    public DateOfBirthExtraction(String ic){
        this.ic=ic;
    }
      public LocalDate getDOB()     { return extractDOB(ic); }
    public String getGender()     { return extractGender(ic); }
    public String getState()      { return extractState(ic); }
    public int getAge()           { return extractAge(ic); }

    public static LocalDate extractDOB(String ic) {
        
        String dobPart = ic.substring(0, 6); // first 6 digits
        int year = Integer.parseInt(dobPart.substring(0, 2));
        int month = Integer.parseInt(dobPart.substring(2, 4));
        int day = Integer.parseInt(dobPart.substring(4, 6));

        year += (year >= 0 && year <= 25) ? 2000 : 1900;

        return LocalDate.of(year, month, day);
    }

    
    public static String extractGender(String ic) {
        int genderDigit = Character.getNumericValue(ic.charAt(11));
        return (genderDigit % 2 == 0) ? "Female" : "Male";
    }

    public static String extractState(String ic) {
        String stateCode = ic.substring(6, 8);
        // Simple mapping, you can extend
        switch (stateCode) {
            case "01": return "Johor";
            case "02": return "Kedah";
            case "03": return "Kelantan";
            case "04": return "Melaka";
            case "05": return "Negeri Sembilan";
            case "06": return "Pahang";
            case "07": return "Penang";
            case "08": return "Perak";
            case "09": return "Perlis";
            case "10": return "Selangor";
            case "11": return "Terengganu";
            case "12": return "Sabah";
            case "13": return "Sarawak";
            case "14": return "KL";
            case "15": return "Labuan";
            case "16": return "Putrajaya";
            default: return "Unknown";
        }
    }

    // Extract age
    public static int extractAge(String ic) {
        LocalDate dob = extractDOB(ic);
        return LocalDate.now().getYear() - dob.getYear();
    }
}
