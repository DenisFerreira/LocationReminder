package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    //    TODO: add End to End testing to the app
    @Test
    fun launchReminderDescriptionWithData() {
        val reminder = ReminderDataItem("test title", "testDescription", "locationTest", 00.0, 00.0)
        val intent = ReminderDescriptionActivity.newIntent(getApplicationContext(), reminder)
        val activityScenario = ActivityScenario.launch<ReminderDescriptionActivity>(intent)

        onView(withId(R.id.titletxt)).check(
            matches(
                withText(
                    containsString(reminder.title)
                )
            )
        )

        onView(withId(R.id.descriptiontxt)).check(
            matches(
                withText(
                    containsString(reminder.description)
                )
            )
        )

        onView(withId(R.id.locationtxt)).check(
            matches(
                withText(
                    containsString(reminder.location)
                )
            )
        )
        activityScenario.close()
    }

    @Test
    fun createReminder() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        val viewModel: SaveReminderViewModel = get()
        //Load viewmodel with data to not require to test map
        viewModel.reminderSelectedLocationStr.postValue("Location Test")
        viewModel.latitude.postValue(0.0)
        viewModel.longitude.postValue(0.0)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(typeText("Title test"))
        onView(withId(R.id.reminderDescription)).perform( typeText("Description Test"), closeSoftKeyboard())


        onView(withId(R.id.saveReminder)).perform(click())

        onView(withText("Title test")).check(matches(isDisplayed()))
        onView(withText("Description Test")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun createReminderWithoutData() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        val viewModel: SaveReminderViewModel = get()
        //Load viewmodel with data to not require to test map
        viewModel.reminderSelectedLocationStr.postValue("Location Test")
        viewModel.latitude.postValue(0.0)
        viewModel.longitude.postValue(0.0)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Please enter title")))

        activityScenario.close()
    }

}
