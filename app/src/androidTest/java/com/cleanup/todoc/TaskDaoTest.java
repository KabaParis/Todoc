package com.cleanup.todoc;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.database.dao.TodocDatabase;
import com.cleanup.todoc.database.dao.TodocDatabase_Impl;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)

public class TaskDaoTest {

    // FOR DATA

    private static TodocDatabase database;

    // DATA SET FOR TEST

    private static long TASK_ID = 1;

    private static Task TASK_DEMO = new Task(TASK_ID, 1, "Repassage",
            1657991966);

    @Rule

    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before

    public void initDb() throws Exception {

        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),

                TodocDatabase.class)

                .allowMainThreadQueries()

                .addCallback(prepopulateDatabase())

                .build();

    }

    @After

    public void closeDb() throws Exception {

        database.close();

    }


    @Test

    public void insertAndGetTask() throws InterruptedException {


        // BEFORE : Adding a new task

        this.database.taskDao().createTask(TASK_DEMO);

        // TEST

        List<Task> value = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());

        assertEquals(1, value.size());
        Task task = value.get(0);
        assertEquals(TASK_ID, task.getId());
        assertEquals(1L, task.getProjectId());
        assertEquals("Repassage", task.getName());
        assertEquals(1657991966, task.getCreationTimestamp());
    }

    private static RoomDatabase.Callback prepopulateDatabase() {

        return new RoomDatabase.Callback() {


            @Override

            public void onCreate(@NonNull SupportSQLiteDatabase db) {

                super.onCreate(db);

                Executors.newSingleThreadExecutor().execute(() -> {
                    db.execSQL("insert into Project(id, name, color) values (1, 'Projet Tartampion', '0xFFEADAD1')");
                    db.execSQL("insert into Project(id, name, color) values (2, 'Projet Lucidia', '0xFFB4CDBA')");
                    db.execSQL("insert into Project(id, name, color) values (3, 'Projet Circus', '0xFFA3CED2')");
                });
            }
        };

    }
    private static Task IRONING = new Task(1L, 1L, "Repassage", TASK_ID

    );

    private static Task WINDOWS = new Task(2L, 2L, "Fenêtres", TASK_ID
    );


    private static Task FLOOR = new Task(3L, 3L, "sol", TASK_ID
    );

    @Test

    public void getTasksWhenNoTaskInserted() throws InterruptedException {

        List<Task> tasks = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());

        assertTrue(tasks.isEmpty());

    }

    @Test

    public void insertAndDeleteTask() throws InterruptedException {

     //    BEFORE : Adding demo user & demo item. Next, get the item added & delete it.

     //   this.database.taskDao().createTask(TASK_DEMO);


        this.database.taskDao().createTask(WINDOWS);

        List<Task> taskAdded = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());

        assertEquals(taskAdded.size(), 1 );

        this.database.taskDao().deleteTask(WINDOWS);

        //TEST

        List<Task> tasks = LiveDataTestUtils.getValue(this.database.taskDao().getTasks());


        assertTrue(tasks.isEmpty());

    }
}
