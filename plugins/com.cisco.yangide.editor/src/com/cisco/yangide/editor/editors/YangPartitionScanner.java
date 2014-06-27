package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.*;

/**
 * @author Alexey Kholupko
 */
public class YangPartitionScanner extends RuleBasedPartitionScanner {

    public final static String YANG_STRING = "__yang_string";
    public final static String YANG_COMMENT = "__yang_comment";

    public YangPartitionScanner() {

        List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

        IToken comment = new Token(YANG_COMMENT);
        rules.add(new MultiLineRule("/*", "*/", comment));
        rules.add(new EndOfLineRule("//", comment));

        IToken multiLinestring = new Token(YANG_STRING);
        // TODO escaping " or ' in each other sequence
        rules.add(new MultiLineRule("\"", "\"", multiLinestring, '\\'));
        rules.add(new MultiLineRule("'", "'", multiLinestring, '\\'));

        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }
}
