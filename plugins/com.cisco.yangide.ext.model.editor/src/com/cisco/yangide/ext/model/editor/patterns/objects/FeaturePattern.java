package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.editor.util.IYangImageConstants;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class FeaturePattern extends DomainObjectPattern {

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getFeature();
    }

    @Override
    public String getCreateImageId() {
        return IYangImageConstants.IMG_FEATURE_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "feature";
    }

    
}
