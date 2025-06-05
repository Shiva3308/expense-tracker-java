import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction {
    String type;       
    String category;
    LocalDate date;
    double amount;

    public Transaction(String type, String category, LocalDate date, double amount) {
        this.type = type;
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return type + "," + category + "," + date + "," + amount;
    }
}

public class ExpenseTracker {
    private static final String FILE_NAME = "transactions.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) {
        loadTransactionsFromFile();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Expense Tracker ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    addTransaction(scanner, "income");
                    break;
                case 2:
                    addTransaction(scanner, "expense");
                    break;
                case 3:
                    viewMonthlySummary(scanner);
                    break;
                case 4:
                    saveTransactionsToFile();
                    System.out.println("Exiting. Transactions saved.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addTransaction(Scanner scanner, String type) {
        System.out.print("Enter category (" + (type.equals("income") ? "salary/business" : "food/rent/travel") + "): ");
        String category = scanner.nextLine().toLowerCase();

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        LocalDate date = LocalDate.now();
        Transaction transaction = new Transaction(type, category, date, amount);
        transactions.add(transaction);
        System.out.println("Transaction added successfully.");
    }

    private static void viewMonthlySummary(Scanner scanner) {
        System.out.print("Enter year (e.g., 2025): ");
        int year = scanner.nextInt();
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();

        double incomeTotal = 0;
        double expenseTotal = 0;

        System.out.println("\n--- Monthly Summary ---");
        for (Transaction t : transactions) {
            if (t.date.getYear() == year && t.date.getMonthValue() == month) {
                System.out.println(t);
                if (t.type.equals("income")) {
                    incomeTotal += t.amount;
                } else {
                    expenseTotal += t.amount;
                }
            }
        }

        System.out.println("Total Income: ₹" + incomeTotal);
        System.out.println("Total Expenses: ₹" + expenseTotal);
        System.out.println("Net Savings: ₹" + (incomeTotal - expenseTotal));
    }

    private static void saveTransactionsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Transaction t : transactions) {
                writer.println(t);
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    private static void loadTransactionsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String type = parts[0];
                    String category = parts[1];
                    LocalDate date = LocalDate.parse(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    transactions.add(new Transaction(type, category, date, amount));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }
}
