/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import junit.framework.TestCase;
/**
 * Classe abstraite servant a faciliter l'ecriture des tests de dépendance entre package. La classe est basée
 * sur <code>JDepend</code> (dont l'url est <code>http://www.clarkware.com/software/jdepend2.2.zip</code>).
 *
 * <p> Exemple de méthode de test:
 * <pre>
 *  public void test_Dependency() {
 *  String[] dependsUpon = {
 *      "net.codjo.orbis.controls.shipment"
 *      , "net.codjo.utils"
 *      , "net.codjo.orbis.utils"
 *      };
 *  assertDependency("net.codjo.orbis.controls", dependsUpon);
 *  assertNoCycle("net.codjo.orbis.controls");
 *  }
 *  </pre>
 * </p>
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.3 $
 */
abstract class AbstractDependencyTestCase extends TestCase {
    protected JDepend jdepend;


    /**
     * Verifie que le package <code>currentPackage</code> n'a comme dépendance directe seulement les package
     * se trouvant dans <code>dependsUpon</code>.
     *
     * @param currentPackage Le package a verifier (ex : "net.codjo.orbis")
     * @param dependsUpon    Tableau de package.
     *
     * @throws IllegalArgumentException TODO
     */
    protected void assertDependency(String currentPackage, String[] dependsUpon) {
        jdepend.analyze();

        JavaPackage testedPack = jdepend.getPackage(currentPackage);
        if (testedPack == null) {
            throw new IllegalArgumentException("Package " + currentPackage
                                               + " est inconnu");
        }
        Set<String> trueDependency = new TreeSet<String>();
        for (Object javaPackage : testedPack.getEfferents()) {
            trueDependency.add(((JavaPackage)javaPackage).getName());
        }

        List wantedDepency = Arrays.asList(dependsUpon);
        if (!trueDependency.containsAll(wantedDepency)
            || !wantedDepency.containsAll(trueDependency)) {
            StringWriter strWriter = new StringWriter();
            doTrace(currentPackage, dependsUpon, new PrintWriter(strWriter));
            fail("Contraintes de Dependance non respectée : \n" + strWriter.toString());
        }
    }


    /**
     * Verfie que le package <code>packageName</code> n'a pas de dependance circulaire.
     *
     * @param packageName Nom du package
     */
    protected void assertNoCycle(String packageName) {
        assertEquals("Cycle de dépendance pour " + packageName, false,
                     jdepend.getPackage(packageName).containsCycle());
    }


    @Override
    protected void setUp() throws Exception {
        jdepend = new JDepend();
        jdepend.addDirectory(System.getProperty("ROOT_PATH", "./") + "/target/classes");
    }


    /**
     * Trace en cas d'erreur.
     *
     * @param packName    Nom du package
     * @param dependsUpon Tableau de package
     * @param os          flux d'écriture
     */
    private void doTrace(String packName, String[] dependsUpon, PrintWriter os) {
        JavaPackage pack = jdepend.getPackage(packName);
        os.println("********* " + pack.getName());

        os.println("*** Différence "
                   + "(++ nouvelle dépendance / -- dépendance en moins):");

        List<String> oldDependence = new ArrayList<String>(Arrays.asList(dependsUpon));

        printNewDependency(os, pack, oldDependence);

        printOldDependency(os, oldDependence);
    }


    private void printNewDependency(PrintWriter os, JavaPackage pack, List<String> oldDependence) {
        for (Object javaPackage : pack.getEfferents()) {
            JavaPackage obj = (JavaPackage)javaPackage;
            if (!oldDependence.remove(obj.getName())) {
                os.println("  <++> " + obj.getName());
            }
        }
    }


    private void printOldDependency(PrintWriter os, List<String> oldDependence) {
        for (String anOldDependence : oldDependence) {
            os.println("  <--> " + anOldDependence);
        }
    }
}
