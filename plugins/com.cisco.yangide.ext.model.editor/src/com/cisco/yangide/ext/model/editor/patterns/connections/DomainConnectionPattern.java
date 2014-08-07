package com.cisco.yangide.ext.model.editor.patterns.connections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractConnectionPattern;
import org.eclipse.graphiti.pattern.IConnectionPattern;

import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;

public abstract class DomainConnectionPattern extends AbstractConnectionPattern implements IConnectionPattern {
    
    protected abstract EObject createEObject(ICreateConnectionContext context);

    @Override
    public Connection create(ICreateConnectionContext context) {
        Connection newConnection = null;

        // create the domain object connection here
        Object newDomainObjectConnetion = createEObject(context);

        AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
        addContext.setNewObject(newDomainObjectConnetion);
        newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);

        return newConnection;
    }

    @Override
    public PictogramElement add(IAddContext context) {     
        IAddConnectionContext addConContext = (IAddConnectionContext) context;
        return YangModelUIUtil.drawPictogramConnectionElement(addConContext, getFeatureProvider(), getCreateName());
    }
    
    @Override
    public boolean canAdd(IAddContext context) {
        return context instanceof IAddConnectionContext;
    }
    


    @Override
    public String getCreateDescription() {
        return "Creates new " + getCreateName() + " connection";
    }
}
