package net.platinumdigitalgroup.jvdf;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
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


    // Maven surefire bug can't include resources when forking is disabled
    private static final String VDF_SAMPLE = "\"root_node\"\n" +
            "{\n" +
            "    \"first_sub_node\"\n" +
            "    {\n" +
            "        \"first\"     \"value1\"\n" +
            "        \"second\"    \"value2\"\n" +
            "    }\n" +
            "    \"second_sub_node\"\n" +
            "    {\n" +
            "        \"third_sub_node\"\n" +
            "        {\n" +
            "            \"fourth\"    \"value4\"\n" +
            "        }\n" +
            "        \"third\"     \"value3\"\n" +
            "    }\n" +
            "}";
    private static final String VDF_SAMPLE_MULTIMAP = "\"root_node\"\n" +
            "{\n" +
            "    \"sub_node\"\n" +
            "    {\n" +
            "        \"key\"       \"value1\"\n" +
            "        \"key\"       \"value2\"\n" +
            "    }\n" +
            "    \"sub_node\"\n" +
            "    {\n" +
            "        \"key\"       \"value3\"\n" +
            "        \"key\"       \"value4\"\n" +
            "    }\n" +
            "}";

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

    private static final String VDF_ESCAPE_TEST = "\"key with \\\"\" \"value with \\\" \" \"newline\" \"val\\n\\nue\"";
    private static final String VDF_ESCAPE_TEST_RESULT = "value with \" ";

    @Test
    public void testEscape() {
        VDFNode node = parser.parse(VDF_ESCAPE_TEST);
        Assert.assertEquals(VDF_ESCAPE_TEST_RESULT, node.getString("key with \""));
        Assert.assertEquals("val\n\nue", node.getString("newline"));
    }

    private static final String VDF_NULLKV_TEST = "\"key\" \"\" \"spacer\" \"spacer\" \"\" \"value\"";

    @Test
    public void testNullKeyValue() {
        VDFNode node = parser.parse(VDF_NULLKV_TEST);
        Assert.assertEquals("", node.getString("key"));
        Assert.assertEquals("value", node.getString(""));
    }

    private static final String VDF_SUBSEQUENTKV_TEST = "\"key\"\"value\"";

    @Test
    public void testSubsequentKeyValue() {
        VDFNode node = parser.parse(VDF_SUBSEQUENTKV_TEST);
        Assert.assertEquals("value", node.getString("key"));
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
        VDFNode root = parser.parse(VDF_SAMPLE);

        Assert.assertEquals(VDFNode.class, root.getSubNode("root_node").getClass());
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

    @Test
    public void testDefaultValue() {
        VDFNode root = parser.parse(VDF_SAMPLE);
        Assert.assertEquals("not_existing", root
                .getString("this_key_does_not_exist", "not_existing"));
        Assert.assertEquals(1, root
                .getInt("this_key_does_not_exist", 1));
        Assert.assertEquals(0.123f, root
                .getFloat("this_key_does_not_exist", 0.123f), 0f);
        Assert.assertEquals(Long.MAX_VALUE, root
                .getLong("this_key_does_not_exist", Long.MAX_VALUE), 0f);
        Assert.assertEquals(Color.CYAN, root
                .getColor("this_key_does_not_exist", Color.CYAN));
    }

    @Test
    public void testMultimap() throws URISyntaxException, IOException {
        VDFNode root = parser.parse(VDF_SAMPLE_MULTIMAP);

        Assert.assertEquals(2, root.getSubNode("root_node").values("sub_node"));
        Assert.assertEquals("value1", root
                .getSubNode("root_node")
                .getSubNode("sub_node", 0)
                .getString("key"));
        Assert.assertEquals("value4", root
                .getSubNode("root_node")
                .getSubNode("sub_node", 1)
                .getString("key", 1));
    }

    @Test
    public void testReduceMultimap() throws URISyntaxException, IOException {
        VDFNode root = parser.parse(VDF_SAMPLE_MULTIMAP).reduce();

        Assert.assertEquals("value1", root
                .getSubNode("root_node")
                .getSubNode("sub_node")
                .getString("key", 0));

        Assert.assertEquals("value4", root
                .getSubNode("root_node")
                .getSubNode("sub_node")
                .getString("key", 3));
    }

}
