package net.platinumdigitalgroup.jvdf;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Brendan Heinonen
 */
public class TestParser {

    private VDFParser parser = new VDFParser();

    private static final String VDF_SIMPLE_TEST = "key value";
    private static final String VDF_SIMPLE_TEST_RESULT = "value";

    @Test
    public void testSimple() {
        Assert.assertEquals(VDF_SIMPLE_TEST_RESULT, parser.parse(VDF_SIMPLE_TEST).getString("key"));
    }

    private static final String VDF_QUOTES_TEST = "\"key with space\" \"value with space\"";
    private static final String VDF_QUOTES_TEST_RESULT = "value with space";

    @Test
    public void testQuotes() {
        Assert.assertEquals(VDF_QUOTES_TEST_RESULT, parser.parse(VDF_QUOTES_TEST).getString("key with space"));
    }

    private static final String VDF_ESCAPE_TEST = "\"key with \\\"\" \"value with \\\"\"";
    private static final String VDF_ESCAPE_TEST_RESULT = "value with \"";

    @Test
    public void testEscape() {
        Assert.assertEquals(VDF_ESCAPE_TEST_RESULT, parser.parse(VDF_ESCAPE_TEST).getString("key with \""));
    }

    private static final String VDF_UNDERFLOW_TEST = "root_node { child_node { key value }";

    @Test(expected = VDFParseException.class)
    public void testUnderflow() {
        parser.parse(VDF_UNDERFLOW_TEST);
    }

    private static final String VDF_OVERFLOW_TEST = "root_node { child_node { key value } } }";

    @Test(expected = VDFParseException.class)
    public void testOverflow() {
        parser.parse(VDF_OVERFLOW_TEST);
    }

    private static final String VDF_CHILD_TEST = "root { child { key value } }";
    private static final String VDF_CHILD_TEST_RESULT = "value";

    @Test
    public void testChild() {
        Assert.assertEquals(VDF_CHILD_TEST_RESULT, parser.parse(VDF_CHILD_TEST)
                .getSubNode("root")
                .getSubNode("child")
                .getString("key"));
    }

    @Test
    public void testSample() throws URISyntaxException, IOException {
        Path path = Paths.get(ClassLoader.getSystemResource("sample.txt").toURI());
        VDFNode root = parser.parse(Files.readAllLines(path).toArray(new String[]{}));

        Assert.assertEquals(VDFNode.class, root.get("root_node").getClass());
        Assert.assertEquals("value1", root
                .getSubNode("root_node")
                .getSubNode("first_sub_node")
                .getString("first"));
        Assert.assertEquals("value2", root
                .getSubNode("root_node")
                .getSubNode("first_sub_node")
                .getString("second"));
        Assert.assertEquals("value3", root
                .getSubNode("root_node")
                .getSubNode("second_sub_node")
                .getString("third"));
        Assert.assertEquals("value4", root
                .getSubNode("root_node")
                .getSubNode("second_sub_node")
                .getSubNode("third_sub_node")
                .getString("fourth"));
    }

}
