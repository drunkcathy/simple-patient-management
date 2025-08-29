package dao;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import entity.Appointment;
import entity.Patient;
import adt.HashMapADT;
import utility.AppointmentIDGenerator;
import dao.QueueManagement;
import dao.MedicalHistoryManagement;
import entity.MedicalHistory;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import javax.mail.Transport;   

public class AppointmentManagement {

    private Scanner scanner = new Scanner(System.in);

        private HashMapADT<String, Patient> patients = new HashMapADT<>(16);
    private HashMapADT<String, Appointment> appointments = new HashMapADT<>(16);
    
    private static final String APPOINTMENT_FILE = "appointments.txt";
    MedicalHistoryManagement mhm = new MedicalHistoryManagement ();

    String rescheduledFrom = "N/A";
    private Appointment appointment;
    private String rescheduleLink;
    private static Map<String, String> cancelTokens = new HashMap<>();

 

    public AppointmentManagement(HashMapADT<String, Patient> patientsMap) {
        if (patientsMap != null) {
            this.patients = patientsMap;
        }
    }
    
    public void createFiles() {
        try {
            File file = new File(APPOINTMENT_FILE);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public adt.ArrayListADT<Appointment> getAllAppointments() {
    adt.ArrayListADT<Appointment> list = new adt.ArrayListADT<>();
    for (Appointment appt : appointments.values()) { 
        list.add(appt);
    }
    return list;
}


    
    public LocalDateTime calculateNextCheckUp(MedicalHistory history) {
    String diagnosis = history.getDiagnosis().toLowerCase();
    
    if (diagnosis.contains("cancer") || diagnosis.contains("heart attack") || diagnosis.contains("kidney")) {
       
        return LocalDateTime.now().plusDays(7);
    } else if (diagnosis.contains("fever") || diagnosis.contains("cold")) {
  
        return LocalDateTime.now().plusDays(30);
    } else if(diagnosis.contains("cough")){
        return LocalDateTime.now().plusDays(14);
    }else {
            System.out.println("No checked up needed !");
            return null;
    }
}

    public void loadAppointments() {
    Path path = Paths.get(APPOINTMENT_FILE);
    if (!Files.exists(path)) return;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    try {
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            line = line.trim();
            if (!line.startsWith("|") || line.contains("Appointment ID") || line.startsWith("+")) {
                continue; // skip separators and headers
            }

            // Split by '|' and trim
            String[] parts = line.split("\\|");
            if (parts.length < 7) continue;

            String appointmentId = parts[1].trim();
            String patientName = parts[2].trim();
            LocalDateTime appointmentTime = LocalDateTime.parse(parts[3].trim(), dtf);
            String room = parts[4].trim();
            String reason = parts[5].trim();
            String nextCheckStr = parts[6].trim();
            LocalDateTime nextCheckUp = nextCheckStr.equals("N/A") ? null : LocalDateTime.parse(nextCheckStr, dtf);
            String rescheduledFrom = parts.length > 7 ? parts[7].trim() : "N/A";

            // Find patient in map
            Patient patient = null;
            for (Patient p : patients.values()) {
                if (p.getName().equals(patientName)) {
                    patient = p;
                    break;
                }
            }
            if (patient == null) continue;

            // Create Appointment object
            Appointment appointment = new Appointment(patient, appointmentId, appointmentTime, room, reason, nextCheckUp, rescheduledFrom);

            // Put into map
            appointments.put(appointmentId, appointment);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    public void generateRoomNumber(){
        int roomNum = (int)(Math.random()*4)+1;
    }
    
    public String reasonToCheck() {
        System.out.print("Enter reason for appointment / illness: ");
        return scanner.nextLine().trim();
    }
    
   public LocalDateTime calculateNextCheckUp(String diagnosis) {
    if (diagnosis == null) return null;

    diagnosis = diagnosis.toLowerCase();

    if (diagnosis.contains("cancer") || diagnosis.contains("heart") || diagnosis.contains("kidney")) {
        // Critical conditions: follow-up in 7 days
        return LocalDateTime.now().plusDays(7);
    } 
    else if (diagnosis.contains("flu") || diagnosis.contains("cold")) {
        // Moderate conditions - follow-up in 30 days
        return LocalDateTime.now().plusDays(30);
    } 
    else if (diagnosis.contains("checkup") || diagnosis.contains("review")) {
        // General review: follow-up in 14 days
        return LocalDateTime.now().plusDays(14);
    } 
    else {
        // No follow-up required
        return null;
    }
}

    
    public void bookAppointment() {
    System.out.print("Enter patient ID: ");
    String patientID = scanner.nextLine().trim();
    Patient patient = patients.get(patientID);

    if (patient == null) {
        System.out.println("Patient not found!");
        return;
    }

    System.out.print("Enter reason for appointment / illness: ");
    String reason = scanner.nextLine().trim();

   // Current date/time
LocalDateTime now = LocalDateTime.now();
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

LocalDateTime appointmentTime;

while (true) {
    System.out.println("Enter appointment date and time (yyyy-MM-dd HH:mm):");
    String input = scanner.nextLine().trim();

    try {

        appointmentTime = LocalDateTime.parse(input, formatter);
    } catch (DateTimeParseException e) {
        System.out.println("Invalid date/time format! Example: 2025-09-01 14:30");
        continue; 
    }

    if (appointmentTime.isBefore(now)) {
        System.out.println("Appointment cannot be in the past. Current time: " + now.format(formatter));
        continue; // ask again
    }

    break;
}

   LocalDateTime nextCheckUp = calculateNextCheckUp(reason);

    String appointmentID = AppointmentIDGenerator.generateID();
    String room = "Room" + (int)(Math.random() * 4 + 1);

  Appointment appointment = new Appointment(patient, appointmentID, appointmentTime, room, reason, nextCheckUp, rescheduledFrom);

    appointments.put(appointmentID, appointment);

try (FileWriter writer = new FileWriter(APPOINTMENT_FILE, true)) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    String nextCheck = (nextCheckUp != null) ? nextCheckUp.format(dtf) : "N/A";

    File file = new File(APPOINTMENT_FILE);
if (file.length() == 0) {
    writer.write("+----------------+----------------------------+----------------------------+--------+----------------------+----------------+----------------+\n");
    writer.write("| Appointment ID |       Name                 |        Appointment Time    | Room   | Reason               | Next Check-Up   | Rescheduled From |\n");
    writer.write("+----------------+----------------------------+----------------------------+--------+----------------------+----------------+----------------+\n");
}
writer.write(String.format("| %-14s | %-25s | %-30s | %-6s | %-18s | %-14s | %-16s |\n",
    appointmentID,                      // Appointment ID
    patient.getName(),                  // Name
    appointmentTime.format(dtf),        // Appointment Time
    room,                               // Room
    reason,                             // Reason
    (nextCheckUp != null) ? nextCheckUp.format(dtf) : "N/A", // Next Check-Up
    (appointment.getRescheduledFrom() != null) ? appointment.getRescheduledFrom() : "N/A" // Rescheduled From
));


writer.write("+----------------+----------------------------+----------------------------+--------+----------------------+----------------+----------------+\n");


    System.out.println("Appointment booked successfully: " + appointmentID);

} catch (IOException e) {
    System.out.println("Error writing to file: " + e.getMessage());
}

   // generate tokens
String cancelToken = UUID.randomUUID().toString();
cancelTokens.put(cancelToken, appointmentID);
String cancelLink = "http://127.0.0.1:8081/cancel?appointmentId=" + appointmentID + "&token=" + cancelToken;

String rescheduleToken = UUID.randomUUID().toString();
    cancelTokens.put(rescheduleToken, appointmentID);
    String rescheduleLink = "http://127.0.0.1:8081/reschedule?appointmentId=" + appointmentID + "&token=" + rescheduleToken;

sendAppointmentEmail(patient, appointment, cancelLink, rescheduleLink);
    }


    public void sendAppointmentEmail(Patient patient, Appointment appointment, String cancelLink, String rescheduleLink) {
    final String fromEmail = "catherinely-pm23@student.tarc.edu.my";
    final String password = "uvxx swny lakl fqcj";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(fromEmail, password);
        }
    });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(patient.getEmail()));
        message.setSubject("Appointment Confirmation");

