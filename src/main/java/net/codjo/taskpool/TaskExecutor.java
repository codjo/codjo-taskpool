/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
/**
 * Cette classe représente un thread qui va exécuter une tâche (classe Task) de la pile de tâche à effectuer
 * (TaskList).
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
class TaskExecutor extends Thread {
    private TaskManager taskManager;


    TaskExecutor(TaskManager taskManager) {
        if (taskManager == null) {
            throw new IllegalArgumentException();
        }
        this.taskManager = taskManager;
    }


    @Override
    public void run() {
        try {
            Task task = taskManager.popToDoTask();

            while (task != null) {

                task.run();
                taskManager.closeTask(task);

                task = taskManager.popToDoTask();
            }
        }
        catch (InterruptedException e) {
            ;
        }
        taskManager.closeExecutor(this);
    }
}
