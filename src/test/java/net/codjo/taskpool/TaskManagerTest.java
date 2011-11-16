/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import junit.framework.TestCase;
/**
 * Test <code>TaskManager</code>.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.3 $
 */
public class TaskManagerTest extends TestCase {
    private TaskManager taskManager = new TaskManager(1);


    public void testTaskLifeCycle() throws InterruptedException {
        FakeTask task = new FakeTask(0);
        assertEquals(task.taskDone, false);

        taskManager.pushToDoTask(task);
        taskManager.start();
        taskManager.waitCurrentFinished();

        assertEquals(task.taskDone, true);
        assertEquals(taskManager.hasToDoTask(), false);
    }


    public void test_manager() throws InterruptedException {
        List<FakeTask> tasks = new ArrayList<FakeTask>();

        for (int i = 0; i < 200; i++) {
            FakeTask task = new FakeTask(i);
            taskManager.pushToDoTask(task);
            tasks.add(i, task);
        }
        taskManager.start();

        taskManager.waitCurrentFinished();

        for (FakeTask fakeTask : tasks) {
            assertEquals(fakeTask.toString(), fakeTask.taskDone, true);
        }
    }


    public void test_pushToDoTask() throws Exception {
        taskManager = new TaskManager(0);
        Task task = new FakeTask(0);
        taskManager.pushToDoTask(task);
        taskManager.start();
        assertEquals(taskManager.popToDoTask(), task);
    }


    public void test_pushToDoTask_sorted() throws Exception {
        taskManager = new TaskManager(0);

        taskManager.pushToDoTask(new FakeTask(1));
        taskManager.pushToDoTask(new FakeTask(0));
        taskManager.pushToDoTask(new FakeTask(5));

        taskManager.start();
        assertEquals(taskManager.popToDoTask().getPriority(), 5);
        assertEquals(taskManager.popToDoTask().getPriority(), 1);

        taskManager.pushToDoTask(new FakeTask(2));

        assertEquals(taskManager.popToDoTask().getPriority(), 2);
        assertEquals(taskManager.popToDoTask().getPriority(), 0);
    }


    public void test_shutdown() throws Exception {
        FakeTask task = new FakeTask(0);

        taskManager.start();
        taskManager.pushToDoTask(task);
        taskManager.waitCurrentFinished();
        taskManager.shutdown();

        assertEquals(task.taskDone, true);
        assertEquals(taskManager.hasToDoTask(), false);

        for (Iterator<? extends Thread> iter = taskManager.threads(); iter.hasNext();) {
            Thread thread = iter.next();
            assertEquals("Thread inactif", thread.isAlive(), false);
        }
    }


    public void test_waitCurrentFinished() throws Exception {
        final Semaphore semaphore = new Semaphore(0);
        FakeTask taskA = new FakeTask(5) {

            @Override
            public synchronized void run() {
                super.run();
                taskManager.stop();
                semaphore.release();
            }
        };
        FakeTask taskB = new FakeTask(0);

        taskManager.pushToDoTask(taskA);
        taskManager.pushToDoTask(taskB);

        taskManager.start();

        semaphore.acquire();

        assertTrue("Task A est executée", taskA.taskDone);
        assertFalse("Task B est bloquée(car pool est stop)", taskB.taskDone);
        assertEquals(taskManager.hasToDoTask(), true);
    }


    public void test_waitCurrentFinished_Simple() throws Exception {
        final FakeTask taskA = new FakeTask(5);
        final FakeTask taskB = new FakeTask(0);

        taskManager.pushToDoTask(taskA);
        taskManager.pushToDoTask(taskB);

        taskManager.start();
        taskManager.waitCurrentFinished();
        taskManager.stop();

        assertTrue("Task A est executée", taskA.taskDone);
        assertTrue("Task B est executée", taskB.taskDone);
        assertFalse(taskManager.hasToDoTask());
    }


    public void test_restart() throws Exception {
        final FakeTask taskA = new FakeTask(0);
        final FakeTask taskB = new FakeTask(0);

        taskManager.pushToDoTask(taskA);
        taskManager.start();
        taskManager.waitCurrentFinished();
        taskManager.stop();

        taskManager.pushToDoTask(taskB);

        taskManager.start();
        taskManager.waitCurrentFinished();
        taskManager.stop();

        assertSame(taskB.executor, taskA.executor);
    }


    @Override
    protected void tearDown() throws java.lang.Exception {
        taskManager.shutdown();
    }


    static class FakeTask implements Task {
        private int priority;
        private boolean taskDone = false;
        private Thread executor;


        FakeTask(int priority) {
            this.priority = priority;
        }


        public int getPriority() {
            return priority;
        }


        public synchronized void run() {
            executor = Thread.currentThread();
            taskDone = true;
            //noinspection SynchronizeOnThis
            notifyAll();
        }


        @Override
        public String toString() {
            return "Task(p=" + getPriority() + ")";
        }
    }
}
