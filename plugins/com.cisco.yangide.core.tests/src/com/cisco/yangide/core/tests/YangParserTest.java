package com.cisco.yangide.core.tests;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.internal.YangASTParser;

public class YangParserTest extends TestCase {
    public void testSimpleParser() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import.yang"), false)) {

            Module module = new YangASTParser().parseYangFile(in);
            assertEquals("my-crypto", module.getName());
            assertEquals(7, module.getNameStartPosition());
            assertEquals(0, module.getStartPosition());
            assertEquals(328, module.getLength());
        }
    }

    public void testNodeAtPostion() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import.yang"), false)) {

            Module module = new YangASTParser().parseYangFile(in);
            assertEquals(module, module.getNodeAtPosition(1));
            assertEquals(module.getImports().get(0), module.getNodeAtPosition(100));
        }
    }

    public void testIncompleteParse() throws Exception {
        try (InputStream in = FileLocator.openStream(Platform.getBundle("com.cisco.yangide.core.tests"), new Path(
                "yang/simple_import_incomplete.yang"), false)) {

            Module module = new YangASTParser().parseYangFile(in);
            assertNotNull(module);
            assertEquals(1, module.getImports().size());
            assertNotNull(module.getImports().get(0));
        }
    }
}
