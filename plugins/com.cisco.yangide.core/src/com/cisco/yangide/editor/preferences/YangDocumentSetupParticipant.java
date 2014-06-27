/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */ 
package com.cisco.yangide.editor.preferences;

/**
 * @author Alexey Kholupko
 *
 */

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

import com.cisco.yangide.editor.editors.YangPartitionScanner;

/**
 * The document setup participant from Ant. 
 */
public class YangDocumentSetupParticipant  implements IDocumentSetupParticipant {

    /**
     * The name of the Ant partitioning. Again, clarify whats the purpose of partition scanner 
     * and why can it be used in preview instead of real scanner
     * @since 3.0
     */
    
    public YangDocumentSetupParticipant() {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
     */
    public void setup(IDocument document) {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3= (IDocumentExtension3) document;
            IDocumentPartitioner partitioner = createDocumentPartitioner();
            extension3.setDocumentPartitioner(IDocumentExtension3.DEFAULT_PARTITIONING, partitioner);
            partitioner.connect(document);
        } 
    }
    
    private IDocumentPartitioner createDocumentPartitioner() {
        return new FastPartitioner(
                new YangPartitionScanner(), new String[]{
                    YangPartitionScanner.YANG_COMMENT,
                    YangPartitionScanner.YANG_STRING});
    }
}
