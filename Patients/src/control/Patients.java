/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package control;

import adt.HashMapADT;
import adt.PriorityQueueADT;
import boundary.MedicalHistoryUI;
import boundary.PatientUI;
import boundary.MenuUI;
import boundary.QueueUI;
import dao.AppointmentManagement;
import entity.Patient;
import java.util.Scanner;
import utility.PatientPriorityComparator;

/**
 *
 * @author Catherine Lim
 */
public class Patients {

  
 
    public static void main(String[] args) {
     //   new PatientUI.start();

         HashMapADT<String, Patient> patientMap = new HashMapADT<>(100);
    AppointmentManagement am = new AppointmentManagement(patientMap);
    
    //test and checckk
try {
    am.startServer();
  //  System.out.println("Server is running on http://127.0.0.1:8080");
} catch (Exception e) {
    //System.out.println("Failed to start HTTP server: " + e.getMessage());
    e.printStackTrace();
}
   // System.out.println("Server is running...");

        MenuUI menu = new MenuUI();
        boolean running = true;
        Scanner sc = new Scanner(System.in);

        while (running) {
            menu.printMenu();

            System.out.print("Continue? (y/n): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("n")) {
                running = false;
                System.out.println("Exiting program. Goodbye!");
            }
        }

        sc.close();
    }
}

    

