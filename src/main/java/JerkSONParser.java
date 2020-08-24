import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JerkSONParser {

    private String rawData;
    private Integer numErrors;

    public JerkSONParser(String rawData){
        this.rawData = rawData;
        this.numErrors = 0;
    }

    public String getRawData() {
        return rawData;
    }

    public Integer getNumErrors() {
        return numErrors;
    }

    public String getFormattedData() {
        List<String> jerkSONs = separateJerkSONs(rawData);
        List<GroceryItem> groceryObjects = getJerkSONsAsObjs(jerkSONs);
        groceryObjects = removeItemsWithErrors(groceryObjects);
        Map<String, List<Double>> itemPriceData = getItemPriceData(groceryObjects);
        return formatData(itemPriceData);
    }

    public String formatData(Map<String, List<Double>> itemPriceData) {
        StringBuilder sb = new StringBuilder();
        for(String item : itemPriceData.keySet()){
            sb.append(String.format("name:%8S        seen:%2d time%s\n",
                            item, itemPriceData.get(item).size(), isPlural(itemPriceData.get(item).size())));
            sb.append("=============        =============\n");

            List<Double> uniquePrices = itemPriceData.get(item).stream().distinct().collect(Collectors.toList());
            for(Double price : uniquePrices){
                Integer priceCount = getPriceCount(price, itemPriceData.get(item));
                sb.append(String.format("Price:%7.2f        seen:%2d time%s\n", price, priceCount, isPlural(priceCount)));
                sb.append("-------------        -------------\n");
            }
            sb.append("\n");
        }
        sb.append(String.format("Errors               seen:%2d time%s", this.numErrors, isPlural(this.numErrors)));
        return sb.toString();
    }

    private Integer getPriceCount(Double price, List<Double> priceList) {
        Integer count = 0;
        for(Double value: priceList){
            if(value.equals(price)){
                count++;
            }
        }
        return count;
    }

    public String isPlural(Integer i){
        if(i != 1){
            return "s";
        }else{
            return "";
        }
    }

    public Map<String, List<Double>> getItemPriceData(List<GroceryItem> groceryObjects) {
        Map<String, List<Double>> output = new HashMap<>();

        for(GroceryItem item: groceryObjects){
            Set<String> keySet = output.keySet();
            Boolean keyExists = false;
            String matchingKey = null;
            for(String key: keySet){
                Integer shorterLength = Math.min(item.getName().length(), key.length());
                if(key.substring(0,shorterLength).equalsIgnoreCase(item.getName())){
                    keyExists = true;
                    matchingKey = key;
                    break;
                }
            }
            if(keyExists){
                List<Double> updatedList = output.get(matchingKey);
                updatedList.add(item.getPrice());
                output.replace(matchingKey, updatedList);
            }else{
                output.put(item.getName(), new ArrayList<Double>(Arrays.asList(item.getPrice())));
            }
        }
        return output;
    }

    public List<GroceryItem> removeItemsWithErrors(List<GroceryItem> list) {
        List<GroceryItem> output = list;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getName().equals("") || list.get(i).getPrice() == -1d || list.get(i).getType().equals("") || list.get(i).getExpiration() == null){
                output.remove(list.get(i));
                this.numErrors++;
            }
        }
        return output;
    }

    public List<String> separateJerkSONs(String input) {
        Pattern groceryItem = Pattern.compile("([^#]+)(#{2}|\\z)");
        Matcher matcher = groceryItem.matcher(input);

        List<String> output = new ArrayList<>();
        while(matcher.find()){
            output.add(matcher.group(1));
        }
        return output;
    }

    public List<GroceryItem> getJerkSONsAsObjs(List<String> jerkSONs) {
        List<GroceryItem> groceryObjects = new ArrayList<>();
        jerkSONs.forEach(e -> groceryObjects.add(convertJerkSONToObj(e)));
        return groceryObjects;
    }

    public GroceryItem convertJerkSONToObj(String item){
        String name = getName(item);
        Double price = getPrice(item);
        String type = getType(item);
        LocalDate expir = getExpiration(item);
        return new GroceryItem(name, price, type, expir);
    }

    public LocalDate getExpiration(String item) {
        Pattern expiration = Pattern.compile("[eE][xX][pP][iI][rR][aA][tT][iI][oO][nN]:(\\d{1,2}/\\d{1,2}/\\d{4})");
        Matcher matcher = expiration.matcher(item);
        if(matcher.find()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(matcher.group(1), formatter);
        }
        return null;
    }

    public String getType(String item) {
        Pattern type = Pattern.compile("[tT][yY][pP][eE]:(\\w+)");
        Matcher matcher = type.matcher(item);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    public Double getPrice(String item) {
        Pattern price = Pattern.compile("[pP][rR][iI][cC][eE]:(\\d+.\\d{2})");
        Matcher matcher = price.matcher(item);
        if(matcher.find()){
            return Double.parseDouble(matcher.group(1));
        }
        return -1d;
    }

    public String getName(String item) {
        Pattern name = Pattern.compile("[nN][aA][mM][eE]:([a-zA-Z]+)");
        Matcher matcher = name.matcher(item);
        if(matcher.find()){
            return matcher.group(1);
        }
        return "";
    }
}
