/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.core.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.opendaylight.yangtools.antlrv4.code.gen.YangParser.Yang_version_stmtContext;

/**
 * Reusable checks of basic constraints on yang statements
 *
 * @author Konstantin Zaitsev
 * @date Jul 9, 2014
 */
public class BasicValidations {

    static final String SUPPORTED_YANG_VERSION = "1";

    /**
     * It isn't desirable to create instance of this class.
     */
    private BasicValidations() {
    }

    static void checkNotPresentBoth(ParseTree parent, Class<? extends ParseTree> childType1,
            Class<? extends ParseTree> childType2) {
        if (BasicValidations.checkPresentChildOfTypeSafe(parent, childType1, true)
                && BasicValidations.checkPresentChildOfTypeSafe(parent, childType2, false)) {
            ValidationUtil.ex(parent, ValidationUtil.f("(In (sub)module:%s) Both %s and %s statement present in %s:%s",
                    ValidationUtil.getRootParentName(parent), ValidationUtil.getSimpleStatementName(childType1),
                    ValidationUtil.getSimpleStatementName(childType2),
                    ValidationUtil.getSimpleStatementName(parent.getClass()), ValidationUtil.getName(parent)));
        }
    }

    static void checkOnlyPermittedValues(ParseTree ctx, Set<String> permittedValues) {
        String mandatory = ValidationUtil.getName(ctx);
        String rootParentName = ValidationUtil.getRootParentName(ctx);

        if (!permittedValues.contains(mandatory)) {
            ValidationUtil.ex(ctx, ValidationUtil.f(
                    "(In (sub)module:%s) %s:%s, illegal value for %s statement, only permitted:%s", rootParentName,
                    ValidationUtil.getSimpleStatementName(ctx.getClass()), mandatory,
                    ValidationUtil.getSimpleStatementName(ctx.getClass()), permittedValues));
        }
    }

    static void checkUniquenessInNamespace(ParseTree stmt, Set<String> uniques) {
        String name = ValidationUtil.getName(stmt);
        String rootParentName = ValidationUtil.getRootParentName(stmt);

        if (uniques.contains(name)) {
            ValidationUtil.ex(stmt, ValidationUtil.f("(In (sub)module:%s) %s:%s not unique in (sub)module",
                    rootParentName, ValidationUtil.getSimpleStatementName(stmt.getClass()), name));
        }
        uniques.add(name);
    }

    /**
     * Check if only one module or submodule is present in session(one yang file)
     */
    static void checkOnlyOneModulePresent(ParseTree ctx, String moduleName, String globalId) {
        if (globalId != null) {
            ValidationUtil.ex(ctx, ValidationUtil.f("Multiple (sub)modules per file"));
        }
    }

    static void checkPresentYangVersion(ParseTree ctx, String moduleName) {
        if (!checkPresentChildOfTypeSafe(ctx, Yang_version_stmtContext.class, true)) {
            ValidationUtil.ex(ctx, ValidationUtil.f(
                    "Yang version statement not present in module:%s, Validating as yang version:%s", moduleName,
                    SUPPORTED_YANG_VERSION));
        }
    }

    static void checkDateFormat(ParseTree stmt, DateFormat format) {
        try {
            format.parse(ValidationUtil.getName(stmt));
        } catch (ParseException e) {
            String exceptionMessage = ValidationUtil.f(
                    "(In (sub)module:%s) %s:%s, invalid date format expected date format is:%s",
                    ValidationUtil.getRootParentName(stmt), ValidationUtil.getSimpleStatementName(stmt.getClass()),
                    ValidationUtil.getName(stmt), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            ValidationUtil.ex(stmt, exceptionMessage);
        }
    }

    private static Pattern identifierPattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_.-]*");

    static void checkIdentifier(ParseTree statement) {
        checkIdentifierInternal(statement, ValidationUtil.getName(statement));
    }

    static void checkIdentifierInternal(ParseTree statement, String name) {
        if (!identifierPattern.matcher(name).matches()) {

            String message = ValidationUtil.f("%s statement identifier:%s is not in required format:%s",
                    ValidationUtil.getSimpleStatementName(statement.getClass()), name, identifierPattern.toString());
            String parent = ValidationUtil.getRootParentName(statement);
            message = parent.equals(name) ? message : ValidationUtil.f("(In (sub)module:%s) %s", parent, message);

            if (statement instanceof ParserRuleContext) {
                message = "Error on line " + ((ParserRuleContext) statement).getStart().getLine() + ": " + message;
            }

            ValidationUtil.ex(statement, message);
        }
    }

    private static Pattern prefixedIdentifierPattern = Pattern.compile("(.+):(.+)");

    static void checkPrefixedIdentifier(ParseTree statement) {
        checkPrefixedIdentifierInternal(statement, ValidationUtil.getName(statement));
    }

    private static void checkPrefixedIdentifierInternal(ParseTree statement, String id) {
        Matcher matcher = prefixedIdentifierPattern.matcher(id);

        if (matcher.matches()) {
            try {
                // check prefix
                checkIdentifierInternal(statement, matcher.group(1));
                // check ID
                checkIdentifierInternal(statement, matcher.group(2));
            } catch (YangValidationException e) {
                ValidationUtil.ex(statement,
                        ValidationUtil.f("Prefixed id:%s not in required format, details:%s", id, e.getMessage()));
            }
        } else {
            checkIdentifierInternal(statement, id);
        }
    }

