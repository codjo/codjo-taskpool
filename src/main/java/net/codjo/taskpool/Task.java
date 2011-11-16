/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
/**
 * Interface représentant une tâche à exécuter. C'est un thread qui fera appel à sa méthode run pour exécuter
 * le traitement à faire.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public interface Task extends Runnable {
    /**
     * Retourne la priorité de la tâche (ordre de priorité ascendant).
     *
     * @return The Priority value
     */
    public int getPriority();
}
