import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JerkSONParser {

    //private Map<String, Double> items;
    //private List<GroceryItem> items2;
    //private List<String> jerkSONs;
    private String rawData;

    public JerkSONParser(String rawData){
        this.rawData = rawData;
    }

    public String getRawData() {
        return rawData;
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

    public String formattedData() {
        List<String> jerkSONs = separateJerkSONs(rawData);

        return null;
    }
}
