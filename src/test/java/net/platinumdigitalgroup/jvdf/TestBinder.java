package net.platinumdigitalgroup.jvdf;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brendan Heinonen
 */
public class TestBinder {

    private VDFParser parser = new VDFParser();

    private class SimplePOJO {
        @VDFBindField
        public String key;
    }

    @Test
    public void testSimpleBinder() {
        SimplePOJO pojo = new SimplePOJO();
        new VDFBinder(parser.parse("key value")).bindTo(pojo);
        Assert.assertEquals("value", pojo.key);
    }

    private class NamedPOJO {
        @VDFBindField(keyName = "key_name")
        public String key;

        @VDFBindField(keyName = "second_key")
        public String secondKey;
    }

    @Test
    public void testNamedBinder() {
        NamedPOJO pojo = new NamedPOJO();
        new VDFBinder(parser.parse("key_name value second_key value2")).bindTo(pojo);
        Assert.assertEquals("value", pojo.key);
        Assert.assertEquals("value2", pojo.secondKey);
    }

    private class TypedPOJO {
        @VDFBindField
        public int bint;

        @VDFBindField
        public float bfloat;

        @VDFBindField
        public long blong;

        @VDFBindField
        public String string;
    }

    @Test
    public void testTypedBinder() {
        TypedPOJO pojo = new TypedPOJO();
        new VDFBinder(parser.parse("bint 123 bfloat 1.23 blong 123 string \"made it\"")).bindTo(pojo);
        Assert.assertEquals("made it", pojo.string);
        Assert.assertEquals(123, pojo.bint);
        Assert.assertEquals(1.23f, pojo.bfloat, 0.1f);
        Assert.assertEquals(123L, pojo.blong);
    }


    public class RecursivePOJO {
        public class RecursiveChild {
            public class RecursiveSecondChild {
                @VDFBindField
                String key;
            }

            @VDFBindField
            RecursiveSecondChild child;
        }

        @VDFBindField
        RecursiveChild root;
    }

    @Test
    public void testRecursiveBinder() {
        RecursivePOJO pojo = new RecursivePOJO();

        new VDFBinder(parser.parse("root { child { key value } }")).bindTo(pojo);
        Assert.assertEquals("value", pojo.root.child.key);
    }

}
