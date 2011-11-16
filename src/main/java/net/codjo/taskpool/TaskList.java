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
 * Liste de t�che (Task) � effectuer (tri�s par ordre de priorites).
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
     * D�pile la t�che de plus grande priorit� de la liste.
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
     * Empile une t�che dans la liste
     *
     * @param task Une t�che.
     */
    public void push(Task task) {
        content.add(task);
        this.orderTaskListByPriority();
    }


    /**
     * tri de la liste par ordre de priorit� indice 0 => plus grande priorit�
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
