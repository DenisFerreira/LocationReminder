package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    //    TODO: Add testing implementation to the RemindersDao.kt
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertReminder() = runBlockingTest {
        val reminderDao = database.reminderDao()
        val reminder = ReminderDTO(
            "Title",
            "Description",
            "Location",
            36.79,
            37.45
        )
        reminderDao.saveReminder(reminder)
        val loaded = reminderDao.getReminderById(reminder.id)
        if (loaded != null) {
            assertThat(reminder.id, `is`(loaded.id) )
            assertThat(reminder.title, `is`(loaded.title) )
            assertThat(reminder.description, `is`(loaded.description) )
            assertThat(reminder.location, `is`(loaded.location) )
            assertThat(reminder.latitude, `is`(loaded.latitude) )
            assertThat(reminder.longitude, `is`(loaded.longitude) )
        }else fail()
    }
    @After
    fun closeDB() {
        database.close()
    }
}