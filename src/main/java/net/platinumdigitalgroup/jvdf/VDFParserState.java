/*
Copyright 2017 Platinum Digital Group LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package net.platinumdigitalgroup.jvdf;

import java.util.Stack;

/**
 * Holds the internal state of the VDF parser.
 * @author Brendan Heinonen
 */
public class VDFParserState {

    /**
     * The root node is the base of the VDF document.  All subnodes are children of the root node.
     */
    private final VDFNode rootNode;

    /**
     * Since a VDF document can have a virtually unlimited amount of subnodes, we use a stack datastructure to represent
     * the level of subnodes the parser state is currently at. When we enter a subnode, a new VDFNode is pushed to the
     * top of the stack.  As we leave subnodes, the stack is popped.  The bottom of the stack should always point to
     * the root node.
     */
    private final Stack<VDFNode> childStack = new Stack<>();

    /**
     * This flag represents if the parser is currently iterating over a character preceded with an open quote. Since
     * whitespaces are considered control characters in the VDF spec, quotes are used to enclose tokens containing
     * a whitespace.
     */
    private boolean quoteState = false;

    /**
     * This flag represents if the previous character was an escape character.
     */
    private boolean escapePending = false;

    /**
     * This flag represents if a key has been specified and the next token is a value.
     */
    private boolean valuePending = false;

    /**
     * This flag represents if the current string is intentionally a null string
     */
    private boolean nullString = false;

    /**
     * When valuePending is true, the key name that the next value corresponds to.
     */
    private String keyName = "";

    /**
     * General-use string buffer that represents the last token. This is cleared after every control character.
     */
    private final StringBuilder currentString = new StringBuilder();

    /**
     * Initializes the parser state with a starting root node.
     * @param root an existing root node
     */
    public VDFParserState(VDFNode root) {
        this.rootNode = root;
        this.childStack.push(root);
    }

    /**
     * Initializes the parser state.
     */
    public VDFParserState() {
        this(new VDFNode());
    }


    /**
     * Returns the root VDFNode for this parser state.
     * @return the VDFNode representing the root of the VDF document
     */
    public VDFNode root() {
        return rootNode;
    }

    /**
     * Returns the VDFNode the parser is currently on.
     * @return the VDFNode that the parser is currently writing key/values to
     */
    public VDFNode current() {
        return childStack.peek();
    }

    /**
     * Handle a quote character.
     */
    public void quote() {
        if(escapePending) {
            // If there's an escape pending, this quote is escaped
            character('"');
        } else {
            // If there's no escape pending, we're entering or leaving a quoted string
            quoteState = !quoteState;

            if (quoteState) {
                // If we're starting a new quoted string, reset the current string
                resetString();
            } else {
                // Otherwise, the string has been terminated

                if(currentString.length() == 0)
                    nullString = true;

                // Simulate a space at the end of a quote
                space();
            }
        }
    }

    /**
     * Handle a whitespace character.
     */
    public void space() {
        // If we're inside a quoted string, append space to the current string
        if(quoteState) {
            character(' ');
        } else {
            // Ignore meaningless spaces
            if(currentString.length() == 0 && !nullString)
                return;

            valuePending = !valuePending;

            // If valuePending was toggled to true, the last string was the key name
            // If valuePending was toggled to false, the last
            if(valuePending) {
                // Store the key name
                keyName = currentString.toString();
                //System.out.println(keyName);
            } else {
                // Store the value into the current node
                currentValue(keyName, currentString.toString());
            }

            resetString();
        }
    }

    /**
     * Handle an escape character.
     */
    public void escape() {
        // This shouldn't just set escapePending to true because \\ is a valid escape sequence.
        escapePending = !escapePending;

        // If escape was just disabled, we know that this character must be \, which is the escape sequence \\
        if(!escapePending) {
            character('\\');
        }
    }

    /**
     * Handle a miscellaneous non-control character.
     * @param c a non-control character
     */
    public void character(char c) {
        // Check specced escape sequence
        if(escapePending) {
            if(c == 'n')
                c = '\n';
        }

        // If the character is not a control character, append it to the current string
        currentString.append(c);

        // Reset the escape state
        escapePending = false;
    }

    /**
     * Start a subnode context.
     */
    public void beginSubNode() {
        if(escapePending || quoteState) {
            character('{');
        } else {
            // Create new subnode
            VDFNode node = new VDFNode();

            // Set the current node's value
            currentValue(keyName, node);

            // Push node onto child node stack
            childStack.push(node);

            resetKV();
        }
    }

    /**
     * End a subnode context.
     */
    public void endSubNode() {
        if(escapePending || quoteState) {
            character('}');
        } else {
            // At this point, we're done adding key/values, so reset the string buffer and KV state
            resetKV();

            // Popping the root node means there were more ended subnodes than subnodes that existed
            if (rootNode == childStack.pop()) {
                throw new VDFParseException("The root node was popped. There was a subnode mismatch (misplaced '}'?).");
            }
        }
    }


    /**
     * Finalizes the parser. Called at the end of parsing.
     */
    public void endParse() {
        // Call space to commit the current KV pair
        space();

        if(childStack.peek() != rootNode) {
            throw new VDFParseException("The root node was not at the top of the stack at the end of parsing. " +
                    "There was a subnode mismatch (misplaced '{'?)");
        }
    }

    /**
     * Pushes a key/value pair to the current node.
     * @param key the key
     * @param val the value
     */
    private void currentValue(String key, Object val) {
        current().put(key, val);
    }

    /**
     * Clears the string buffer.
     */
    private void resetString() {
        currentString.setLength(0);
        nullString = false;
    }

    /**
     * Resets the KV state.
     */
    private void resetKV() {
        resetString();
        valuePending = false;
    }

}