    static void checkSchemaNodeIdentifier(ParseTree statement) {
        String id = ValidationUtil.getName(statement);

        try {
            for (String oneOfId : id.split("/")) {
                if (oneOfId.isEmpty()) {
                    continue;
                }
                checkPrefixedIdentifierInternal(statement, oneOfId);
            }
        } catch (YangValidationException e) {
            ValidationUtil.ex(statement,
                    ValidationUtil.f("Schema node id:%s not in required format, details:%s", id, e.getMessage()));
        }
    }

    private interface MessageProvider {
        String getMessage();
    }

    static void checkPresentChildOfTypeInternal(ParseTree parent, Set<Class<? extends ParseTree>> expectedChildType,
            MessageProvider message, boolean atMostOne) {
        if (!checkPresentChildOfTypeSafe(parent, expectedChildType, atMostOne)) {
            String str = atMostOne ? "(Expected exactly one statement) " + message.getMessage() : message.getMessage();
            ValidationUtil.ex(parent, str);
        }
    }

    static void checkPresentChildOfType(final ParseTree parent, final Class<? extends ParseTree> expectedChildType,
            boolean atMostOne) {

        // Construct message in checkPresentChildOfTypeInternal only if
        // validaiton fails, not in advance
        MessageProvider message = new MessageProvider() {

            @Override
            public String getMessage() {
                String message = ValidationUtil.f("Missing %s statement in %s:%s",
                        ValidationUtil.getSimpleStatementName(expectedChildType),
                        ValidationUtil.getSimpleStatementName(parent.getClass()), ValidationUtil.getName(parent));

                String root = ValidationUtil.getRootParentName(parent);
                message = ValidationUtil.getName(parent).equals(root) ? message : ValidationUtil.f(
                        "(In (sub)module:%s) %s", root, message);
                return message;
            }
        };

        Set<Class<? extends ParseTree>> expectedChildTypeSet = new HashSet<>();
        expectedChildTypeSet.add(expectedChildType);

        checkPresentChildOfTypeInternal(parent, expectedChildTypeSet, message, atMostOne);
    }

    /**
     * Implementation of interface <code>MessageProvider</code> for method
     * {@link BasicValidations#checkPresentChildOfTypeSafe(ParseTree, Set, boolean)
     * checkPresentChildOfTypeSafe(ParseTree, Set, boolean) * }
     */
    private static class MessageProviderForSetOfChildTypes implements MessageProvider {

        private Set<Class<? extends ParseTree>> expectedChildTypes;
        private ParseTree parent;

        public MessageProviderForSetOfChildTypes(Set<Class<? extends ParseTree>> expectedChildTypes, ParseTree parent) {
            this.expectedChildTypes = expectedChildTypes;
            this.parent = parent;
        }

        @Override
        public String getMessage() {
            StringBuilder childTypes = new StringBuilder();
            String orStr = " OR ";
            for (Class<? extends ParseTree> type : expectedChildTypes) {
                childTypes.append(ValidationUtil.getSimpleStatementName(type));
                childTypes.append(orStr);
            }
            String message = ValidationUtil.f("Missing %s statement in %s:%s", childTypes.toString(),
                    ValidationUtil.getSimpleStatementName(parent.getClass()), ValidationUtil.getName(parent));

            String root = ValidationUtil.getRootParentName(parent);
            message = ValidationUtil.getName(parent).equals(root) ? message : ValidationUtil.f(
                    "(In (sub)module:%s) %s", root, message);
            return message;
        }
    };

    static void checkPresentChildOfTypes(final ParseTree parent,
            final Set<Class<? extends ParseTree>> expectedChildTypes, boolean atMostOne) {

        // Construct message in checkPresentChildOfTypeInternal only if
        // validaiton fails, not in advance
        MessageProvider message = new MessageProviderForSetOfChildTypes(expectedChildTypes, parent);
        checkPresentChildOfTypeInternal(parent, expectedChildTypes, message, atMostOne);
    }

    static boolean checkPresentChildOfTypeSafe(ParseTree parent, Set<Class<? extends ParseTree>> expectedChildType,
            boolean atMostOne) {

        int foundChildrenOfType = ValidationUtil.countPresentChildrenOfType(parent, expectedChildType);

        return atMostOne ? foundChildrenOfType == 1 ? true : false : foundChildrenOfType != 0 ? true : false;
    }

    static boolean checkPresentChildOfTypeSafe(ParseTree parent, Class<? extends ParseTree> expectedChildType,
            boolean atMostOne) {

        int foundChildrenOfType = ValidationUtil.countPresentChildrenOfType(parent, expectedChildType);

        return atMostOne ? foundChildrenOfType == 1 ? true : false : foundChildrenOfType != 0 ? true : false;
    }

    static List<String> getAndCheckUniqueKeys(ParseTree ctx) {
        String key = ValidationUtil.getName(ctx);
        ParseTree parent = ctx.getParent();
        String rootParentName = ValidationUtil.getRootParentName(ctx);

        List<String> keyList = ValidationUtil.listKeysFromId(key);
        Set<String> duplicates = ValidationUtil.getDuplicates(keyList);

        if (duplicates.size() != 0) {
            ValidationUtil.ex(ctx, ValidationUtil.f("(In (sub)module:%s) %s:%s, %s:%s contains duplicates:%s",
                    rootParentName, ValidationUtil.getSimpleStatementName(parent.getClass()),
                    ValidationUtil.getName(parent), ValidationUtil.getSimpleStatementName(ctx.getClass()), key,
                    duplicates));
        }
        return keyList;
    }
}
