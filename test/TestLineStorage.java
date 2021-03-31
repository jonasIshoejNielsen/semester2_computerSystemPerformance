
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.software.LineStorage;

import java.util.HashMap;
import java.util.Random;

public class TestLineStorage {
    @Test
    void testDoWordCountInputMap() {
        var inputString = "<p><b>Foo hEj</br> hej</p> </b>";
        var inputMap    = new HashMap<String, Integer>();
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
    void testDoWordCountLink() {
        var inputString = "<a href='xyz' title='FooBar'>foo<a title='foo' href='xyz' > foo</a> bar</a>";
        var inputMap    = new HashMap<String, Integer>();

        LineStorage ls  = new LineStorage(inputString, new Random().nextInt());
        ls.doWordCount(inputMap, true);
        Assertions.assertEquals(3, inputMap.get("foo"));
        Assertions.assertEquals(1, inputMap.get("bar"));
        Assertions.assertEquals(1, inputMap.get("FooBar"));
    }

}
