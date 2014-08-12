package com.cisco.yangide.ext.model.editor.util;

import java.util.Comparator;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

import com.cisco.yangide.ext.model.NamedNode;

public class YangModelUIUtil {
    
    private YangModelUIUtil() {
        super();
    }
    
    public static final int DEFAULT_WIDTH = 150;
    public static final int DEFAULT_TEXT_HEIGHT = 16;
    public static final int DEFAULT_V_ALIGN = 5;
    public static final int DEFAULT_H_ALIGN = 2;
    
    public static final int DEFAULT_BOX_ANCHOR_RADIUS = 2;

    public static final int DEFAULT_COMPOSITE_HEIGHT = 30;
    
    private static final ShapeVerticalComparator COMPARATOR = new ShapeVerticalComparator();
    
    private static class ShapeVerticalComparator implements Comparator<Shape> {

        @Override
        public int compare(Shape o1, Shape o2) {
            if (null == o1 && null == o2) {
                return 0;
            }
            if (null != o1 && null == o2) {
                return 1;
            }
            if (null == o1 && null != o2) {
                return -1;
            }
            return o1.getGraphicsAlgorithm().getY() > o2.getGraphicsAlgorithm().getY() ? 1 : o1.getGraphicsAlgorithm().getY() == o2.getGraphicsAlgorithm().getY() ? 0 : -1;
        }

    }
    
    public static void sortPictogramElements(EList<Shape> elements) {
        ECollections.sort(elements, COMPARATOR);
    }

    
    public static void layoutPictogramElement(PictogramElement diagram, IFeatureProvider fp) {
        LayoutContext lc = new LayoutContext(diagram);
        fp.layoutIfPossible(lc);
    }
   
   
    public static Anchor getChopboxAnchor(AnchorContainer ac) {
        return Graphiti.getPeService().getChopboxAnchor(ac);
    }
    
    public static Anchor getBoxRelativeAnchor(AnchorContainer ac) {
        for (Anchor a : ac.getAnchors()) {
            if (a instanceof BoxRelativeAnchor) {
                return a;
            }
        }
        return null;
    }
    
    
    public static PictogramElement drawObject(EObject obj, ContainerShape cs, IFeatureProvider fp, int x, int y) {
        AddContext ac = new AddContext();
        ac.setTargetContainer(cs);
        ac.setLocation(x, y);
        ac.setNewObject(obj);
        return fp.addIfPossible(ac);
    }
    
    public static Connection drawPictogramConnectionElement(IAddConnectionContext context, IFeatureProvider fp, String title) {
        EObject addedEReference = (EObject) context.getNewObject();
        IPeCreateService peCreateService = Graphiti.getPeCreateService();

        Connection connection = peCreateService.createFreeFormConnection(fp.getDiagramTypeProvider().getDiagram());
        connection.setStart(context.getSourceAnchor());
        connection.setEnd(context.getTargetAnchor());

        IGaService gaService = Graphiti.getGaService();
        Polyline polyline = gaService.createPlainPolyline(connection);
        polyline.setStyle(StyleUtil.getStyleForDomainObject(fp.getDiagramTypeProvider().getDiagram()));

        // create link and wire it
        fp.link(connection, addedEReference);
        
        // add dynamic text decorator for the reference name
        ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
        Text text = gaService.createPlainText(textDecorator);
        text.setStyle(StyleUtil.getStyleForTextDecorator(fp.getDiagramTypeProvider().getDiagram()));
        gaService.setLocation(text, 10, 0);

        // set reference name in the text decorator
        text.setValue(title);
        
        // add static graphical decorators (composition and navigable)
        createConnectionArrow(connection, fp);
        
        return connection;
    }
    
