package com.cisco.yangide.ext.model.editor.util;

import java.util.Arrays;
import java.util.List;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public enum YangTag {
    DESCRIPTION, YANG_VERSION("yang-version"), NAMESPACE, PREFIX, ORGANIZATION(true), CONTACT, REFERENCE, CONFIG(Arrays.asList(Strings.EMPTY_STRING, Boolean.TRUE.toString(), Boolean.FALSE.toString())), 
    MANDATORY(Arrays.asList(Strings.EMPTY_STRING, Boolean.TRUE.toString(), Boolean.FALSE.toString())), STATUS(Arrays.asList(Strings.EMPTY_STRING, "current", "deprecated", "obsolete")), 
    PRESENCE(Arrays.asList(Strings.EMPTY_STRING, Boolean.TRUE.toString(), Boolean.FALSE.toString())), 
    ORDERED_BY("ordered-by", Arrays.asList(Strings.EMPTY_STRING, "user", "system", "obsolete")), DEFAULT, UNITS;
    private String name;
    private boolean required;
    private List<String> possibleValues;
    private IPropertyDescriptor pd;
    private YangTag() {
        required = false;
    }
    private YangTag(boolean required) {
        this();
        this.required = required;
    }
    private YangTag(String name) {
        this();
        this.name = name;
    }
    private YangTag(List<String> possibleValues) {
        this();
        this.possibleValues = possibleValues;
    }
    private YangTag(String name, List<String> possibleValues) {
        this();
        this.name = name;
        this.possibleValues = possibleValues;
    }
    public String getDescriptor() {
        return toString();
    }
    public String getName() {
        if (null == name) {
            return toString().toLowerCase();
        }
        return name;
    }
    public boolean isRequired() {
        return required;
    }
    public List<String> getPossibleValues() {
        return possibleValues;
    }
    public IPropertyDescriptor getPropertyDescriptor() {
        if (null == pd) {
            if (null != possibleValues && !possibleValues.isEmpty()) {
                pd = new ComboBoxPropertyDescriptor(this, getName(), possibleValues.toArray(new String[0]));
            } else {
                pd = new TextPropertyDescriptor(this, getName());
            }
        }
        return pd;
    }
}
