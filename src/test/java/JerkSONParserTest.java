import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

class JerkSONParserTest {

    private String rawData;
    private JerkSONParser parser;

    @BeforeEach
    void setUp() throws Exception {
        Main main = new Main();
        rawData = main.readRawDataToString();

        parser = new JerkSONParser(rawData);
    }

    @Test
    public void testJSONSeparator(){
        //given
        Integer expected = 28;
        //when
        List<String> jerkSONSArray = parser.separateJerkSONs(parser.getRawData());
        Integer actual = jerkSONSArray.size();
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
}