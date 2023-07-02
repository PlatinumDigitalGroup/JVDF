package net.platinumdigitalgroup.jvdf;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AreteS0ftware
 */
public class TestWriter {

    private final VDFParser parser = new VDFParser();
    private final VDFWriter writer = new VDFWriter();

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

    private static final String test = "\"keyvalues\"\n " +
            "{\n" +
            "    \"Material\" {\n" +
            "        \"MaterialData\" {\n" +
            "            \"Vertices\" {\n" +
            "                \"Color\" {\n" +
            "                    \"vertex\" \"0.92156863 0.03529412 0.03529412 1.0\"\n" +
            "                    \"vertex\" \"0.2 0.0 1.0 1.0\"\n" +
            "                    \"vertex\" \"0.05490196 1.0 0.039215688 1.0\"\n" +
            "                    \"vertex\" \"0.9490196 0.9490196 0.047058824 1.0\"\n" +
            "                    \"vertex\" \"0.92156863 0.10980392 0.8392157 1.0\"\n" +
            "                    \"vertex\" \"1.0 1.0 1.0 1.0\"\n" +
            "                }\n" +
            "                \"Position\" {\n" +
            "                    \"vertex\" \"-3.99 -2.99\"\n" +
            "                    \"vertex\" \"-2.99 2.01\"\n" +
            "                    \"vertex\" \"1.01 0.01\"\n" +
            "                    \"vertex\" \"3.01 -1.99\"\n" +
            "                    \"vertex\" \"2.01 -4.99\"\n" +
            "                    \"vertex\" \"-1.8385 -6.1870003\"\n" +
            "                }\n" +
            "            }\n" +
            "            \"offset\" \"1.0 1.0\"\n" +
            "        }\n" +
            "        \"filePath\" \"/home/arete/Badlogic.jpg\"\n" +
            "        \"ppm\" \"100.0\"\n" +
            "        \"uWrap\" \"Repeat\"\n" +
            "        \"vWrap\" \"MirroredRepeat\"\n" +
            "    }\n" +
            "}";

    @Test
    public void testSample() {
        VDFNode node = parser.parse(test);
        String result = writer.write(node);
        Assert.assertEquals(test, result);
    }
}
