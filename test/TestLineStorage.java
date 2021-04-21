
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.software.meaurements.Config;
import org.simple.software.LineStorage;

import java.util.Random;

public class TestLineStorage {
    public void init() {
        Config.setAllToFalse();
    }

    LineStorage makeLineStorage (String inputString) {
        return new LineStorage(inputString, new Random().nextInt(), null,0);
    }

    @Test
    void testDoWordCountInputMap() {
        init();
        String inputString = "<p><b>Foo hEj</br> hej</p> </b>";
        LineStorage ls  = makeLineStorage(inputString);
        ls.putResultValue("Foo", 4);
        ls.putResultValue("br", 1);
        ls.putResultValue("p", 2);

        ls.doWordCount(true);
        Assertions.assertEquals(5, ls.getResults().get("Foo"));
        Assertions.assertEquals(1, ls.getResults().get("hEj"));
        Assertions.assertEquals(1, ls.getResults().get("hej"));
        Assertions.assertEquals(1, ls.getResults().get("br"));
        Assertions.assertEquals(2, ls.getResults().get("p"));
    }

    @Test
    void testDoWordCountMultipleLinks() {
        init();
        String inputString = "<a href='xyz' title='FooBar'>foo<a title='foo' href='xyz' > foo</a> bar</a>";
        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(3, ls.getResults().get("foo"));
        Assertions.assertEquals(1, ls.getResults().get("bar"));
        Assertions.assertEquals(1, ls.getResults().get("FooBar"));
    }

    @Test
    void testDoWordCountLinkMultipleTitles() {
        init();
        String inputString = "<a title='FooBar' title='FooBar' href='xyz' title='FooBar'>foo</a>";
        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("foo"));
        Assertions.assertEquals(3, ls.getResults().get("FooBar"));
    }

    @Test
    void testDoWordCountLinkWithOutQuetes() {
        init();
        String inputString = "<a href=\"/w/index.php?title=Distributed_computing&amp;action=edit&amp;section=3\" title=\"Edit section: History\">edit</a>";

        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("Distributed"));
        Assertions.assertEquals(1, ls.getResults().get("computing"));
        Assertions.assertEquals(1, ls.getResults().get("Edit"));
        Assertions.assertEquals(1, ls.getResults().get("section"));
        Assertions.assertEquals(1, ls.getResults().get("History"));
        Assertions.assertEquals(1, ls.getResults().get("edit"));
    }

    @Test
    void testDoWordCountLinkEndInTile() {
        init();
        String inputString = "<a href=\"/wiki/Distributed database\" title=\"Di";

        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("Di"));
    }
    @Test
    void testDoWordCountLinkEndInEmptyTile() {
        init();
        String inputString = "<a href=\"/w/index.php?title=Distributed computing&amp;action=edit&amp;section=8\" title=";

        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("Distributed"));
        Assertions.assertEquals(1, ls.getResults().get("computing"));
    }
    @Test
    void testDoWordCountLinkEndInEmptyTileNoEquals() {
        init();
        String inputString = "hi<a href=\"/wiki/Client%E2%80%93server model\" title";

        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("hi"));
    }

}
