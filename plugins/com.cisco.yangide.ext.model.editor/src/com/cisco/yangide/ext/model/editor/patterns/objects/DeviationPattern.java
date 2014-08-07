package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.IYangImageConstants;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class DeviationPattern extends DomainObjectPattern {

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getDeviation();
    }

    @Override
    public String getCreateImageId() {
        return IYangImageConstants.IMG_DEVIATION_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "deviation";
    }
    
    

}
