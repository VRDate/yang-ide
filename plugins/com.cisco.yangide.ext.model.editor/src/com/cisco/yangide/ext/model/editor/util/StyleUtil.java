package com.cisco.yangide.ext.model.editor.util;

import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.internal.Workbench;

@SuppressWarnings("restriction")
public class StyleUtil {

    private StyleUtil() {
        super();
    }

    public static final IColorConstant DOMAIN_OBJECT_TEXT_FOREGROUND = new ColorConstant(0, 0, 0);
    public static final IColorConstant DOMAIN_OBJECT_FOREGROUND = new ColorConstant(137, 173, 213);// new
    // ColorConstant(98,
    // 131,
    // 167);
    public static final IColorConstant DOMAIN_OBJECT_BACKGROUND = new ColorConstant(187, 218, 247);

    public static final IColorConstant DOMAIN_OBJECT_TYPE_TEXT_COLOR = new ColorConstant(149, 125, 71);

    public static Style getStyleForCommonValues(Diagram diagram) {
        final String styleId = "COMMON-VALUES";
        IGaService gaService = Graphiti.getGaService();

        // Is style already persisted?
        Style style = gaService.findStyle(diagram, styleId);

        if (style == null) { // style not found - create new style
            style = gaService.createPlainStyle(diagram, styleId);
            setCommonValues(style);
        }
        return style;
    }

    @SuppressWarnings("restriction")
    public static Style getStyleForDomainObject(Diagram diagram) {
        final String styleId = "DOMAIL-OBJECT";
        IGaService gaService = Graphiti.getGaService();

        // this is a child style of the common-values-style
        Style parentStyle = getStyleForCommonValues(diagram);
        Style style = gaService.findStyle(parentStyle, styleId);

        if (style == null) { // style not found - create new style
            style = gaService.createPlainStyle(parentStyle, styleId);
            style.setFilled(true);
            style.setForeground(gaService.manageColor(diagram, DOMAIN_OBJECT_FOREGROUND));

            // no background color here, we have a gradient instead
            gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteGlossAdaptions());
        }
        return style;

    }

    public static Style getStyleForDomainObjectText(Diagram diagram) {
        final String styleId = "DOMAIL-OBJECT-TEXT";
        IGaService gaService = Graphiti.getGaService();

        // this is a child style of the common-values-style
        Style parentStyle = getStyleForCommonValues(diagram);
        Style style = gaService.findStyle(parentStyle, styleId);

        if (style == null) { // style not found - create new style
            style = gaService.createPlainStyle(parentStyle, styleId);
            setCommonTextValues(diagram, gaService, style);
            FontData font = getDefaultFont();
            style.setFont(gaService.manageFont(diagram, font.getName(), font.getHeight(), false, false));
        }
        return style;
    }

    public static Style getStyleForDomainObjectTypeText(Diagram diagram) {
        final String styleId = "DOMAIL-OBJECT-TYPE-TEXT";
        IGaService gaService = Graphiti.getGaService();

        // this is a child style of the common-values-style
        Style parentStyle = getStyleForCommonValues(diagram);
        Style style = gaService.findStyle(parentStyle, styleId);

        if (style == null) { // style not found - create new style
            style = gaService.createPlainStyle(parentStyle, styleId);
            setCommonTextValues(diagram, gaService, style);
            FontData font = getDefaultFont();
            style.setFont(gaService.manageFont(diagram, font.getName(), font.getHeight(), false, false));
            style.setForeground(gaService.manageColor(diagram, DOMAIN_OBJECT_TYPE_TEXT_COLOR));
        }
        return style;
    }

    public static Style getStyleForDomainObjectNumberText(Diagram diagram) {
        final String styleId = "DOMAIL-OBJECT-NUMBER-TEXT";
        IGaService gaService = Graphiti.getGaService();

        // this is a child style of the common-values-style
        Style parentStyle = getStyleForCommonValues(diagram);
        Style style = gaService.findStyle(parentStyle, styleId);

        if (style == null) { // style not found - create new style
            style = gaService.createPlainStyle(parentStyle, styleId);
            setCommonTextValues(diagram, gaService, style);
            FontData font = getDefaultFont();
            style.setFont(gaService.manageFont(diagram, font.getName(), font.getHeight() - 2, false, false));
        }
        return style;
    }

    public static Style getStyleForTextDecorator(Diagram diagram) {
        final String styleId = "TEXT-DECORATOR-TEXT";
        IGaService gaService = Graphiti.getGaService();

        // this is a child style of the common-values-style
        Style parentStyle = getStyleForCommonValues(diagram);
        Style style = gaService.findStyle(parentStyle, styleId);

        if (style == null) { // style not found - create new style
            style = gaService.createPlainStyle(parentStyle, styleId);
            setCommonTextValues(diagram, gaService, style);
            FontData font = getDefaultFont();
            style.setFont(gaService.manageFont(diagram, font.getName(), font.getHeight(), true, false));
        }
        return style;
    }

    private static void setCommonTextValues(Diagram diagram, IGaService gaService, Style style) {
        style.setFilled(false);
        style.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
        style.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
        style.setForeground(gaService.manageColor(diagram, DOMAIN_OBJECT_TEXT_FOREGROUND));
    }

    private static void setCommonValues(Style style) {
        // style.setLineStyle(LineStyle.SOLID);
        style.setLineVisible(true);
        // style.setLineWidth(2);
        style.setTransparency(0.0);
    }

    private static FontData getDefaultFont() {
        FontRegistry registry = Workbench.getInstance().getThemeManager().getCurrentTheme().getFontRegistry();
        return registry.getFontData("org.eclipse.jface.dialogfont")[0];
    }
}
