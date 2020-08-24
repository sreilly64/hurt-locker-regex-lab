import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class JerkSONParserTest {

    private String rawData;
    private JerkSONParser parser;
    private String testItem;

    @BeforeEach
    void setUp() throws Exception {
        Main main = new Main();
        rawData = main.readRawDataToString();
        testItem = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016";

        parser = new JerkSONParser(rawData);
    }

    @Test
    public void testJSONSeparator(){
        //given
        Integer expected = 28;
        //when
        List<String> jerkSONSArray = parser.separateJerkSONs(parser.getRawData());
        Integer actual = jerkSONSArray.size();
        System.out.println(jerkSONSArray);
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetRawData(){
        //given
        //when
        String actual = parser.getRawData();
        //then
        Assertions.assertEquals(rawData, actual);
    }

    @Test
    public void testGetName(){
        //given
        String expected = "Milk";
        //when
        String actual = parser.getName(testItem);
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetPrice(){
        //given
        Double expected = 3.23;
        //when
        Double actual = parser.getPrice(testItem);
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetType(){
        //given
        String expected = "Food";
        //when
        String actual = parser.getType(testItem);
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetExpiration(){
        //given
        LocalDate expected = LocalDate.of(2016, 1, 25);
        //when
        LocalDate actual = parser.getExpiration(testItem);
        System.out.println(actual.toString());
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testConvertJerkSONtoObj(){
        //given
        GroceryItem expected = new GroceryItem("Milk", 3.23, "Food", LocalDate.of(2016,1,25));
        //when
        GroceryItem actual = parser.convertJerkSONToObj(testItem);
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetJerkSONsAsObjs(){
        //given
        Integer expected = parser.separateJerkSONs(rawData).size();
        //when
        List<GroceryItem> objList = parser.getJerkSONsAsObjs(parser.separateJerkSONs(rawData));
        Integer actual = objList.size();
        //then
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void testRemoveErrors(){
        //given
        Integer expected = 24;
        //when
        List<GroceryItem> objList = parser.getJerkSONsAsObjs(parser.separateJerkSONs(rawData));
        List<GroceryItem> listWithNoErrors = parser.removeItemsWithErrors(objList);
        Integer actual = listWithNoErrors.size();
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveErrorsCount(){
        //given
        Integer expected = 4;
        //when
        List<GroceryItem> objList = parser.getJerkSONsAsObjs(parser.separateJerkSONs(rawData));
        List<GroceryItem> listWithNoErrors = parser.removeItemsWithErrors(objList);
        Integer actual = parser.getNumErrors();
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetItemPriceData(){
        //given
        Integer expected = 4;
        //when
        List<GroceryItem> objList = parser.getJerkSONsAsObjs(parser.separateJerkSONs(rawData));
        List<GroceryItem> listWithNoErrors = parser.removeItemsWithErrors(objList);
        Map<String, List<Double>> map = parser.getItemPriceData(listWithNoErrors);
        System.out.println(map.toString());
        Integer actual = map.values().size();
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testPrettyPrint(){
        //given
        List<GroceryItem> objList = parser.getJerkSONsAsObjs(parser.separateJerkSONs(rawData));
        List<GroceryItem> listWithNoErrors = parser.removeItemsWithErrors(objList);
        Map<String, List<Double>> map = parser.getItemPriceData(listWithNoErrors);
        String expected = "name: COOKIES        seen: 8 times\n" +
                "=============        =============\n" +
                "Price:   2.25        seen: 8 times\n" +
                "-------------        -------------\n" +
                "\n" +
                "name:    MILK        seen: 6 times\n" +
                "=============        =============\n" +
                "Price:   3.23        seen: 5 times\n" +
                "-------------        -------------\n" +
                "Price:   1.23        seen: 1 time\n" +
                "-------------        -------------\n" +
                "\n" +
                "name:   BREAD        seen: 6 times\n" +
                "=============        =============\n" +
                "Price:   1.23        seen: 6 times\n" +
                "-------------        -------------\n" +
                "\n" +
                "name:  APPLES        seen: 4 times\n" +
                "=============        =============\n" +
                "Price:   0.25        seen: 2 times\n" +
                "-------------        -------------\n" +
                "Price:   0.23        seen: 2 times\n" +
                "-------------        -------------\n" +
                "\n" +
                "Errors               seen: 4 times";
        //when
        String actual = parser.formatData(map);
        //then
        Assertions.assertEquals(expected, actual);
    }
}