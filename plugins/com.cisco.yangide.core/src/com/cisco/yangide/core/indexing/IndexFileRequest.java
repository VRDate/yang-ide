/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.core.indexing;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.model.YangFileInfo;

/**
 * @author Konstantin Zaitsev
 * @date Jul 1, 2014
 */
public class IndexFileRequest extends IndexRequest {

    private IFile file;

    public IndexFileRequest(IFile file, IndexManager manager) {
        super(file.getFullPath(), manager);
        this.file = file;
    }

    @Override
    public boolean execute(IProgressMonitor progressMonitor) {
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) {
            return true;
        }
        try {
            // remove previously indexed file
            manager.remove(file);

            System.err.println(toString());
            YangFileInfo info = (YangFileInfo) YangCorePlugin.createYangFile(file).getElementInfo(progressMonitor);
            manager.addModule(info.getModule(), file.getFullPath(), "");
        } catch (YangModelException e) {
            YangCorePlugin.log(e);
        }
        return true;
    }

    @Override
    public String toString() {
        return "indexing " + file.getFullPath();
    }
}
