/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
/**
 * Test les contraintes de dependances de <code>net.codjo.taskpool</code>.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.3 $
 */
public class DependencyConstraintTest extends AbstractDependencyTestCase {

    public void test_dependency() throws Exception {

        assertDependency("net.codjo.taskpool", new String[]{"java.lang", "java.util"});

        assertNoCycle("net.codjo.taskpool");
    }
}
