import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Set;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class budgetTC {
   public static Map<String, Double> storage = null;
   public static Scanner input = new Scanner(System.in);
   public static NumberFormat formatter = NumberFormat.getInstance();
   public static DecimalFormat decimalFormatter = new DecimalFormat("#.##");
   private static double netMonthlyIncome;
   private static double grossMonthlyIncome;
   private static double totalTaxes = 0;
   private static double fedTax = 0.0;
   private static double stateTax = 0.0;
   private static double cityTax = 0.0;
   private static double ficaTax = 0.0;
   private static double totalSpent = 0.0;

   public static void main(String args[]) throws IOException 
   {
      
      formatter.setGroupingUsed(true);
      decimalFormatter.setGroupingSize(2);
   
      //instantiate matrices that store tax bracket information
      double[][] federalBrackets = { { .10, .12, .22, .24, .32, .35, .37 },
             { 0, 9876, 40126, 85526, 163301, 207351, 518401 } };
      double[][] calStateBrackets = { { .01, .02, .04, .06, .08, .093, .103, .113, .123 },
             { 0, 8932, 21175, 33421, 46395, 58634, 299508, 359407, 599012 } };
      double[][] nyStateBrackets = { { .04, .045, .0525, .059, .0597, .0633, .0685, .0965, .103, .109 },
             { 0, 8500, 11700, 13901, 21401, 80651, 215401, 1077550, 5000000, 25000000 } };
      double[][] nyCityBrackets = { { .03078, .03762, .03819, .03876 }, { 0, 12000, 25000, 50000 } };
   
      // Greet user
      System.out.println("Welcome to BudgetTC");
   
      // Checks to see if the test document had data in it, if it does it asks if they want to continue.
      boolean hasData = false;
      if (hasData = storageReader()) 
      {
         System.out.println("It looks like you have already made a budget. if you would like to continue with your\n"
                + "previous budget, enter \"1\". If you would like to create a new budget, enter \"2\".\n");
         if (input.next().equals("1")) 
         {
            System.out.println();
            budgetSummary();
            findUserAction();
            return;
         }
      }
   
      if(!hasData)
         System.out.print("You have not made a budget before with BudgetTC or you have deleted it.");
   
      // Ask user for monthly income and calculate taxes.
      System.out.print("\nWhat monthly pre-tax income would you like to use for your new budget? ==> $");
      grossMonthlyIncome = input.nextDouble();
     
      calculateTotalTax(federalBrackets, calStateBrackets, nyStateBrackets, nyCityBrackets);
      
      netMonthlyIncome = grossMonthlyIncome - totalTaxes;
   
      // Prints out taxes
      taxSummary(totalTaxes, fedTax, stateTax, cityTax, ficaTax);
   
      // Creates premade budget with unallocated categories
      createBudget(netMonthlyIncome);
   
      //starts a recursive loop of asking the user what to do.
      findUserAction();
   }


   // Calculates the total tax to be paid by the user using given income with the execption of calulating fica.
   public static void calculateTotalTax(double[][] federalBrackets, double[][] calStateBrackets, double[][] nyStateBrackets, double[][] nyCityBrackets) {
   
      System.out.print("\nAre you married filing jointly(\"j\") or single filing seperatly(\"s\")? ");
      String type = input.next();
      System.out.println();
   
      if (type.equals("j")) 
      {
         for (int x = 0; x < nyStateBrackets[1].length; x++) 
         {
            nyStateBrackets[1][x] = nyStateBrackets[1][x] * 2;
            if (x < calStateBrackets[1].length)
               calStateBrackets[1][x] = calStateBrackets[1][x] * 2;
            if (x < federalBrackets[1].length)
               federalBrackets[1][x] = federalBrackets[1][x] * 2;
            if (x < nyCityBrackets[1].length)
               nyCityBrackets[1][x] = nyCityBrackets[1][x] * 2;
         }
      }
   
      totalTaxes += ficaTax = ficaCalculator(grossMonthlyIncome);
   
      fedTax = calculateTaxFromBracket(federalBrackets, grossMonthlyIncome);
   
      System.out.print("Which state do you live in(ca/tx/ny)? ");
      type = input.next();
   
      if (type.equals("California") || type.equals("california") || type.equals("cal") || type.equals("CA") || type.equals("cali") || type.equals("ca"))
      {
         stateTax = calculateTaxFromBracket(calStateBrackets, grossMonthlyIncome);
         totalTaxes = fedTax + stateTax;
      }
      if (type.equals("Texas") || type.equals("texas") || type.equals("TX") || type.equals("tex")
             || type.equals("tx"))
         ;
      {
         totalTaxes = fedTax;
      }
      if (type.equals("Ny") || type.equals("NY") || type.equals("nyc") || type.equals("ny") || type.equals("NYC")
             || type.equals("New York") || type.equals("new york") || type.equals("newyork")) 
      {
         System.out.print("Do you live in New York City?");
         type = input.next();
         System.out.println();
         stateTax = calculateTaxFromBracket(nyStateBrackets, grossMonthlyIncome);
         totalTaxes = fedTax + stateTax;
         if (type == "yes" || type == "YES" || type == "Yes")
            ;
         {
            cityTax = calculateTaxFromBracket(nyCityBrackets, grossMonthlyIncome);
            totalTaxes += cityTax;
         }
      }
   }

   //prints out the users tax summary which outlines what taxes are paid in each category.
   public static void taxSummary(double totalTaxes, double fedTax, double stateTax, double cityTax, double ficaTax) {
   
      System.out.print("\nThis months federal income tax: ==> $"
             + formatter.format(Double.parseDouble(decimalFormatter.format(fedTax))));
      System.out.print("\nThis months state income tax: ==> $" /* calculateTax(stateBrackets, grossMonthlyIncome) */);
      if (stateTax == 0)
      {
         System.out.print(" Texas does not have an income tax :)");
      } 
      else 
      {
         System.out.print(formatter.format(Double.parseDouble(decimalFormatter.format(stateTax))));
      }
      System.out.print("\nThis months city income tax: ==> $" /* calculateTax(stateBrackets, grossMonthlyIncome) */);
      if (cityTax == 0) {
         System.out.print("Your City does not have an income tax or it is not calculated by this calculator");
      } 
      else 
      {
         System.out.print(formatter.format(Double.parseDouble(decimalFormatter.format(cityTax))));
      }
      System.out.println("\nThis months FICA tax: ==> $" + ficaCalculator(grossMonthlyIncome));
      System.out.println("Total taxes witheld for this month: ==> $"
             + formatter.format(Double.parseDouble(decimalFormatter.format(totalTaxes))));
      System.out.printf("\nYou have %.2f dollars to allocate in this months budget\n", netMonthlyIncome);
   
   }

   //helper method to the calculateTotalTax method. It calculates the amount of tax in each category using a certain bracket matrix.
   public static double calculateTaxFromBracket(double[][] brackets, double grossIncome)
   {
      double income = grossIncome * 12;
      double taxes = 0;
      for (int c = 0; c < brackets[0].length - 1; c++) 
      {
         if (brackets[1][c + 1] < income) 
         {
            taxes += brackets[0][c] * (brackets[1][c + 1] - brackets[1][c]);
         } else if (brackets[1][c + 1] > income) 
         {
            taxes += (income - brackets[1][c]) * brackets[0][c];
            return taxes / 12;
         }
         if (c == brackets[0].length - 2 && income > brackets[1][brackets[0].length - 1]) 
         {
            c = brackets[0].length - 1;
            taxes += 0;
         }
      }
   
      return taxes / 12;
   }

    //helper method to the calculateTotalTax method. It calculates the amount od tax to be paid for FICA(used for social-security/medicare/medicaid).
   public static double ficaCalculator(double grossIncome)
   {
      double taxes = 0;
      if (grossIncome * .062 > 8853)
         taxes = 8853;
      else
         taxes = grossIncome * .062;
      return taxes;
   }

   //Adds premade categories to the budget for the user to modify
   public static void createBudget(double netInc) 
   {
      storage = new TreeMap<>();
      storage.put("(A) Housing", 0.0);
      storage.put("(B) Utilities", 0.0);
      storage.put("(C) Health Care", 0.0);
      storage.put("(D) Food", 0.0);
      storage.put("(E) Transportation", 0.0);
      storage.put("(F) Personal Care", 0.0);
      storage.put("(G) Entertainment", 0.0);
      storage.put("(H) Debt", 0.0);
      storage.put("(I) Savings", 0.0);
   
      Set<String> set1 = storage.keySet();
      
   
      totalSpent = 0;
      double value;
   
      for (String key : set1) 
      {
         if (key.charAt(1) == 'A')
            System.out.print("How much do you want to allocate for " + key + "? ==> $");
         else
            System.out.printf("You have $ "
                   + formatter.format(Double.parseDouble(decimalFormatter.format(netInc - totalSpent)))
                   + " left, how much do you want to allocate for " + key + "? ==> $");
         value = input.nextDouble();
         totalSpent += value;
         storage.put(key, value);
      }
      storage.put("'(T1) Federal-Income-Tax", fedTax);
      if(stateTax>0)
         storage.put("'(T2) State-Income-Tax", stateTax);
      if(cityTax>0)
         storage.put("'(T3) City-Income-Tax", cityTax);
      storage.put("'(T4) FICA", ficaTax);
      System.out.println();
   
   }

   //recurisve method that asks the user what to do until the user decides to end the program. Each user input is tied to an action.
   public static void findUserAction() throws IOException 
   {
      double tempNum = 0;
      System.out.println("\nEnter the number corresponding to the action you want to perform."
             + "\nAdd a category to the budget ---------------- 1"
             + "\nModify an ammount allocated to a category --- 2"
             + "\nDelete a user created category -------------- 3"
             + "\nAccess a Retirement Calculator----------------4"
             + "\nPrintout Final Budget Summary-----------------5"
             + "\nEnd program-----------------------------------6"
             + "\n*DELETE* data and end program-----------------7\n");
   
      int userAction = 0;
      try 
      {
         userAction = input.nextInt();
      } 
      catch (Exception e) 
      {
         System.out.println("\n---You did not enter a number, try again.---\n");
         findUserAction();
      }
      if (userAction > 8) 
      {
         System.out.println(
                "\n---The number you entered does not correspond with any of the options, try again.---\n");
         findUserAction();
      }
      if (userAction == 1) 
      {
         System.out.print("What is the name of the category you would like to add (1 word)? ==> ");
         String cat = input.next();
         System.out.print("You have $ "
            + formatter.format(Double.parseDouble(decimalFormatter.format(netMonthlyIncome - totalSpent)))
            + " left, how much do you want to allocate for " + cat + "? ==> $");
         double val = input.nextDouble();
         addCategory(true, cat, val, null);
         totalSpent+=val;
         System.out.print("Added!\n");
         findUserAction();
      } else if (userAction == 2) 
      {
         displayBudget();
         System.out
                .print("What is the ID(Letter or Number in front) of the category you would like to modify? ==> ");
         String id = input.next();
         Set<String> set1 = storage.keySet();
         String keyval = "";
         for (String key : set1) 
         {
            if (key.substring(key.indexOf('(') + 1, key.indexOf(')')).equals(id))
               keyval = key;
         }
         
         System.out.print("You have $ "
            + formatter.format(Double.parseDouble(decimalFormatter.format(netMonthlyIncome - totalSpent)))
            + " left, how much do you want to allocate for " + keyval + "? ==> $");
         totalSpent-= storage.get(keyval);
         storage.put(keyval, tempNum = input.nextDouble());
         totalSpent+=tempNum;
         System.out.print("Modified!\n");
         findUserAction();
      } else if (userAction == 3) 
      {
         displayBudget();
         System.out.print("\nWhat is the ID of the category you would like to delete? ==> ");
         String id = input.next();
         String preMadeIDs = "ABCDEFGHIabcdefghi";
         if (!(id.substring(0, 1)).equals("1")) 
         {
            if (preMadeIDs.indexOf(id) > -1)
               System.out.println(
                      "\nSorry, you can only delete a category you made yourself.\nDeletion of premade categories is not permisable.\n");
            else
               System.out.println(
                      "\nThe ID that you have input does not exist as a premade category, only a numerical ID is deletable");
            findUserAction();
            return;
         } else 
         {
            Map<String, Double> reAdd = new TreeMap<>();
            List<String> keylist = new ArrayList<>(storage.keySet());
            String temp;
            for (int x = 9; x < keylist.size(); x++) 
            {
               temp = (keylist.get(x));
               if (temp.substring(temp.indexOf('(') + 1, temp.indexOf(')')).equals(id))
               {
                  storage.remove(temp);
               }
               else
               {
                  reAdd.put(temp, storage.get(temp));
                  storage.remove(temp);
               }
            }
            addCategory(false, null, null, reAdd);
            System.out.print("deleted!\n");
            findUserAction();
         }
      }
      else if (userAction == 4) 
      {
         System.out.println();
         retirementCalc();
         findUserAction();
      } 
      else if (userAction == 5) 
      {
         System.out.println();
         budgetSummary();
         findUserAction();
      } 
      else if (userAction == 6) 
      {
         storageWriter();
         System.out.print("\nThe budget that you have created today will be saved for your next visit.");
         System.out.println(" Thank you for using BudgetTC, Goodbye!");
         return;
      } 
      else if (userAction == 7) 
      {
         System.out.println(
                "\nAre you sure that you want to delete your budget? You wont be able to access them next time if you do so.\n");
         if (input.next().equals("yes")) 
         {
            deleteData();
            System.out.print("\nThe budget that you have created has been permanently deleted.");
            System.out.println(" Thank you for using BudgetTC, Goodbye!");
            return;
         } 
         else 
         {
         findUserAction();
         }
      
      }
   }

   //adds a user made category to the budget. If boolean whatToDo is false it will add a list of categories to the budget.
   public static void addCategory(boolean whatToDo, String category, Double value, Map<String, Double> reAdd) 
   {
      int budgetSize = storage.size();
      if (whatToDo) 
      {
         String x = "_(" + (budgetSize + 1) + ") " + category;
         budgetSize++;
         storage.put(x, value);
      } 
      else 
      {
         Set<String> set1 = reAdd.keySet();
         budgetSize++;
         for (String key : set1) 
         {
            String x = "_(" + (budgetSize++) + ") " + key.substring(key.indexOf(' ')+1);
            storage.put(x, reAdd.get(key));
         }
      }
   }

   //Creates a very simple budget display to tell the user what ID is matched to each category and its allocated value.
   public static void displayBudget() 
   {
      Set<String> set1 = storage.keySet();
      for (String key : set1) 
      {
         System.out.println(key + " ==> " + storage.get(key));
      }
      System.out.println();
   }

   //A more complex version of displayBudget(), it tells the user more information and visulaizes the proportion of money spent in each category.
   public static void budgetSummary() 
   {
      System.out.print("ID    Category             Amount Allocated     Proportion of Total"
                   +"\n--------------------------------------------------------------------");
      Set<String> set1 = storage.keySet();
      String id, cat;
      double prop;
      double total = grossMonthlyIncome;
      for (String key : set1) 
      {
         id = (key.substring(key.indexOf('(') + 1, key.indexOf(')')));
         System.out.print("\n" + id);
         for (int x = id.length(); x < 5; x++) 
         {
            System.out.print(" ");
         }
         cat = key.substring(key.indexOf(" "));
         System.out.print(cat);
         for (int x = cat.length(); x < 27; x++) 
         {
            System.out.print(" ");
         }
         System.out.print("$" + formatter.format(Double.parseDouble(decimalFormatter.format(storage.get(key)))));
         total -= storage.get(key);
         for (int x = (formatter.format(Double.parseDouble(decimalFormatter.format(storage.get(key))))).length(); x < 16; x++) 
         {
            System.out.print(" ");
         }
         System.out.print(formatter.format(
                Double.parseDouble(decimalFormatter.format((storage.get(key) / netMonthlyIncome) * 100))) + "% ");
         for (int x = (formatter.format(Double.parseDouble(decimalFormatter.format((storage.get(key) / netMonthlyIncome) * 100))))
                        .length(); x < 6; x++) 
                        {
         
            System.out.print(" ");
         }
         for (double x = 0; x < (storage.get(key) / netMonthlyIncome) * 100; x++) 
         {
            System.out.print("|");
         }
      }
      totalSpent = netMonthlyIncome - total;
      System.out.println("\n\nAmount Unallocated/Over allocated: $"
             + formatter.format(Double.parseDouble(decimalFormatter.format((total)))) + "\n");
   }

   //Determines how much money the user will have at retirement. 
   public static void retirementCalc() 
   {
      System.out.print(
             "To find out how much money you will have at retirment based on your current monthly savings allocation\n"
                     + "we will need to ask you a few questions:\n\nFirstly, what is your current age? ");
      double currentAge = input.nextDouble();
      System.out.print("\nAt what age do you plan to retire? ");
      double retirementAge = input.nextDouble();
      System.out.print("\nHow much money have you saved up till now in a Tax-Deffered savings account? ");
      double currentSav = input.nextDouble();
      double total = currentSav;
      System.out.print("\nWhat ROI would you like us to use(enter in decimal form)?"
             + "\n(enter \"0\" if you would like use the standard market return(9%)) ==> ");
      double returnOnInvestment = input.nextDouble();
      if (returnOnInvestment == 0.0)
         returnOnInvestment = 0.09;
      double monthlyContribution = storage.get("(I) Savings");
      for (int x = 0; x < (retirementAge - currentAge); x++) {
         total = (total * (1 + returnOnInvestment)) + ((monthlyContribution) * 12);
      }
   
      double inflationAdjustedTotal = (Math.pow(.975, (retirementAge - currentAge)) * total);
   
      System.out.printf("\nAfter " + (retirementAge - currentAge) + " years of investing $%.2f a month at "
             + (100 * returnOnInvestment) + "%% with a current savings of $" + formatter.format(currentSav)
             + " in a tax-deferred savings account, you will have approximatley $"
             + formatter.format(Double.parseDouble(decimalFormatter.format(total))) + " dollars.\n" + "Which in "
             + (retirementAge - currentAge) + " years will be worth $"
             + formatter.format(Double.parseDouble(decimalFormatter.format(inflationAdjustedTotal)))
             + " at an estimated 2.5%% annual inflation.\n", monthlyContribution);
   
      // return total;
   }

   //deletes all of the data in the budget
   public static void deleteData() throws IOException 
   {
      FileWriter fWriter = new FileWriter("StorageVault.txt", false);
      BufferedWriter writer = new BufferedWriter(fWriter);
      writer.write("");
      writer.close();
   }

   //writes all of the data into a txt file so the user can access it next time.
   public static void storageWriter() throws IOException 
   {
      FileWriter fWriter = new FileWriter("StorageVault.txt", false);
      BufferedWriter writer = new BufferedWriter(fWriter);
      Set<String> set1 = storage.keySet();
      writer.write(String.valueOf(netMonthlyIncome));
      writer.newLine();
      writer.write(String.valueOf(grossMonthlyIncome));
      writer.newLine();
      //writer.write(String.valueOf(totalSpent));
      //writer.newLine();
      for (String key : set1) {
         writer.write(key);
         writer.newLine();
         writer.write(String.valueOf(storage.get(key)));
         writer.newLine();
      }
      writer.close();
   }

   //reads data from the file and adds it to the map which hold the data. Returns false if there is no data.
   public static boolean storageReader() throws IOException 
   {
      FileReader fReader = new FileReader("StorageVault.txt");
      BufferedReader reader = new BufferedReader(fReader);
      storage = new TreeMap<>();
      String temp1, temp2, temp3;
   
      if ((temp1 = reader.readLine()) != null && (temp2 = reader.readLine()) != null) 
      {
         netMonthlyIncome = Double.parseDouble(temp1);
         grossMonthlyIncome = Double.parseDouble(temp2);
         //totalSpent =  Double.parseDouble(temp3);
      }
      String keyval = "";
      String val = "";
      boolean x = false;
      while (((keyval = reader.readLine()) != null) && ((val = reader.readLine()) != null)) 
      {
         storage.put(keyval, Double.parseDouble(val));
         x = true;
      }
      reader.close();
      return x;
   }

}