        String body = "Hi " + patient.getName() + ", your appointment is booked on " +
                appointment.getAppointmentTime() +
                "\n\nClick to cancel: " + cancelLink +
                "\nClick to reschedule: " + rescheduleLink;

        message.setText(body);  // important
        Transport.send(message);
        System.out.println("Confirmation email sent to " + patient.getEmail());

    } catch (MessagingException e) {
        e.printStackTrace();
    }
}


    public static class CancelHandler implements HttpHandler {
        private static final Object fileLock = new Object();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String appointmentId = params.get("appointmentId");
            String token = params.get("token");

            String response;

            synchronized (fileLock) {
                if (token != null && cancelTokens.containsKey(token)
                        && cancelTokens.get(token).equals(appointmentId)) {
                    boolean removed = removeAppointmentFromFile(appointmentId);
                    response = removed ? "Appointment " + appointmentId + " has been cancelled successfully!"
                            : "Appointment not found!";
                    cancelTokens.remove(token);
                } else {
                    response = "Invalid or expired token!";
                }

                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }

        private static Map<String, String> queryToMap(String query) {
            Map<String, String> map = new HashMap<>();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) map.put(pair[0], pair[1]);
                }
            }
            return map;
        }

        private static boolean removeAppointmentFromFile(String appointmentId) throws IOException {
    Path path = Paths.get(APPOINTMENT_FILE);
    if (!Files.exists(path)) return false;

    List<String> lines = Files.readAllLines(path);

      boolean removed = lines.removeIf(line -> line.contains("| " + appointmentId + " "));
    
    Files.write(path, lines);
    return removed;
}

    }

    public void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/cancel", new CancelHandler());
          server.createContext("/reschedule", new RescheduleHandler()); 
        server.setExecutor(null);
        server.start();
      //  System.out.println("Server running at http://127.0.0.1:8081/cancel");
    }
    
    public void checkAndQueueAppointments(PatientManagement pm) {
    LocalDateTime now = LocalDateTime.now();

    for (Appointment appt : appointments.values()) {
        if (appt.getAppointmentTime().isBefore(now.plusMinutes(5))) {
            Patient patient = appt.getPatient();
            pm.addToQueue(patient.getID());
            System.out.println("Appointment patient " + patient.getName() + " added to queue.");
        }
    }
}

    //test
