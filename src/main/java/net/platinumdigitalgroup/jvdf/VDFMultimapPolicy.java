package net.platinumdigitalgroup.jvdf;

/**
 * Describes what the parser should do when it encounters a multimapped key.
 * @author Brendan Heinonen
 */
public enum VDFMultimapPolicy {

    /**
     * Accept the value and push it to the back of the value array.
     */
    DEFAULT,

    /**
     * Silently reject the value.
     */
    REJECT,

    /**
     * Throw a VDFParseException.
     */
    EXCEPT,

    /**
     * Reduce multimapped keys as they are encountered.
     */
    AUTO_REDUCE,

    /**
     * Reduce keys at the very end of parsing.
     */
    AUTO_REDUCE_END

}
