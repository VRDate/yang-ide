package com.cisco.yangide.ext.model.editor.property;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import com.cisco.yangide.ext.model.NamedNode;
import com.cisco.yangide.ext.model.editor.util.PropertyUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class GeneralTabNameSection extends GFPropertySection implements ITabbedPropertyConstants {

    private Text nameText;
    private NamedNode node; 
    
    @Override
    public void createControls(Composite parent,
        TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);
 
        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
        Composite composite = factory.createFlatFormComposite(parent);
        FormData data;
 
        nameText = factory.createText(composite, "");
        data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, VSPACE);
        nameText.setLayoutData(data);
        nameText.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                if (null != node) {
                    node.setName(((Text) e.widget).getText());
                    YangModelUIUtil.updatePictogramElement(getDiagramTypeProvider().getFeatureProvider(), 
                            YangModelUIUtil.getBusinessObjectPropShape(getDiagramTypeProvider().getFeatureProvider(), node, PropertyUtil.OBJECT_HEADER_TEXT_SHAPE_KEY));
                }
                
            }
        });
 
        CLabel valueLabel = factory.createCLabel(composite, "Name:");
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(nameText, -HSPACE);
        data.top = new FormAttachment(nameText, 0, SWT.CENTER);
        valueLabel.setLayoutData(data);
    }
 
    @Override
    public void refresh() {
        PictogramElement pe = getSelectedPictogramElement();
        if (pe != null) {
            Object bo = Graphiti.getLinkService()
                 .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null || !YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNamedNode(), bo)) {
                return;
            }
            node = ((NamedNode) bo);
            nameText.setText(node.getName() == null ? "" : node.getName());
        }
    }
}
