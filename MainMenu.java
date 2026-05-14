package boutiquecapsulehotelsystem;

import java.util.Scanner;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class MainMenu {
    public static void main(String[] args) {
       
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n+-------------------------------------------------------------+");
                System.out.println("|             BOUTIQUE CAPSULE HOTEL SYSTEM                   |");
                System.out.println("+-------------------------------------------------------------+");
                System.out.println("| 1. Browse Rooms               6. Cancel Reservation         |");
                System.out.println("| 2. Search Rooms               7. Change Reservation Date    |");
                System.out.println("| 3. Book Room                  8. Income Statement           |");
                System.out.println("| 4. View Reservations          9. Exit                       |");
                System.out.println("| 5. Check-Out (Billing)                                      |");
                System.out.println("+-------------------------------------------------------------+");
                System.out.print("Enter option: ");

                
                if (!sc.hasNextInt()) {
                    String invalidInput = sc.next();
                    throw new Exception("'" + invalidInput + "' is not a valid number!");
                }

                int choice = sc.nextInt();

                switch (choice) {
                    case 1: Room.browseRooms(); break;
                    case 2: Room.searchRooms(); break;
                    case 3: Reservation.bookRoom(); break;
                    case 4: Reservation.viewReservations(); break;
                    case 5: PaymentFramework.checkOut(); break;
                    case 6: Reservation.cancelReservation(); break;
                    case 7: Reservation.changeReservation(); break;
                    case 8: PaymentFramework.viewIncomeStatement(); break;
                    case 9: 
                        System.out.println("Exiting System..."); 
                        System.exit(0); 
                        break;
                    default: 
                        System.out.println("Invalid choice! Please select 1-9.");
                        break;
                }

            } catch (Exception e) {
              
                System.err.println("\n[SYSTEM ERROR]: " + e.getMessage());
                System.out.println("Please try again.");
            } finally {
                
                sc.nextLine(); 
            }
        }
    }
}