    public static Polyline createConnectionArrow(Connection connection, IFeatureProvider fp) {
        ConnectionDecorator cd = Graphiti.getPeCreateService().createConnectionDecorator(connection, false, 1.0, true);
        Polyline polyline = Graphiti.getGaCreateService().createPlainPolyline(cd,
                new int[] { -10, 5, 0, 0, -10, -5});
        polyline.setStyle(StyleUtil.getStyleForDomainObject(fp.getDiagramTypeProvider().getDiagram()));
        return polyline;
    }    
    public static void drawPictogramElementHeader(ContainerShape containerShape, IAddContext context, IFeatureProvider fp, String imageId, String title, int width, int height) {
        // create shape for text
        final Shape imageShape = Graphiti.getPeCreateService().createShape(containerShape, false);
        // create and set text graphics algorithm
        final Image image = Graphiti.getGaService().createImage(imageShape, imageId);
        image.setHeight(height);
        image.setWidth(height);
        image.setStretchH(true);
        image.setStretchH(true);
        image.setProportional(true);
        Graphiti.getGaService().setLocationAndSize(image, DEFAULT_V_ALIGN, 0, height, height);
        final Shape textShape = Graphiti.getPeCreateService().createShape(containerShape, false);
        Text text;
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getNamedNode(), context.getNewObject())) {
            if (null == ((NamedNode) context.getNewObject()).getName()) {
                ((NamedNode) context.getNewObject()).setName("<name>");
            }
            text = Graphiti.getGaService().createPlainText(textShape, ((NamedNode) context.getNewObject()).getName());
            fp.link(textShape, new Object[] { context.getNewObject(), YangModelUtil.MODEL_PACKAGE.getNamedNode_Name() });
        } else {
            text = Graphiti.getGaService().createPlainText(textShape, title);
        }
        text.setStyle(StyleUtil.getStyleForDomainObjectText(fp.getDiagramTypeProvider().getDiagram()));
        Graphiti.getGaService().setLocationAndSize(text, height + DEFAULT_V_ALIGN, 0, width, height);       
        
    }
    public static void drawBoxRelativeAnchor(ContainerShape containerShape, IAddContext context, IFeatureProvider fp) {
        final BoxRelativeAnchor boxAnchor = Graphiti.getPeCreateService().createBoxRelativeAnchor(containerShape);
        boxAnchor.setRelativeWidth(1.0);
        boxAnchor.setRelativeHeight(0.5);

        boxAnchor.setReferencedGraphicsAlgorithm(containerShape.getGraphicsAlgorithm());
        final Ellipse ellipse = Graphiti.getGaService().createPlainEllipse(boxAnchor);

        Graphiti.getGaService().setLocationAndSize(ellipse, -2 * DEFAULT_BOX_ANCHOR_RADIUS,
                -2 * DEFAULT_BOX_ANCHOR_RADIUS, 2 * DEFAULT_BOX_ANCHOR_RADIUS, 2 * DEFAULT_BOX_ANCHOR_RADIUS);
        ellipse.setStyle(StyleUtil.getStyleForDomainObject(fp.getDiagramTypeProvider().getDiagram()));
    }
    public static ContainerShape drawPictogramElement(IAddContext context, IFeatureProvider fp, String imageId, String title) {
        ContainerShape result = null;
        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(), context.getNewObject())) {
            result = drawCompositePictogramElement(context, fp, imageId, title);
        } else {
            result = drawAttributePictogramElement(context, fp, imageId, title);
        }
        fp.link(result, context.getNewObject());
        ChopboxAnchor anchor = Graphiti.getPeCreateService().createChopboxAnchor(result);
        anchor.setReferencedGraphicsAlgorithm(result.getGraphicsAlgorithm());
        // create BoxRelativeAnchor for references
        if (context.getNewObject() instanceof EObject && YangModelUtil.hasConnection((EObject) context.getNewObject())){
            drawBoxRelativeAnchor(result, context, fp);
        }
        // call the layout feature
        if (!(context.getTargetContainer() instanceof Diagram)) {
            layoutPictogramElement(context.getTargetContainer(), fp);
        }
        return result;
    }
    
    public static ContainerShape drawCompositePictogramElement(IAddContext context, IFeatureProvider fp, String imageId, String title) {

        ContainerShape targetShape = context.getTargetContainer();
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        IGaService gaService = Graphiti.getGaService();

        ContainerShape containerShape = peCreateService.createContainerShape(targetShape, true);
   
        final int width = context.getWidth() <= 0 ? DEFAULT_WIDTH : context.getWidth();
        final int height = context.getHeight() <= 0 ? DEFAULT_COMPOSITE_HEIGHT : context.getHeight();

        RoundedRectangle roundedRectangle = gaService.createPlainRoundedRectangle(containerShape, 5, 5);
        roundedRectangle.setStyle(StyleUtil.getStyleForDomainObject(fp.getDiagramTypeProvider().getDiagram()));
        gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), width, height);

        // create shape for line
        final Shape shape = peCreateService.createContainerShape(containerShape, false);

        // create and set graphics algorithm
        final Polyline polyline = gaService.createPlainPolyline(shape, new int[] { 0, DEFAULT_TEXT_HEIGHT, width,
                DEFAULT_TEXT_HEIGHT });
        polyline.setStyle(StyleUtil.getStyleForDomainObject(fp.getDiagramTypeProvider().getDiagram()));
        drawPictogramElementHeader(containerShape, context, fp, imageId, title, width, DEFAULT_TEXT_HEIGHT);            
        return containerShape;

    }
    
    public static ContainerShape drawAttributePictogramElement(IAddContext context, IFeatureProvider fp, String imageId, String title) {
        ContainerShape targetShape = context.getTargetContainer();
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        IGaService gaService = Graphiti.getGaService();

        ContainerShape containerShape = peCreateService.createContainerShape(targetShape, true);

        final int width = context.getWidth() <= 0 ? DEFAULT_WIDTH : context.getWidth();
        final int height = context.getHeight() <= 0 ? DEFAULT_TEXT_HEIGHT : context.getHeight();

        GraphicsAlgorithm rectangle;
        if (context.getTargetContainer() instanceof Diagram) {
            rectangle = gaService.createPlainRoundedRectangle(containerShape, 5, 5);
        } else {
            rectangle = gaService.createInvisibleRectangle(containerShape);
        }
        rectangle.setStyle(StyleUtil.getStyleForDomainObject(fp.getDiagramTypeProvider().getDiagram()));
        gaService.setLocationAndSize(rectangle, context.getX(), context.getY(), width, height);
        drawPictogramElementHeader(containerShape, context, fp, imageId, title, width, DEFAULT_TEXT_HEIGHT);        
        return containerShape;

    }
}
