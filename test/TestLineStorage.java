
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.software.HelperFunctions;
import org.simple.software.meaurements.Config;
import org.simple.software.LineStorage;

import java.io.IOException;
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
        ls.putResultValue("foo", 4);
        ls.putResultValue("br", 1);
        ls.putResultValue("p", 2);

        ls.doWordCount(true);
        Assertions.assertEquals(5, ls.getResults().get("foo"));
        Assertions.assertEquals(2, ls.getResults().get("hej"));
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
        Assertions.assertEquals(1, ls.getResults().get("foobar"));
    }

    @Test
    void testDoWordCountLinkMultipleTitles() {
        init();
        String inputString = "<a title='FooBar' title='FooBar' href='xyz' title='FooBar'>foo</a>";
        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("foo"));
        Assertions.assertEquals(3, ls.getResults().get("foobar"));
    }

    @Test
    void testDoWordCountLinkWithOutQuetes() {
        init();
        String inputString = "<a href=\"/w/index.php?title=Distributed_computing&amp;action=edit&amp;section=3\" title=\"Edit section: History\">edit</a>";

        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("distributed"));
        Assertions.assertEquals(1, ls.getResults().get("computing"));
        Assertions.assertEquals(2, ls.getResults().get("edit"));
        Assertions.assertEquals(1, ls.getResults().get("section"));
        Assertions.assertEquals(1, ls.getResults().get("history"));
    }

    @Test
    void testDoWordCountLinkEndInTile() {
        init();
        String inputString = "<a href=\"/wiki/Distributed database\" title=\"Di";

        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("di"));
    }
    @Test
    void testDoWordCountLinkEndInEmptyTile() {
        init();
        String inputString = "<a href=\"/w/index.php?title=Distributed computing&amp;action=edit&amp;section=8\" title=";

        LineStorage ls  = makeLineStorage(inputString);
        ls.doWordCount(true);
        Assertions.assertEquals(1, ls.getResults().get("distributed"));
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
    @Test
    void testPBTDocumentGeneration() throws IOException {
        init();
        Random rand = new Random();
        for (int i = 0; i < 10_000; i++) {
            int length      = rand.nextInt(50);
            int file        = rand.nextInt(2) + 1;
            int seed        = (int) (Math.random()*10000);
            String docu1    = HelperFunctions.generateDocument(length, file, seed);
            LineStorage ls  = makeLineStorage(docu1);
            ls.doWordCount(true);
        }
        Assertions.assertEquals(true, true);
    }

}
