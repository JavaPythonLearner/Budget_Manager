package budget;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Main {
    DataManager dataManager;

    public Main() {
        this.dataManager = new DataManager(Path.of("purchases.txt"), BigDecimal.ZERO);
    }

    public static void main(String[] args) {
        Main mainApp = new Main();
        mainApp.performUserInteraction();
    }

    public void performUserInteraction() {
        UserInteraction userInteraction = new UserInteraction();
        boolean exit = false;

        while (!exit) {
            userInteraction.showMainMenu();
            int itemNumber = userInteraction.getMenuAction();
            Optional<MainMenu> userChoice = MainMenu.getMenuItem(itemNumber);

            if (userChoice.isPresent()) {
                userInteraction.printBlankLine();
                switch (userChoice.get()) {
                    case AddIncome:
                        getIncome(userInteraction);
                        break;
                    case AddPurchase:
                        getNewPurchaseData(userInteraction);
                        break;
                    case ShowPurchases:
                        showPurchase(userInteraction);
                        break;
                    case Balance:
                        showBalance(userInteraction);
                        break;
                    case Save:
                        saveData(userInteraction);
                        break;
                    case Load:
                        LoadData(userInteraction);
                        break;
                    case Analyze:
                        showAnalyze(userInteraction);
                        break;
                    case Exit:
                        exit = true;
                        userInteraction.exit();
                        break;
                    default:
                        break;
                }
                userInteraction.printBlankLine();
            } else {
                System.out.println("It is an unknown action.");
            }
        }
    }

     private void saveData(UserInteraction userInteraction) {
        try {
            dataManager.saveData();
            userInteraction.showPurchasesSaved();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void LoadData(UserInteraction userInteraction) {
        try {
            dataManager.loadData();
            userInteraction.showPurchasesLoaded();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getIncome(UserInteraction userInteraction) {
        dataManager.setIncome(userInteraction.getUserIncome());
    }

    private void showBalance(UserInteraction userInteraction) {
        double balance = dataManager.getBalance().doubleValue();
        userInteraction.showUserBalance(String.format("Balance: $%.2f", balance));
    }


    private void getNewPurchaseData(UserInteraction userInteraction) {
        while (true) {
            userInteraction.showPurchaseTypesMenuToAddPurchase();
            int itemNumber = userInteraction.getMenuAction();
            Optional<PurchaseTypesMenuToAddPurchase> userChoice = PurchaseTypesMenuToAddPurchase.getMenuItem(itemNumber);

            PurchasesCategory category;
            if (userChoice.isPresent()) {
                switch (userChoice.get()) {
                    case Food:
                        category = PurchasesCategory.Food;
                        break;
                    case Clothes:
                        category = PurchasesCategory.Clothes;
                        break;
                    case Entertainment:
                        category = PurchasesCategory.Entertainment;
                        break;
                    case Other:
                        category = PurchasesCategory.Other;
                        break;

                    case Back:
                    default:
                        return;
                }
                userInteraction.printBlankLine();
                Map.Entry<String, BigDecimal> entry = userInteraction.getUserPurchase();
                this.dataManager.addPurchaseToList(category, entry.getKey(), entry.getValue());
                userInteraction.printBlankLine();
            }
        }
    }

    private void showPurchase(UserInteraction userInteraction) {
        while (true) {
            userInteraction.showPurchaseTypesMenuToShowPurchase();
            int itemNumber = userInteraction.getMenuAction();
            Optional<PurchaseTypesMenuToShowPurchase> userChoice = PurchaseTypesMenuToShowPurchase.getMenuItem(itemNumber);

            PurchasesCategory category = null;
            if (userChoice.isPresent()) {
                switch (userChoice.get()) {
                    case Food:
                        category = PurchasesCategory.Food;
                        break;
                    case Clothes:
                        category = PurchasesCategory.Clothes;
                        break;
                    case Entertainment:
                        category = PurchasesCategory.Entertainment;
                        break;
                    case Other:
                        category = PurchasesCategory.Other;
                        break;
                    case All:
                        break;
                    case Back:
                    default:
                        return;
                }

                String purchases;
                BigDecimal sum;
                String title;
                if (userChoice.get() == PurchaseTypesMenuToShowPurchase.All) {
                    purchases = dataManager.getPurchaseTotalContent();
                    sum = dataManager.getPurchasesTotalSum();
                    title = "All:\n";
                } else {
                    purchases = dataManager.getPurchaseListContent(category);
                    sum = dataManager.getPurchaseListSum(category);
                    title = category.name() + ":\n";
                }
                userInteraction.printBlankLine();
                userInteraction.showUserPurchase(title + purchases.trim(),
                        String.format("Total sum: $%s", sum.toPlainString()));
                userInteraction.printBlankLine();
            }
        }
    }

    private void showAnalyze(UserInteraction userInteraction) {
        while (true) {
            userInteraction.showPurchasesSortMenu();
            int itemNumber = userInteraction.getMenuAction();
            Optional<PurchasesSortMenu> userChoice = PurchasesSortMenu.getMenuItem(itemNumber);
            if (userChoice.isPresent()) {
                userInteraction.printBlankLine();
                switch (userChoice.get()) {
                    case SortALL:
                        userInteraction.printSortedListData("All:", dataManager.getPurchaseTotalContentPriceSorted());
                        break;
                    case SortByType:
                        userInteraction.printSortedListData("Types:", dataManager.getPurchasesTypesSumSorted());
                        break;
                    case SortCertainType:
                        showAnalyzeCertainType(userInteraction);
                        break;
                    case Back:
                    default:
                        return;

                }
                userInteraction.printBlankLine();
            }

        }
    }

    private void showAnalyzeCertainType(UserInteraction userInteraction) {
        userInteraction.showPurchasesSortCertainTypeMenu();
        int itemNumber = userInteraction.getMenuAction();
        Optional<PurchaseTypesMenuToSort> userChoice = PurchaseTypesMenuToSort.getMenuItem(itemNumber);
        if (userChoice.isPresent()) {
            userInteraction.printBlankLine();
            switch (userChoice.get()) {
                case Food:
                    userInteraction.printSortedListData(userChoice.get().menuText, dataManager.getPurchaseListContentPriceSorted(PurchasesCategory.Food));
                    break;
                case Clothes:
                    userInteraction.printSortedListData(userChoice.get().menuText, dataManager.getPurchaseListContentPriceSorted(PurchasesCategory.Clothes));
                    break;
                case Entertainment:
                    userInteraction.printSortedListData(userChoice.get().menuText, dataManager.getPurchaseListContentPriceSorted(PurchasesCategory.Entertainment));
                    break;
                case Other:
                    userInteraction.printSortedListData(userChoice.get().menuText, dataManager.getPurchaseListContentPriceSorted(PurchasesCategory.Other));
                    break;
            }
        }
    }

}

class UserInteraction {
    Scanner scanner;

    UserInteraction() {
        this.scanner = new Scanner(System.in);
    }

    public int getMenuAction() {
        return scanner.nextInt();
    }

    public void exit() {
        System.out.println("Bye!");
        scanner.close();
    }

    public void printBlankLine() {
        System.out.println();
    }

    public BigDecimal getUserIncome() {
        System.out.println("Enter income");
        final BigDecimal income = BigDecimal.valueOf(scanner.nextDouble())
                .setScale(2, RoundingMode.HALF_UP);
        System.out.println("Income was added!");
        return income;
    }

    public Map.Entry<String, BigDecimal> getUserPurchase() {
        System.out.println("Enter purchase name:");
        scanner.nextLine();       // I don't understand why this is necessary in my case???
        String name = scanner.nextLine();

        System.out.println("Enter its price:");
        double price = 0.00;
        if (scanner.hasNext()) {
            // nextDouble() didn't see the value???
            String priceStr = scanner.next();
            price = Double.parseDouble(priceStr);
        }
        System.out.println("Purchase was added!");

        return Map.entry(name, BigDecimal.valueOf(price).setScale(2, RoundingMode.UP));
    }

    public void showUserPurchase(String purchaseList, String totalSum) {
        if (purchaseList.isEmpty()) {
            System.out.println("The purchase list is empty!");
            return;
        }
        System.out.println(purchaseList);
        System.out.println(totalSum);
    }

    public void showUserBalance(String balance) {
        System.out.println(balance);
    }

    public void showMainMenu() {
        MainMenu.showMainMenu();
    }

    public void showPurchaseTypesMenuToShowPurchase() {
        PurchaseTypesMenuToShowPurchase.showPurchaseTypesMenuToShowPurchase();
    }

    public void showPurchaseTypesMenuToAddPurchase() {
        PurchaseTypesMenuToAddPurchase.showPurchaseTypesMenuToAddPurchase();
    }

    public void showPurchasesSortMenu() {
        PurchasesSortMenu.showPurchasesSortMenu();
    }

    public void showPurchasesSortCertainTypeMenu() {
        PurchaseTypesMenuToSort.showPurchaseTypesMenuToSort();
    }

    public void showPurchasesSaved() {
        System.out.println("Purchases were saved!");
    }

    public void showPurchasesLoaded() {
        System.out.println("Purchases were loaded!");
    }

    public void printSortedListData(String title, String purchaseTotalContentPriceSorted) {
        if (purchaseTotalContentPriceSorted.isEmpty()) {
            System.out.println("The purchase list is empty!");
        } else {
            System.out.println(title + purchaseTotalContentPriceSorted);
        }
    }
}

//*******************************Data Models *************************************

/**
 * DataManger is based on Strategy pattern using Purchases helpList
 * Constructor: DataManager(Path sourceFile, BigDecimal income)
 *              - sourceFile : used file to store data and or load from it
 *              - income: amount of available money
 */
class DataManager {
    private final Purchases foodPurchases;
    private final Purchases clothPurchases;
    private final Purchases entertainmentPurchases;
    private final Purchases otherPurchases;

    private Purchases helpList;
    private final String sectionName = "[%s]";

    private final Path dataSourceFile;
    private BigDecimal income;
     final Comparator<Map.Entry<String, BigDecimal>> priceSorter = Map.Entry.comparingByValue();//.thenComparing(Map.Entry.comparingByKey());
     final Comparator<Map.Entry<String, BigDecimal>> priceSorterReveserd = priceSorter.thenComparing(Map.Entry.comparingByKey()).reversed();

    public DataManager(Path sourceFile, BigDecimal income) {
        this.dataSourceFile = sourceFile;
        this.income = income;
        this.foodPurchases = new Food();
        this.clothPurchases = new Clothes();
        entertainmentPurchases = new Entertainment();
        this.otherPurchases = new Other();
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }
    public BigDecimal getIncome() {
        return this.income;
    }

    private void setPurchaseList(PurchasesCategory category) {
        switch (category) {
            case Food:
                helpList = foodPurchases;
                break;
            case Clothes:
                helpList = clothPurchases;
                break;
            case Entertainment:
                helpList = entertainmentPurchases;
                break;
            case Other:
                helpList = otherPurchases;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + category);
                //break;
        }
    }

    public void addPurchaseToList(PurchasesCategory category, String name, BigDecimal price) {
        setPurchaseList(category);
        helpList.addNewPurchase(name, price);
    }

    public BigDecimal getPurchaseListSum(PurchasesCategory category) {
        setPurchaseList(category);
        return helpList.getPurchasesSum();
    }

    public BigDecimal getPurchasesTotalSum() {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchasesCategory category : PurchasesCategory.values()) {
            total = total.add(getPurchaseListSum(category));
        }
        return total;
    }


    public String getPurchaseListContent(PurchasesCategory category) {
        setPurchaseList(category);
        return helpList.getPurchaseContent();
    }

    public String getPurchaseTotalContent() {
        final StringBuilder sb = new StringBuilder();
        for (PurchasesCategory category : PurchasesCategory.values()) {
            setPurchaseList(category);
            sb.append(helpList.getPurchaseContent());
        }
        return sb.toString();
    }

    public String getPurchaseListContentPriceSorted(PurchasesCategory category) {
        setPurchaseList(category);
        return helpList.getPurchaseContent(priceSorterReveserd);
    }

    public String getPurchasesTypesSumSorted() {
        Map<String, BigDecimal> typesSummaryMap = new HashMap<>();
        BigDecimal sum;
        BigDecimal total = BigDecimal.ZERO;

        final StringBuilder sb = new StringBuilder();
        for (PurchasesCategory category : PurchasesCategory.values()) {
            sum = getPurchaseListSum(category).setScale(2, RoundingMode.UP);
            typesSummaryMap.put(category.name(), sum);
            total = total.add(sum);
        }
        typesSummaryMap.entrySet()
                .stream()
                .sorted(priceSorterReveserd)
                .forEach(entry -> sb.append(entry.getKey()).append(" - $").append(entry.getValue().toPlainString()).append("\n"));
        sb.append("Total sum: $").append(total.toPlainString());
        return sb.toString();
    }

    private Map<String, BigDecimal> getAllPurchases() {
        Map<String, BigDecimal> totalMap = new HashMap<>();
        for (PurchasesCategory category : PurchasesCategory.values()) {
            setPurchaseList(category);
            totalMap.putAll(helpList.specializedPurchaseMap);
        }
        return totalMap;
    }

    public String getPurchaseTotalContentPriceSorted() {
        StringBuilder sb = new StringBuilder();
        getAllPurchases().entrySet()
                .stream()
                .sorted(priceSorterReveserd)
                .forEach(entry -> sb.append(entry.getKey()).append(" $").append(entry.getValue().toPlainString()).append("\n"));
        return sb.toString();
    }

    public void saveData() throws IOException {
        //Files.createFile(this.dataSourceFile);
        Files.writeString(this.dataSourceFile, "[Income]\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.writeString(this.dataSourceFile, getIncome().toPlainString() + "\n", StandardOpenOption.APPEND);
        for (PurchasesCategory category : PurchasesCategory.values()) {
            setPurchaseList(category);
            String data = String.format(this.sectionName, category.name()) + "\n";
            data += helpList.getPurchaseContent();
            Files.writeString(this.dataSourceFile, data, StandardOpenOption.APPEND);
        }
    }

    public void loadData() throws IOException {
        String data = Files.readString(this.dataSourceFile);
        // Resolve income
        String sectionName = "[Income]\n";
        int start = data.indexOf(sectionName);
        int end = data.indexOf("\n", sectionName.length());
        if (start != -1 && end != -1) {
            String incomeString = data.substring(start +sectionName.length(), end);
            setIncome(BigDecimal.valueOf(Double.parseDouble(incomeString)));
        }

        // Resolve the purchases for each category separately
        for (PurchasesCategory category : PurchasesCategory.values()) {
            setPurchaseList(category);
            helpList.clearData();        // Always clear the table when loading data from a source
            sectionName = String.format(this.sectionName, category.name()) + "\n";
            if ((start = data.indexOf(sectionName)) != -1) {
                start += sectionName.length();
                end = data.indexOf("[", start);
                if (end == -1) {
                    end = data.length();
                }
                helpList.LoadData(data.substring(start, end));
            }
        }
    }

    public BigDecimal getBalance() {
        BigDecimal purchases = getPurchasesTotalSum();
        return this.income.subtract(purchases);
    }

}

abstract class Purchases {
    protected Map<String, BigDecimal> specializedPurchaseMap;

    public Purchases() {
        this.specializedPurchaseMap = new HashMap<>();
    }

    public void addNewPurchase(String name, BigDecimal price) {
        this.specializedPurchaseMap.merge(name, price, BigDecimal::add);
    }

    public String getPurchaseContent() {
        final StringBuilder purchaseContent = new StringBuilder();
        specializedPurchaseMap.forEach((key, value) ->
                purchaseContent.append(key).append(" $").append(value.toPlainString()).append("\n"));
        return purchaseContent.toString();
    }

    public String getPurchaseContent(Comparator<Map.Entry<String, BigDecimal>> sorter) {
        StringBuilder sb = new StringBuilder();
        specializedPurchaseMap.entrySet()
                .stream()
                .sorted(sorter)
                .forEach(entry -> sb.append(entry.getKey()).append(" $").append(entry.getValue().toPlainString()).append("\n"));
        return sb.toString();
    }

    public BigDecimal getPurchasesSum() {
        return specializedPurchaseMap.values()
                .stream()
                .reduce(BigDecimal.valueOf(0.00), BigDecimal::add, BigDecimal::add);
    }

    public void LoadData(String data) {
        Scanner scanner = new Scanner(data);

        while (scanner.hasNextLine()) {
            String lineText = scanner.nextLine();
            int currencyPos = lineText.lastIndexOf("$");
            if (currencyPos != -1) {
                String purchaseName = lineText.substring(0, currencyPos).trim();
                String purchasePrice = lineText.substring(currencyPos+1).trim();
                try {
                    addNewPurchase(purchaseName, BigDecimal.valueOf(Double.parseDouble(purchasePrice)).setScale(2, RoundingMode.UP));
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                    System.out.println("LineText:" + lineText);
                }
            }
        }
    }

    public void clearData() {
        specializedPurchaseMap.clear();
    }
}

class Food extends Purchases {
    public Food() {
        super();
    }
}

class Clothes extends Purchases {
    public Clothes() {
        super();
    }
}

class Entertainment extends Purchases {
    public Entertainment() {
        super();
    }
}

class Other extends Purchases {
    public Other() {
        super();
    }
}

/****************************************************************************/

/* The program should have the following categories:
   Food, Clothes, Entertainment, Other
 */
enum PurchasesCategory {
    Food, Clothes, Entertainment, Other
}

/* *************************** Menu enums ********************************* */

/**
 * 1. Add Income. We must track both our expenses and our income.
 * When this item is selected, the program should ask to enter
 * the amount of income.
 * 2. Add Purchase. This item should add a purchase to the list.
 * You have implemented this feature in the previous stage.
 * 3. Show the list of purchases. This menu item should display
 * a list of all expenses and incomes in the order they were made.
 * 4. Balance. Show the balance.
 * 5. Exit. Exit the program. Make this item under number 0, not number 5.
 */
enum MainMenu {
    AddIncome("Add Income", 1),
    AddPurchase("Add Purchase", 2),
    ShowPurchases("Show the list of purchases", 3),
    Balance("Balance", 4),
    Save("Save", 5),
    Load("Load", 6),
    Analyze("Analyze (Sort)", 7),
    Exit("Exit", 0);

    String menuText;
    int menuInputKey;

    MainMenu(String menuText, int menuKeyNumber) {
        this.menuText = menuText;
        this.menuInputKey = menuKeyNumber;
    }

    public static Optional<MainMenu> getMenuItem(int inputKey) {
        for (MainMenu item : MainMenu.values()) {
            if (item.menuInputKey == inputKey) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static void showMainMenu() {
        System.out.println("Choose your action");
        for (MainMenu item : MainMenu.values()) {
            System.out.format("%d) %s\n", item.menuInputKey, item.menuText);
        }
    }
}

enum PurchaseTypesMenuToAddPurchase {
    Food("Food", 1),
    Clothes("Clothes", 2),
    Entertainment("Entertainment", 3),
    Other("Other", 4),
    Back("Back", 5);

    String menuText;
    int menuInputKey;

    PurchaseTypesMenuToAddPurchase(String menuText, int menuKeyNumber) {
        this.menuText = menuText;
        this.menuInputKey = menuKeyNumber;
    }

    public static Optional<PurchaseTypesMenuToAddPurchase> getMenuItem(int inputKey) {
        for (PurchaseTypesMenuToAddPurchase item : PurchaseTypesMenuToAddPurchase.values()) {
            if (item.menuInputKey == inputKey) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static void showPurchaseTypesMenuToAddPurchase() {
        System.out.println("Choose the type of purchases");
        for (PurchaseTypesMenuToAddPurchase item : PurchaseTypesMenuToAddPurchase.values()) {
            System.out.format("%d) %s\n", item.menuInputKey, item.menuText);
        }
    }
}

enum PurchaseTypesMenuToShowPurchase {
    Food("Food", 1),
    Clothes("Clothes", 2),
    Entertainment("Entertainment", 3),
    Other("Other", 4),
    All("All", 5),
    Back("Back", 6);

    String menuText;
    int menuInputKey;

    PurchaseTypesMenuToShowPurchase(String menuText, int menuKeyNumber) {
        this.menuText = menuText;
        this.menuInputKey = menuKeyNumber;
    }

    public static Optional<PurchaseTypesMenuToShowPurchase> getMenuItem(int inputKey) {
        for (PurchaseTypesMenuToShowPurchase item : PurchaseTypesMenuToShowPurchase.values()) {
            if (item.menuInputKey == inputKey) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static void showPurchaseTypesMenuToShowPurchase() {
        System.out.println("Choose your action");
        for (PurchaseTypesMenuToShowPurchase item : PurchaseTypesMenuToShowPurchase.values()) {
            System.out.format("%d) %s\n", item.menuInputKey, item.menuText);
        }
    }
}

enum PurchasesSortMenu{
    SortALL("Sort all purchases", 1),
    SortByType("Sort by Type", 2),
    SortCertainType("Sort certain type", 3),
    Back("Back", 4);

    String menuText;
    int menuInputKey;

    PurchasesSortMenu(String menuText, int menuKeyNumber) {
        this.menuText = menuText;
        this.menuInputKey = menuKeyNumber;
    }

    public static Optional<PurchasesSortMenu> getMenuItem(int inputKey) {
        for (PurchasesSortMenu item : PurchasesSortMenu.values()) {
            if (item.menuInputKey == inputKey) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static void showPurchasesSortMenu() {
        System.out.println("How do you want to sort?");
        for (PurchasesSortMenu item : PurchasesSortMenu.values()) {
            System.out.format("%d) %s\n", item.menuInputKey, item.menuText);
        }
    }
}

enum PurchaseTypesMenuToSort {
    Food("Food", 1),
    Clothes("Clothes", 2),
    Entertainment("Entertainment", 3),
    Other("Other", 4);

    String menuText;
    int menuInputKey;

    PurchaseTypesMenuToSort(String menuText, int menuKeyNumber) {
        this.menuText = menuText;
        this.menuInputKey = menuKeyNumber;
    }

    public static Optional<PurchaseTypesMenuToSort> getMenuItem(int inputKey) {
        for (PurchaseTypesMenuToSort item : PurchaseTypesMenuToSort.values()) {
            if (item.menuInputKey == inputKey) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static void showPurchaseTypesMenuToSort() {
        System.out.println("Choose the type of purchases");
        for (PurchaseTypesMenuToSort item : PurchaseTypesMenuToSort.values()) {
            System.out.format("%d) %s\n", item.menuInputKey, item.menuText);
        }
    }
}

//************************************************************************