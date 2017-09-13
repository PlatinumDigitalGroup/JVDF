package net.platinumdigitalgroup.jvdf;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brendan Heinonen
 */
public class TestPreprocessor {

    private VDFPreprocessor preprocessor = new VDFPreprocessor();

    private static final String VDF_WHITESPACE_TEST = "      \"Key\"                    \"Value\"       \n";
    private static final String VDF_WHITESPACE_TEST_RESULT = "\"Key\" \"Value\"";

    @Test
    public void testWhitespace() {
        Assert.assertEquals(VDF_WHITESPACE_TEST_RESULT, preprocessor.process(VDF_WHITESPACE_TEST));
    }

    private static final String VDF_COMMENT_TEST = "key// This comment will be stripped\nvalue/* This comment will be stripped\n// This line will be stripped";
    private static final String VDF_COMMENT_TEST_RESULT = "key value ";

    @Test
    public void testComments() {
        Assert.assertEquals(VDF_COMMENT_TEST_RESULT, preprocessor.process(VDF_COMMENT_TEST));
    }

    private static final String VDF_MINIFY_TEST = "\"root_node\"\n{\n   \"second_node\"\n   {\n     \"key\" \"value\"\n     \"key2\"    \"value2\"\n    }\n}";
    private static final String VDF_MINIFY_TEST_RESULT = "\"root_node\" { \"second_node\" { \"key\" \"value\" \"key2\" \"value2\" } }";

    @Test
    public void testMinification() {
        Assert.assertEquals(VDF_MINIFY_TEST_RESULT, preprocessor.process(VDF_MINIFY_TEST));
    }

    private static final String VDF_NEWLINE_DELIM_TEST = "key\nvalue\n\"key\"\n\"value\"";
    private static final String VDF_NEWLINE_DELIM_TEST_RESULT = "key value \"key\" \"value\"";

    @Test
    public void testNewlineDelimited() {
        Assert.assertEquals(VDF_NEWLINE_DELIM_TEST_RESULT, preprocessor.process(VDF_NEWLINE_DELIM_TEST));
    }

}
