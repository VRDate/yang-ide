/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.core.search.processing.JobManager;

/**
 * @author Konstantin Zaitsev
 * @date Jun 25, 2014
 */
@SuppressWarnings("restriction")
public class IndexManager extends JobManager {

    @Override
    public String processName() {
        return "Yang indexer";
    }

    /**
     * @param res
     */
    public void indexAll(IProject project) {
        // TODO Auto-generated method stub
        
    }

    public synchronized void removeIndexFamily(IPath path) {
    }
}
