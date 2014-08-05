/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ext.refactoring.rename;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

import com.cisco.yangide.core.dom.ASTNamedNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.indexing.ElementIndexReferenceInfo;
import com.cisco.yangide.core.indexing.ElementIndexReferenceType;
import com.cisco.yangide.core.model.YangModelManager;

/**
 * @author Konstantin Zaitsev
 * @date Jul 30, 2014
 */
public abstract class YangRenameProcessor extends RenameProcessor {
    private String newName;
    private boolean updateReferences;
    private IFile file;
    private ASTNamedNode node;

    public YangRenameProcessor(ASTNamedNode node) {
        this.node = node;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @param selection
     */
    public void setUpdateReferences(boolean updateReferences) {
        this.updateReferences = updateReferences;
    }

    /**
     * @return the updateReferences
     */
    public boolean isUpdateReferences() {
        return updateReferences;
    }

    /**
     * @return the file
     */
    public IFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(IFile file) {
        this.file = file;
    }

    @Override
    public String getProcessorName() {
        return "Rename grouping element";
    }

    @Override
    public boolean isApplicable() throws CoreException {
        return node != null;
    }

    @Override
    public Object[] getElements() {
        return new Object[] { node };
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants)
            throws CoreException {
        return null;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        Module module = (Module) node.getModule();
        QName qname = new QName(module.getName(), null, node.getName(), module.getRevision());
        ElementIndexReferenceInfo[] infos = YangModelManager.getIndexManager().searchReference(qname,
                getReferenceType(), getFile().getProject());

        CompositeChange composite = new CompositeChange("Rename");
        composite.markAsSynthetic();

        HashMap<String, TextChange> map = new HashMap<>();
        addEdit(composite, map, file.getFullPath().toString(), node.getNameStartPosition(), node.getNameLength(),
                getNewName());
        for (ElementIndexReferenceInfo info : infos) {
            String name = getNewName();
            if (!info.getPath().equals(file.getFullPath().toString())) {
                name = info.getReference().getPrefix() + ":" + getNewName();
            }
            addEdit(composite, map, info.getPath(), info.getStartPosition(), info.getLength(), name);
        }
        return composite;
    }

    private void addEdit(CompositeChange composite, HashMap<String, TextChange> map, String path, int pos, int len,
            String name) {
        TextChange change = getChangeOrCreate(composite, map, path);
        ReplaceEdit child = new ReplaceEdit(pos, len, name);
        change.getEdit().addChild(child);
        change.addTextEditGroup(new TextEditGroup("Update reference", child));
    }

    private TextChange getChangeOrCreate(CompositeChange composite, HashMap<String, TextChange> map, String path) {
        if (!map.containsKey(path)) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
            TextChange change = null;
            // if (document != null) {
            // change = new DocumentChange(file.getName(), document);
            // } else {
            change = new TextFileChange("Rename element in file", file);
            change.setTextType("yang");
            // }
            MultiTextEdit edit = new MultiTextEdit();
            change.setEdit(edit);
            change.setKeepPreviewEdits(true);
            composite.add(change);
            map.put(path, change);
        }
        return map.get(path);
    }

    protected abstract ElementIndexReferenceType getReferenceType();

    /**
     * @return the node
     */
    public ASTNamedNode getNode() {
        return node;
    }
    // /**
    // * @param file
    // * @return look up opened editors and try to get document.
    // */
    // private IDocument getDocumentByPath(IFile file) {
    // IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    // if (window != null) {
    // IWorkbenchPage page = window.getActivePage();
    // if (page != null) {
    // for (IEditorReference ref : page.getEditorReferences()) {
    // if (YangEditor.EDITOR_ID.equals(ref.getId())) {
    // try {
    // IEditorInput input = ref.getEditorInput();
    // if (input instanceof IFileEditorInput && ((IFileEditorInput) input).getFile().equals(file)) {
    // IEditorPart editor = ref.getEditor(false);
    // if (editor != null) {
    // return ((YangEditor) editor).getDocument();
    // }
    // }
    // } catch (PartInitException e) {
    // // ignore
    // }
    // }
    // }
    // }
    // }
    // return null;
    // }
}
