package com.cleanup.todoc;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cleanup.todoc.database.dao.TodocDatabase;
import com.cleanup.todoc.model.Project;

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

public class ProjectDaoTest {

    // FOR DATA

    private static TodocDatabase database;

    // DATA SET FOR TEST


    @Rule

    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before

    public void initDb() throws Exception {

        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),

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

    public void insertAndGetProject() throws InterruptedException {

        // TEST

        List<Project> value = database.projectDao().getProjects();

        assertEquals(3, value.size());
        Project project = value.get(0);
        assertEquals(1, project.getId());
        assertEquals("Projet Tartampion", project.getName());
        assertEquals(0xFFEADAD1, project.getColor());

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



}