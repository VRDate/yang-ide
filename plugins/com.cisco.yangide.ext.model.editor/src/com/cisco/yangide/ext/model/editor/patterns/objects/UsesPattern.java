package com.cisco.yangide.ext.model.editor.patterns.objects;

import org.eclipse.emf.ecore.EClass;

import com.cisco.yangide.ext.model.Uses;
import com.cisco.yangide.ext.model.editor.util.IYangImageConstants;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class UsesPattern extends DomainObjectPattern {

    @Override
    public String getCreateImageId() {
        return IYangImageConstants.IMG_USES_PROPOSAL;
    }

    @Override
    public String getCreateName() {
        return "uses";
    }

    @Override
    protected EClass getObjectEClass() {
        return YangModelUtil.MODEL_PACKAGE.getUses();
    }

    @Override
    protected String getHeaderText(Object obj) {
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getUses(), obj)) {
            return getCreateName() + " " + YangModelUtil.getQName((Uses) obj);
        }
        return super.getHeaderText(obj);
    }

}
