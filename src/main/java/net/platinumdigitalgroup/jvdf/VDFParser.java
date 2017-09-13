package net.platinumdigitalgroup.jvdf;

/**
 * Parses VDF documents into iterable tree structures.
 * @author Brendan Heinonen
 */
public class VDFParser {

    private VDFPreprocessor preprocessor;

    /**
     * Initializes the VDFParser with a specific preprocessor
     * @param preprocessor the preprocessor to process input strings with
     */
    public VDFParser(VDFPreprocessor preprocessor) {
        this.preprocessor = preprocessor;
    }

    /**
     * Initializes the VDFParser with the default preprocessor (VDFPreprocessor)
     */
    public VDFParser() {
        this(new VDFPreprocessor());
    }

    /**
     * Parses a VDF document.
     * @param vdf the VDF document to parse
     * @return a VDFNode which represents the root of the VDF document
     */
    public VDFNode parse(String vdf) {
        return parse(vdf.split("\\n"));
    }

    /**
     * Parses a VDF document
     * @param vdf an array of lines representing a VDF document to parse
     * @return a VDFNode which represents the node of the VDF document
     */
    public VDFNode parse(String[] vdf) {
        String processed = preprocessor.process(vdf);
        VDFParserState state = new VDFParserState();

        char[] arr = processed.toCharArray();
        for(char c : arr) {
            switch (c) {
                case '"':
                    state.quote();
                    break;
                case ' ':
                    state.space();
                    break;
                case '\\':
                    state.escape();
                    break;
                case '{':
                    state.beginSubNode();
                    break;
                case '}':
                    state.endSubNode();
                    break;
                default:
                    state.character(c);
                    break;
            }
        }
        state.endParse();

        return state.root();
    }


}