public static class RescheduleHandler implements HttpHandler {
    private static final Object fileLock = new Object();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
        String appointmentId = params.get("appointmentId");
        String token = params.get("token");
        String response;

        synchronized (fileLock) {
            if (token != null && cancelTokens.containsKey(token)
                    && cancelTokens.get(token).equals(appointmentId)) {

                try {
                    // Get current appointment time from file
                    LocalDateTime currentTime = getAppointmentTimeFromFile(appointmentId);
                    if (currentTime == null) {
                        response = "Appointment not found!";
                    } else {
                        // Generate random future time (1â€“30 days and 0â€“59 minutes after current)
                        Random rand = new Random();
                        LocalDateTime newTime = currentTime.plusDays(rand.nextInt(30) + 1)
                                                          .plusMinutes(rand.nextInt(60));

                        boolean updated = updateAppointmentTimeInFile(appointmentId, newTime);
                        response = updated ? "Appointment rescheduled successfully to "
                                           + newTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                           : "Appointment not found!";
                    }
                } catch (Exception e) {
                    response = "Error rescheduling appointment!";
                    e.printStackTrace();
                }

                cancelTokens.remove(token);
            } else {
                response = "Invalid or expired token!";
            }

            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // Utility to read the current appointment time from file
    private static LocalDateTime getAppointmentTimeFromFile(String appointmentId) throws IOException {
        Path path = Paths.get(APPOINTMENT_FILE);
        if (!Files.exists(path)) return null;

        List<String> lines = Files.readAllLines(path);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (String line : lines) {
            if (line.contains(appointmentId)) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    return LocalDateTime.parse(parts[3].trim(), dtf);
                }
            }
        }
        return null;
    }
}

