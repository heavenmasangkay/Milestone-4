package boutiquecapsulehotelsystem;
import java.sql.*;
import java.util.Scanner;

public class Room {
    public static void browseRooms() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms ORDER BY room_id ASC")) {

            System.out.println("\n+---------+-------------------+------------+------------+");
            System.out.println("| Room ID | Category          | Price      | Status     |");
            System.out.println("+---------+-------------------+------------+------------+");
            while (rs.next()) {
                System.out.printf("| %-7d | %-17s | \u20b1 %-8d | %-10s |\n", 
                    rs.getInt("room_id"), rs.getString("category"), rs.getInt("price"), rs.getString("status"));
            }
            System.out.println("+---------+-------------------+------------+------------+");
            System.out.print("\nEnter Room ID to view details | 0 to go back: ");
            
            if (sc.hasNextInt()) {
                int id = sc.nextInt();
                if (id != 0) viewRoomDetailsByID(id);
            }
        } catch (Exception e) {
            System.err.println("Browse Error: " + e.getMessage());
        } finally {
            
            System.out.println("Returning to main view...");
        }
    }

    public static void viewRoomDetailsByID(int id) {
        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM rooms WHERE room_id=?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("\n--- ROOM " + id + " DETAILS ---");
                System.out.println("Category:    " + rs.getString("category"));
                System.out.println("Price:       \u20b1 " + rs.getInt("price"));
                System.out.println("Status:      " + rs.getString("status"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("\nPress Enter to continue...");
                new Scanner(System.in).nextLine();
            } else {
                System.out.println("Room ID not found.");
            }
        } catch (Exception e) {
            System.err.println("Detail View Error: " + e.getMessage());
        }
    }

    public static void searchRooms() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBConnection.connect()) {
            System.out.print("\nEnter category keyword (e.g., Deluxe): ");
            String key = sc.nextLine();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM rooms WHERE category LIKE ?");
            pst.setString(1, "%" + key + "%");
            ResultSet rs = pst.executeQuery();
            
            System.out.println("\n--- SEARCH RESULTS ---");
            boolean found = false;
            while (rs.next()) {
                System.out.printf("[%d] %s - \u20b1 %d (%s)\n", 
                    rs.getInt("room_id"), rs.getString("category"), rs.getInt("price"), rs.getString("status"));
                found = true;
            }
            if (!found) System.out.println("No rooms match that keyword.");
            
        } catch (Exception e) {
            System.err.println("Search Error: " + e.getMessage());
        }
    }
}