package boutiquecapsulehotelsystem;
import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PaymentFramework {

    public static void checkOut() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Reservation ID: ");
        if (!sc.hasNextInt()) {
            System.out.println("Invalid ID.");
            sc.next();
            return;
        }
        int id = sc.nextInt();
        try (Connection conn = DBConnection.connect()) {
            PreparedStatement pst = conn.prepareStatement(
                "SELECT r.*, rm.category, rm.price FROM reservations r " +
                "JOIN rooms rm ON r.room_id = rm.room_id WHERE r.res_id = ?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                LocalDate dIn = LocalDate.parse(rs.getString("check_in"));
                LocalDate dOut = LocalDate.parse(rs.getString("check_out"));
                long n = ChronoUnit.DAYS.between(dIn, dOut);
                if (n <= 0) n = 1; 
                double base = n * rs.getInt("price");
                
                System.out.println("\nSelect Customer Type:");
                System.out.println("1. Regular (0%)");
                System.out.println("2. Student (20%)");
                System.out.println("3. Senior (20%)");
                System.out.print("Choice: "); int type = sc.nextInt();
                
                double discRate = (type == 2 || type == 3) ? 0.20 : 0;
                double discAmount = base * discRate;
                double vat = (base - discAmount) * 0.12;
                double total = (base - discAmount) + vat;

                System.out.println("\n-------------------------------------------");
                System.out.println("              FINAL BILLING                ");
                System.out.println("-------------------------------------------");
                System.out.printf("Guest Name:        %s\n", rs.getString("guest_name"));
                System.out.printf("Room ID:           %d\n", rs.getInt("room_id"));
                System.out.printf("Category:          %s\n", rs.getString("category"));
                System.out.printf("Stay Duration:     %d Day(s)\n", n);
                System.out.println("-------------------------------------------");
                System.out.printf("Base Amount:       \u20b1 %.2f\n", base);
                System.out.printf("Discount:         -\u20b1 %.2f\n", discAmount);
                System.out.printf("VAT (12%%):         \u20b1 %.2f\n", vat);
                System.out.println("-------------------------------------------");
                System.out.printf("TOTAL DUE:         \u20b1 %.2f\n", total);
                System.out.println("-------------------------------------------");

                System.out.print("Enter Cash:        \u20b1 "); double cash = sc.nextDouble();
                while (cash < total) {
                    System.out.print("Insufficient. Enter more: ");
                    cash = sc.nextDouble();
                }
                System.out.printf("Change:            \u20b1 %.2f\n", (cash - total));

                System.out.print("\nConfirm Payment? (1-Yes / 2-No): ");
                if (sc.nextInt() == 1) {
                    PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO transactions (res_id, guest_name, total_amount) VALUES (?,?,?)");
                    ins.setInt(1, id);
                    ins.setString(2, rs.getString("guest_name"));
                    ins.setDouble(3, total);
                    ins.executeUpdate();
                    
                    conn.createStatement().executeUpdate("DELETE FROM reservations WHERE res_id=" + id);
                    conn.createStatement().executeUpdate("UPDATE rooms SET status='Available' WHERE room_id=" + rs.getInt("room_id"));
                    System.out.println("Checkout Successful.");
                }
            } else {
                System.out.println("Reservation not found.");
            }
        } catch (Exception e) {
            System.out.println("Checkout error: " + e.getMessage());
        }
    }

    public static void viewIncomeStatement() {
        try (Connection conn = DBConnection.connect(); Statement s = conn.createStatement()) {
            System.out.println("\n+-------------------------------------------------------+");
            System.out.println("|               INCOME STATEMENT (SALES)                |");
            System.out.println("+--------+----------------------------+-----------------+");
            System.out.println("| Res ID | Guest Name                 | Final Amount    |");
            System.out.println("+--------+----------------------------+-----------------+");

            ResultSet rsSales = s.executeQuery("SELECT res_id, guest_name, total_amount FROM transactions");
            double totalProfit = 0;
            int count = 0;
            while (rsSales.next()) {
                System.out.printf("| %-6d | %-26s | \u20b1 %-13.2f |\n", 
                    rsSales.getInt("res_id"), rsSales.getString("guest_name"), rsSales.getDouble("total_amount"));
                totalProfit += rsSales.getDouble("total_amount");
                count++;
            }
            System.out.println("+--------+----------------------------+-----------------+");
            System.out.printf("| Total Bookings Finished: %-28d |\n", count);
            System.out.printf("| Total Net Profit:        \u20b1 %-26.2f |\n", totalProfit);
            System.out.println("+-------------------------------------------------------+");
            
            System.out.println("\nPress Enter to go back to Menu...");
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            System.out.println("Income Statement error: " + e.getMessage());
        }
    }
}