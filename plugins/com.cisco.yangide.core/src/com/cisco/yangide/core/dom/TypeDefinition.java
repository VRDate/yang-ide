/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.core.dom;

/**
 * @author Konstantin Zaitsev
 * @date   Jul 2, 2014
 */
public class TypeDefinition extends ASTNamedNode {

    /**
     * @param parent
     * @param name
     */
    public TypeDefinition(ASTNode parent) {
        super(parent, null);
    }

    @Override
    public String getNodeName() {
        return "typedef";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}