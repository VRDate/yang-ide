/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.dom;

import java.util.Collection;

/**
 * @author Konstantin Zaitsev
 * @date Jun 26, 2014
 */
public abstract class ASTNode {

    /** Common field "description" for Yang statement. */
    private String description;

    /** Common field "reference" for Yang statement. */
    private String reference;

    /**
     * A character index into the original source string, or <code>-1</code> if no source position
     * information is available for this node; <code>-1</code> by default.
     */
    private int startPosition = -1;

    /**
     * A character length, or <code>0</code> if no source position information is recorded for this
     * node; <code>0</code> by default.
     */
    private int length = 0;

    /** Line number. */
    private int lineNumber = -1;

    /** Start position of AST node body '{' or '"'. */
    private int bodyStartPosition = -1;

    /** Parent AST node. */
    private ASTNode parent = null;

    public ASTNode(ASTNode parent) {
        this.parent = parent;
        if (parent instanceof ASTCompositeNode) {
            ((ASTCompositeNode) parent).getChildren().add(this);
        }
    }

    /**
     * @return the startPosition
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * @param startPosition the startPosition to set
     */
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    public int getEndPosition() {
        return this.startPosition + this.length;
    }

    /**
     * @return the parent
     */
    public ASTNode getParent() {
        return parent;
    }
    
    /**
     * @return the parent module of this node
     */
    public ASTNode getModule() {
        ASTNode module = this;
        while (module.getParent() != null)
            module = module.getParent();
        if(module instanceof Module)
            return module;
        
        return null;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the bodyStartPosition
     */
    public int getBodyStartPosition() {
        return bodyStartPosition;
    }

    /**
     * @param bodyStartPosition the bodyStartPosition to set
     */
    public void setBodyStartPosition(int bodyStartPosition) {
        this.bodyStartPosition = bodyStartPosition;
    }

    /**
     * @return the bodyLenght
     */
    public int getBodyLength() {
        return (bodyStartPosition >= 0 && startPosition >= 0) ? (length - (bodyStartPosition - startPosition) + 1) : 0;
    }

    /**
     * @return the bodyEndPosition
     */
    public int getBodyEndPosition() {
        return bodyStartPosition + getBodyLength();
    }

    /**
     * @return the name
     */
    public abstract String getNodeName();

    public abstract void accept(ASTVisitor visitor);

    final void acceptChild(ASTVisitor visitor, ASTNode child) {
        if (child == null) {
            return;
        }
        visitor.preVisit(child);
        child.accept(visitor);
    }

    final void acceptChildren(ASTVisitor visitor, Collection<? extends ASTNode> children) {
        for (ASTNode child : children) {
            visitor.preVisit(child);
            child.accept(visitor);
        }
    }
    
    public boolean isShowedInOutline() {
        return true;
    }
}
