/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.List;
/**
 * Liste de tâche (Task) à effectuer (triés par ordre de priorites).
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
class TaskList {
    private List<Task> content = new ArrayList<Task>();
    private List<Task> runningTask = new ArrayList<Task>();
    private Comparator<Task> taskComparator =
          new Comparator<Task>() {
              public int compare(Task taskA, Task taskB) {
                  int aVal = taskA.getPriority();
                  int bVal = taskB.getPriority();
                  return (aVal < bVal ? 1 : (aVal == bVal ? 0 : -1));
              }
          };


    /**
     * Vide la pile de toutes les taches.
     */
    public void clear() {
        content.clear();
    }


    /**
     * Teste si la liste est vide
     *
     * @return 'true' si vide.
     */
    public boolean isEmpty() {
        return content.isEmpty();
    }


    /**
     * Dépile la tâche de plus grande priorité de la liste.
     *
     * @return Une Task.
     *
     * @throws EmptyStackException la liste ne contient aucun element.
     */
    public Task pop() throws EmptyStackException {
        if (content.isEmpty()) {
            throw new EmptyStackException();
        }
        Task task = content.remove(0);
        runningTask.add(task);
        return task;
    }


    /**
     * Empile une tâche dans la liste
     *
     * @param task Une tâche.
     */
    public void push(Task task) {
        content.add(task);
        this.orderTaskListByPriority();
    }


    /**
     * tri de la liste par ordre de priorité indice 0 => plus grande priorité
     */
    private void orderTaskListByPriority() {
        Collections.sort(content, taskComparator);
    }


    public boolean hasRunningTask() {
        return !runningTask.isEmpty();
    }


    public void closeTask(Task task) {
        runningTask.remove(task);
    }
}