private static boolean updateAppointmentTimeInFile(String appointmentId, LocalDateTime newTime) throws IOException {
    Path path = Paths.get(APPOINTMENT_FILE);
    if (!Files.exists(path)) return false;

    List<String> lines = Files.readAllLines(path);
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    boolean updated = false;

    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains(appointmentId)) {
            String[] parts = line.split("\\|");

            // Ensure array has 7 columns for consistency
            if (parts.length < 8) { 
                parts = Arrays.copyOf(parts, 8);
            }

               parts[7] = " " + parts[3].trim() + " ";

            // Update appointment time column
            parts[3] = " " + newTime.format(dtf) + " ";
StringBuilder sb = new StringBuilder();
            for (String p : parts) sb.append("|").append(p);
            sb.append("|");
            lines.set(i, sb.toString());
            updated = true;
            break;
        }
    }

    Files.write(path, lines);
    return updated;
}

   public static Map<String, String> queryToMap(String query) {
    Map<String, String> map = new HashMap<>();
    if (query != null) {
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) map.put(pair[0], pair[1]);
        }
    }
    return map;
}


public void cleanAppointmentFile() {
    Path path = Paths.get(APPOINTMENT_FILE);
    if (!Files.exists(path)) return;

    try {
        List<String> lines = Files.readAllLines(path);
        List<String> cleaned = new ArrayList<>();

        for (String raw : lines) {
            String line = raw.trim();

            // Leave borders and blank lines alone
            if (line.startsWith("+") || line.isEmpty()) {
                cleaned.add(raw);
                continue;
            }

            // Leave header row alone but trim its cells
            if (line.contains("Appointment ID") || line.contains("Name")) {
                cleaned.add(cleanTableRow(line, 7));
                continue;
            }

            // Only process actual data rows starting with '|'
            if (line.startsWith("|")) {
                cleaned.add(cleanTableRow(line, 7));
            } else {
                cleaned.add(raw);
            }
        }

        Files.write(path, cleaned);
        System.out.println("Appointment file cleaned successfully.");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private String cleanTableRow(String row, int expectedCols) {
    // Strip first/last pipe(s)
    String content = row.replaceAll("^\\|+", "").replaceAll("\\|+$", "");
    String[] parts = content.split("\\|");

    List<String> cells = new ArrayList<>();
    for (String part : parts) {
        String cell = part.trim();
        if (cell.isEmpty()) cell = "Unknown";
        cells.add(" " + cell + " ");
    }

    // Pad or truncate to the expected number of columns
    while (cells.size() < expectedCols) cells.add(" Unknown ");
    if (cells.size() > expectedCols) cells = cells.subList(0, expectedCols);

    // Rebuild with single pipe separators
    StringBuilder sb = new StringBuilder();
    sb.append("|");
    for (String c : cells) sb.append(c).append("|");
    return sb.toString();
}


public Appointment findAppointmentById(String appointmentID) {
    return appointments.get(appointmentID); 
}

public void viewAppointment(adt.ArrayListADT<Appointment> appointmentList) {
    String line = "+----------------+----------------------+----------------+--------+----------------------+----------------+----------------+";
    String headerFormat = "| %-14s | %-20s | %-16s | %-6s | %-20s | %-14s | %-16s |%n";
    String rowFormat    = "| %-14s | %-20s | %-16s | %-6s | %-20s | %-14s | %-16s |%n";

    System.out.println(line);
    System.out.printf(headerFormat,
            "Appointment ID", "Patient Name", "Appointment Time", "Room", "Reason", "Next Check-Up", "Rescheduled From");
    System.out.println(line);

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (Appointment appt : appointmentList) {
        System.out.printf(rowFormat,
                appt.getAppointmentID(),
                appt.getPatient().getName(),
                appt.getAppointmentTime().format(dtf),
                appt.getRoomOnTheDay(),
                appt.getReasonToCheck(),
                appt.getNextCheckUp() != null ? appt.getNextCheckUp().format(dtf) : "N/A",
                appt.getRescheduledFrom() != null ? appt.getRescheduledFrom() : "N/A"
        );
    }

    System.out.println(line);
}

    public void addAppointment(Appointment appointment) {
        appointments.put(appointment.getAppointmentID(), appointment);
    }

 
    public Appointment getAppointmentByID(String appointmentID) {
        return appointments.get(appointmentID);
    }

    public boolean appointmentExists(String appointmentID) {
        return appointments.containsKey(appointmentID);
    }

   }
   
   