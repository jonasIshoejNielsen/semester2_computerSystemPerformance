
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.software.Config;
import org.simple.software.LineStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestLineStorage {
    public void init() {
        Config.setAllToFalse();
    }

    @Test
    void testDoWordCountInputMap() {
        init();
        String inputString = "<p><b>Foo hEj</br> hej</p> </b>";
        Map<String, Integer> inputMap    = new HashMap<>();
        inputMap.put("Foo", 4);
        inputMap.put("br", 1);
        inputMap.put("p", 2);
        LineStorage ls  = new LineStorage(inputString, new Random().nextInt());

        ls.doWordCount(inputMap, true);
        Assertions.assertEquals(5, inputMap.get("Foo"));
        Assertions.assertEquals(1, inputMap.get("hEj"));
        Assertions.assertEquals(1, inputMap.get("hej"));
        Assertions.assertEquals(1, inputMap.get("br"));
        Assertions.assertEquals(2, inputMap.get("p"));
    }

    @Test
    void testDoWordCountMultipleLinks() {
        init();
        String inputString = "<a href='xyz' title='FooBar'>foo<a title='foo' href='xyz' > foo</a> bar</a>";
        Map<String, Integer> inputMap    = new HashMap<>();

        LineStorage ls  = new LineStorage(inputString, new Random().nextInt());
        ls.doWordCount(inputMap, true);
        Assertions.assertEquals(3, inputMap.get("foo"));
        Assertions.assertEquals(1, inputMap.get("bar"));
        Assertions.assertEquals(1, inputMap.get("FooBar"));
    }

    @Test
    void testDoWordCountLinkMultipleTitles() {
        init();
        String inputString = "<a title='FooBar' title='FooBar' href='xyz' title='FooBar'>foo</a>";
        Map<String, Integer> inputMap    = new HashMap<>();

        LineStorage ls  = new LineStorage(inputString, new Random().nextInt());
        ls.doWordCount(inputMap, true);
        Assertions.assertEquals(1, inputMap.get("foo"));
        Assertions.assertEquals(3, inputMap.get("FooBar"));
    }

    @Test
    void testDoWordCountLinkWithOutQuetes() {
        init();
        String inputString = "<a href=\"/w/index.php?title=Distributed_computing&amp;action=edit&amp;section=3\" title=\"Edit section: History\">edit</a>";
        Map<String, Integer> inputMap    = new HashMap<>();

        LineStorage ls  = new LineStorage(inputString, new Random().nextInt());
        ls.doWordCount(inputMap, true);
        Assertions.assertEquals(1, inputMap.get("Distributed"));
        Assertions.assertEquals(1, inputMap.get("computing"));
        Assertions.assertEquals(1, inputMap.get("Edit"));
        Assertions.assertEquals(1, inputMap.get("section"));
        Assertions.assertEquals(1, inputMap.get("History"));
        Assertions.assertEquals(1, inputMap.get("edit"));
    }

}
