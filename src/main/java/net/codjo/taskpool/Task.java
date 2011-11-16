/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
/**
 * Interface repr�sentant une t�che � ex�cuter. C'est un thread qui fera appel � sa m�thode run pour ex�cuter
 * le traitement � faire.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public interface Task extends Runnable {
    /**
     * Retourne la priorit� de la t�che (ordre de priorit� ascendant).
     *
     * @return The Priority value
     */
    public int getPriority();
}
