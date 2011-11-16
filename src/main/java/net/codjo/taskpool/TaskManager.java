/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Cette classe gère le pool de threads (ensemble de TaskExecutor) et les tâches à effectuer.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public class TaskManager {
    private final Object lock = new Object();
    private boolean paused = true;
    private boolean shutdown = false;
    private int threadAllowedNumber;
    private List<TaskExecutor> threadPool;
    private TaskList toDoTasks = new TaskList();
    private int waitingThreads = 0;


    /**
     * @param numberOfThread Nombre de threads.
     */
    public TaskManager(int numberOfThread) {
        this.setThreadAllowedNumber(numberOfThread);
    }


    /**
     * Efface toutes les TodoTask non executées.
     */
    public void clear() {
        synchronized (lock) {
            toDoTasks.clear();
        }
    }


    /**
     * Retourne le nombre de threads du pool.
     *
     * @return La valeur de threadAllowedNumber
     */
    public int getThreadAllowedNumber() {
        synchronized (lock) {
            return threadAllowedNumber;
        }
    }


    /**
     * Teste s'il reste des tâches à effectuer.
     *
     * @return <code>true</code> si il reste des taches.
     */
    public boolean hasToDoTask() {
        synchronized (lock) {
            return !toDoTasks.isEmpty();
        }
    }


    /**
     * Teste s'il existe des tâches en cours d'execution.
     *
     * @return <code>true</code> si il existe des taches en execution.
     */
    public boolean hasRunningTask() {
        synchronized (lock) {
            return toDoTasks.hasRunningTask();
        }
    }


    /**
     * Ajoute une tâche.
     *
     * @param task
     */
    public void pushToDoTask(Task task) {
        synchronized (lock) {
            toDoTasks.push(task);
            lock.notifyAll();
        }
    }


    /**
     * Permet d'arrêter définitivement les threads d'exécution des tâches (TaskExecutor).
     */
    public void shutdown() {
        synchronized (lock) {
            shutdown = true;
            lock.notifyAll();
            waitExecutorsAreClosed();
        }
    }


    /**
     * Lancement du pool de thread (TaskExecutor).
     */
    public void start() {
        synchronized (lock) {
            initThreadPool();
            paused = false;
            lock.notifyAll();
        }
    }


    /**
     * Arrêt de l'ensemble des tâches.
     */
    public void stop() {
        synchronized (lock) {
            paused = true;
        }
    }


    /**
     * Attente que les threads finissent d'exécuter leurs tâches courantes.
     *
     * @throws InterruptedException
     */
    public void waitCurrentFinished() throws InterruptedException {
        synchronized (lock) {
            waitExecutorsAreInWaitState();
            while (hasRunningTask() || hasToDoTask()) {
                lock.wait();
            }
        }
    }


    /**
     * Retourne la tâche la plus prioritaire.
     *
     * @return Une tâche.
     *
     * @throws InterruptedException
     */
    Task popToDoTask() throws InterruptedException {
        synchronized (lock) {
            waitingThreads++;
            lock.notifyAll();

            try {
                waitNewTask();

                if (shutdown) {
                    return null;
                }

                return toDoTasks.pop();
            }
            finally {
                waitingThreads--;
                lock.notifyAll();
            }
        }
    }


    void closeTask(Task task) {
        synchronized (lock) {
            toDoTasks.closeTask(task);
        }
    }


    void closeExecutor(TaskExecutor taskExecutor) {
        synchronized (lock) {
            threadPool.remove(taskExecutor);
            lock.notifyAll();
        }
    }


    Iterator<? extends Thread> threads() {
        synchronized (lock) {
            return threadPool.iterator();
        }
    }


    private void initThreadPool() {
        synchronized (lock) {
            if (threadPool != null) {
                return;
            }
            threadPool = new ArrayList<TaskExecutor>(getThreadAllowedNumber());
            for (int i = 0; i < getThreadAllowedNumber(); i++) {
                threadPool.add(new TaskExecutor(this));
                threadPool.get(i).start();
            }

            waitExecutorsAreInWaitState();
        }
    }


    private void waitExecutorsAreInWaitState() {
        while (waitingThreads != threadPool.size()) {
            try {
                lock.wait();
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }


    private void waitExecutorsAreClosed() {
        while (threadPool.size() != 0) {
            try {
                lock.wait();
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }


    private void setThreadAllowedNumber(int newThreadAllowedNumber) {
        synchronized (lock) {
            threadAllowedNumber = newThreadAllowedNumber;
        }
    }


    private void waitNewTask() throws InterruptedException {
        while ((toDoTasks.isEmpty() || paused) && !shutdown) {
            lock.wait();
        }
    }
}
