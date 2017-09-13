package net.platinumdigitalgroup.jvdf;

import java.awt.*;
import java.util.TreeMap;

/**
 * An iterable tree structure that represents a set of key-value pairs in a VDF document.
 * @author Brendan Heinonen
 */
public class VDFNode extends TreeMap<String, Object> {

    /**
     * Fetches a string value by name.
     * @param key the key name
     * @return the string value of the specified key, or null if the key does not exist in this node
     */
    public String getString(String key) {
        return (String)this.get(key);
    }
    /**
     * Fetches an integer value by name.
     * @param key the key name
     * @return the int value of the specified key, or 0 if the key does not exist in this node
     */
    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    /**
     * Fetches a float value by name.
     * @param key the key name
     * @return the float value of the specified key, or 0.f if the key does not exist in this node
     */
    public float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    /**
     * Fetches a long value by name.
     * @param key the key name
     * @return the long value of the specified key, or 0 if the key does not exist in this node
     */
    public long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    /**
     * Fetches a pointer value by name.  Java does not have pointers, so this will really return a long.
     * @param key the key name
     * @return the long value of the specified key, or 0 if the key does not exist in this node
     */
    public long getPointer(String key) {
        return Long.parseLong(getString(key), 16);
    }

    /**
     * Proxy for getString for spec compliance reasons.
     * @param key the key name
     * @return the string value of the specified key, or null if the key does not exist in this node
     */
    public String getWideString(String key) {
        return getString(key);
    }

    /**
     * Fetches a color value by name.
     * @param key the key name
     * @return the AWT color value of the specified key, or null if the key does not exist in this node
     */
    public Color getColor(String key) {
        return Color.getColor(getString(key));
    }

    /**
     * Fetches a VDF child node by name.
     * @param key the key name
     * @return a VDFNode instance of the specified key, or null if the key does not exist in this node
     */
    public VDFNode getSubNode(String key) {
        return (VDFNode)this.get(key);
    }

}
