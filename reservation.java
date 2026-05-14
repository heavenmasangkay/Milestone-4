package boutiquecapsulehotelsystem;
import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;

public class Reservation {

    public static void bookRoom() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- BOOK ROOM ---");
        try (Connection conn = DBConnection.connect()) {
            System.out.print("Room ID: ");
            int rid = sc.nextInt(); sc.nextLine();
            
            PreparedStatement cp = conn.prepareStatement("SELECT status FROM rooms WHERE room_id=?");
            cp.setInt(1, rid);
            ResultSet rs = cp.executeQuery();
            if (rs.next() && !rs.getString("status").equals("Available")) {
                System.out.println("Room is not available."); return;
            }

            System.out.print("Guest Name: "); String name = sc.nextLine();
            
            String contact;
            while (true) {
                System.out.print("Contact No: "); contact = sc.nextLine();
                if (contact.matches("\\d+")) break;
                System.out.println("Numbers only, please.");
            }

            String in, out;
            while (true) {
                System.out.print("Check-in (YYYY-MM-DD): "); in = sc.nextLine();
                System.out.print("Check-out (YYYY-MM-DD): "); out = sc.nextLine();
                try {
                    if (LocalDate.parse(out).isAfter(LocalDate.parse(in))) break;
                    System.out.println("Check-out must be after check-in.");
                } catch (Exception e) {
                    System.out.println("Invalid date format. Use YYYY-MM-DD.");
                }
            }

            PreparedStatement pst = conn.prepareStatement("INSERT INTO reservations (room_id, guest_name, contact_no, check_in, check_out) VALUES (?,?,?,?,?)");
            pst.setInt(1, rid); pst.setString(2, name); pst.setString(3, contact); pst.setString(4, in); pst.setString(5, out);
            pst.executeUpdate();
            conn.createStatement().executeUpdate("UPDATE rooms SET status='Occupied' WHERE room_id=" + rid);
            System.out.println("Booking Success!");

        } catch (Exception e) {
            System.err.println("Booking Error: " + e.getMessage());
        }
    }

    public static void viewReservations() {
        try (Connection conn = DBConnection.connect(); 
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM reservations")) {
            
            System.out.println("\n+--------+---------+--------------------+------------+------------+");
            System.out.println("| Res ID | Room ID | Guest Name         | Check-in   | Check-out  |");
            System.out.println("+--------+---------+--------------------+------------+------------+");
            while (rs.next()) {
                System.out.printf("| %-6d | %-7d | %-18s | %-10s | %-10s |\n",
                    rs.getInt("res_id"), rs.getInt("room_id"), rs.getString("guest_name"), rs.getString("check_in"), rs.getString("check_out"));
            }
            System.out.println("+--------+---------+--------------------+------------+------------+");
            System.out.println("\nPress Enter to go back...");
            new Scanner(System.in).nextLine();

        } catch (Exception e) {
            System.err.println("View Error: " + e.getMessage());
        }
    }

    public static void cancelReservation() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBConnection.connect()) {
            System.out.print("\nEnter Res ID: ");
            int id = sc.nextInt();
            
            PreparedStatement pst = conn.prepareStatement("SELECT room_id FROM reservations WHERE res_id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int rid = rs.getInt("room_id");
                conn.createStatement().executeUpdate("UPDATE rooms SET status='Available' WHERE room_id=" + rid);
                conn.createStatement().executeUpdate("DELETE FROM reservations WHERE res_id=" + id);
                System.out.println("Reservation Cancelled.");
            } else {
                System.out.println("Reservation ID not found.");
            }
        } catch (Exception e) {
            System.err.println("Cancellation Error: " + e.getMessage());
        }
    }

    public static void changeReservation() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBConnection.connect()) {
            System.out.print("\nEnter Res ID: ");
            int id = sc.nextInt(); sc.nextLine();
            
            System.out.print("New Check-in: "); String in = sc.nextLine();
            System.out.print("New Check-out: "); String out = sc.nextLine();
            
            if (LocalDate.parse(out).isAfter(LocalDate.parse(in))) {
                PreparedStatement up = conn.prepareStatement("UPDATE reservations SET check_in=?, check_out=? WHERE res_id=?");
                up.setString(1, in); up.setString(2, out); up.setInt(3, id);
                int rows = up.executeUpdate();
                if (rows > 0) System.out.println("Dates updated.");
                else System.out.println("ID not found.");
            } else {
                System.out.println("Invalid date logic.");
            }
        } catch (Exception e) {
            System.err.println("Update Error: " + e.getMessage());
        }
    }
}