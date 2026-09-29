import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionLogger {

    private String directoryPath = "transactions";

    public TransactionLogger(){
        File dir  = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

    }
   //DRY >
    private String getCustomerFileName(String customerId){
        return directoryPath + File.separator + "Customer-" + customerId + ".txt";

    }
    //void > Action in File >> no return
    public void log(String customerId, String type,double amount,double resultingBalance) throws IOException {
        String fileName = getCustomerFileName(customerId);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String line = timestamp + "|" + type + "|" + amount + "|" + resultingBalance ;

        writer.write(line);
        writer.newLine();
        writer.close();
    }

    public void printHistory(String customerId) throws IOException{
        // get file if not there print No transaction ..
        String fileName = getCustomerFileName(customerId);
        File file = new File(fileName);

        if (!file.exists()){
            System.out.println("No transactioin yet.");
            return;
        }
        // read the file
        BufferedReader reader = new BufferedReader(new FileReader(file));
        System.out.println(" -- Transaction History --");

        String line = reader.readLine();
        while (line != null){
            String[] parts = line.split("\\|");
            String date = parts[0];
            String type = parts[1];
            double amount = Double.parseDouble(parts[2]);
            String balanceAfter = parts[3];

            System.out.println(date + "|" + type + "|" + amount + "| Balance after: " + balanceAfter);

            line = reader.readLine();
        }
        reader.close();
    }
    public double getTodayTotal(String customerId, String type) throws IOException {
        String fileName = getCustomerFileName(customerId);
        File file = new File(fileName);
        if (!file.exists()){
            return 0;
        }

        String today = java.time.LocalDate.now().toString();
        double total = 0;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null){
            String[] parts = line.split("\\|");
            String dateOnly = parts[0].substring(0, 10);
            String logType = parts[1];
            double amount = Double.parseDouble(parts[2]);

            if (dateOnly.equals(today) && logType.equals(type)){
                total = total + amount;
            }
            line = reader.readLine();
        }
        reader.close();
        return total;
    }
    public static double getLimit (String cardType ,String operation){
        if (cardType.equals("Platinum")){
            if (operation.equals("Withdraw")) return 20000;
            if (operation.equals("Transfer")) return 40000;
            if (operation.equals("Transfer-Own")) return 80000;
            if (operation.equals("Deposit")) return 100000;
        }else if (cardType.equals("Titanium")){
            if (operation.equals("Withdraw")) return 10000;
            if (operation.equals("Transfer")) return 20000;
            if (operation.equals("Transfer-Own")) return 40000;
            if (operation.equals("Deposit")) return 100000;
        }else {
            if (operation.equals("Withdraw")) return 5000;
            if (operation.equals("Transfer")) return 10000;
            if (operation.equals("Transfer-Own")) return 40000;
            if (operation.equals("Deposit")) return 100000;
        }
        return 0;

        }
        public void printFiltered(String customerId, int daysBack) throws IOException{
        String fileName = getCustomerFileName(customerId);
        File file = new File(fileName);

        if (!file.exists()){
            System.out.println("No transactioin yet.");
            return;

        }
        LocalDate cutoff = java.time.LocalDate.now().minusDays(daysBack);
        boolean found = false;

        BufferedReader reader = new BufferedReader(new FileReader(file));
            System.out.println(" -- Filtered Transactions ---");

            String line = reader.readLine();
            while (line != null){
                String[] parts = line.split("\\|");
                String dateOnly = parts[0].substring(0, 10);
                java.time.LocalDate date = java.time.LocalDate.parse(dateOnly);

                if (!date.isBefore(cutoff)){
                    System.out.println(parts[0] + " | " + parts[1] + " | Amount: " +  parts[2] + " | Balance after: " + parts[3]);
                    found = true;

                }
                line = reader.readLine();
            }
            reader.close();

            if (!found){
                System.out.println("No transactioin yet.");
            }
        }
    }

