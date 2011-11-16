/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.taskpool;
import junit.framework.TestCase;
/**
 * Classe de test de TaskList
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.3 $
 */
public class TaskListTest extends TestCase {
    private TaskList list = new TaskList();


    public void test_pop_empty() {
        try {
            list.pop();
            fail("La pile est vide");
        }
        catch (java.util.EmptyStackException ex) {
        }
    }


    public void test_push() {
        Task task = new TaskManagerTest.FakeTask(0);
        list.push(task);

        assertEquals(list.pop(), task);
    }


    public void test_push_order() {
        Task task = new TaskManagerTest.FakeTask(3);
        list.push(task);
        Task task1 = new TaskManagerTest.FakeTask(0);
        list.push(task1);
        Task task2 = new TaskManagerTest.FakeTask(5);
        list.push(task2);

        assertEquals(list.pop(), task2);
        assertEquals(list.pop(), task);
        assertEquals(list.pop(), task1);
    }


    public void test_closeTask() {
        Task task = new TaskManagerTest.FakeTask(3);
        list.push(task);
        assertFalse(list.hasRunningTask());

        assertEquals(list.pop(), task);

        assertTrue(list.hasRunningTask());

        list.closeTask(task);

        assertFalse(list.hasRunningTask());
    }


    @Override
    protected void setUp() {
    }
}
