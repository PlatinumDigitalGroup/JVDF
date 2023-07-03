package net.platinumdigitalgroup.jvdf;

import java.util.Map;
import java.util.Set;

/**
 * Writes multiple VDF nodes into a human readable String.
 * @author AreteS0ftware
 */
public class VDFWriter {

    public VDFWriter() {
    }

    public String write(VDFNode root) {
        return write(root, false);
    }

    public String write(VDFNode root, boolean newLineOnNode) {
        return write(root, new StringBuilder(), new StringBuilder(), newLineOnNode);
    }

    private String write(VDFNode root, StringBuilder whitespace, StringBuilder builder, boolean newLineOnNode) {
        Set<Map.Entry<String, Object[]>> entries = root.entrySet();
        for (Map.Entry<String, Object[]> entry : entries) {
            String key = entry.getKey();
            Object[] value = entry.getValue();
            for (int i = 0; i < value.length; i++) {
                builder.append(whitespace);
                builder.append("\"").append(key).append("\"");
                builder.append(" ");
                Object obj = value[i];
                if (!(obj instanceof VDFNode)) {
                    builder.append("\"").append(obj).append("\"");
                    if (i < value.length - 1) {
                        builder.append("\n");
                    }
                }
                else {
                    VDFNode node = (VDFNode) obj;
                    if (newLineOnNode) {
                        builder.append("\n");
                        builder.append(whitespace);
                    }
                    builder.append("{");
                    if (!node.isEmpty()) {
                        builder.append("\n");
                        whitespace.append("    ");
                    }
                    write(node, whitespace, builder, newLineOnNode);
                    if (!node.isEmpty()) {
                        whitespace.setLength(whitespace.length() - 4);
                        builder.append(whitespace);
                    }
                    builder.append("}");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

}