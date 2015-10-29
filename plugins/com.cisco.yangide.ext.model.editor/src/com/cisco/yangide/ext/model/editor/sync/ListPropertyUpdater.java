/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.sync;

import com.cisco.yangide.core.dom.ContrainerSchemaNode;

/**
 * @author Konstantin Zaitsev
 * @date Aug 28, 2014
 */
public class ListPropertyUpdater extends SourceNodePropertyUpdater<ContrainerSchemaNode> {

    public ListPropertyUpdater(DiagramModelAdapter adapter) {
        super(adapter);
    }

    @Override
    protected boolean isHandleProperty(String name) {
        return "config".equals(name) || "max-elements".equals(name) || "min-elements".equals(name)
                || "ordered-by".equals(name) || "units".equals(name);
    }
}