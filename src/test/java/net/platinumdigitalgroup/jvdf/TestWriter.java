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

    @Test
    public void testSample() {
        VDFNode node1 = parser.parse(VDF_SAMPLE);
        String result = writer.write(node1, true);
        VDFNode node2 = parser.parse(result);
        //assertStringEquals(VDF_SAMPLE, result);
        assertNodesEquals(node1, node2);
    }

    @Test
    public void testSampleMultimap() {
        VDFNode node1 = parser.parse(VDF_SAMPLE_MULTIMAP);
        String result = writer.write(node1, true);
        VDFNode node2 = parser.parse(result);
        //assertStringEquals(VDF_SAMPLE_MULTIMAP, result);
        assertNodesEquals(node1, node2);
    }

    /*
    private void assertStringEquals(String string1, String string2) {
        String[] split1 = string1.split("\n");
        String[] split2 = string2.split("\n");
        for (int i = 0; i < split1.length; i++) {
            Assert.assertEquals(split1[i].replace(" ", ""), split2[i].replace(" ", ""));
        }
    }
     */

    private void assertNodesEquals(VDFNode node1, VDFNode node2) {
        for (String key : node1.keySet()) {
            Object[] node1values = node1.get(key);
            Object[] node2values = node2.get(key);
            for (int i = 0; i < node1values.length; i++) {
                Object obj1 = node1values[i];
                Object obj2 = node2values[i];
                if (!(obj1 instanceof VDFNode) && !(obj2 instanceof VDFNode)) {
                    Assert.assertEquals(obj1, obj2);
                }
                else {
                    assertNodesEquals((VDFNode) obj1, (VDFNode) obj2);
                }
            }
        }
    }

}
