package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        stopKoin()
        val remindersList = mutableListOf<ReminderDTO>()
        for(i in (1 ..10)){
            val reminderDTO = ReminderDTO(
                "Title$i",
                "Description$i",
                "Location$i",
                36.76,
                36.76
            )
            remindersList.add(reminderDTO)
        }

        fakeDataSource = FakeDataSource(remindersList)
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun loadReminder_checkNotNull() {
        // GIVEN
        remindersListViewModel.loadReminders()

        // WHEN
        val viewModelList = remindersListViewModel.remindersList.getOrAwaitValue()

        // THEN
        assertThat(viewModelList, not(nullValue()))
    }

    @Test
    fun loadReminder_compareViewmodelWithDatasourceReminders() = mainCoroutineRule.runBlockingTest {

        remindersListViewModel.loadReminders()
        val reminderFromViewModel = remindersListViewModel.remindersList.getOrAwaitValue()
        val reminderFromDataSource = (fakeDataSource.getReminders() as Result.Success).data

        assertThat(reminderFromViewModel[0].id, `is`(reminderFromDataSource[0].id))
        assertThat(reminderFromViewModel[0].title, `is`(reminderFromDataSource[0].title))
        assertThat(reminderFromViewModel[0].description, `is`(reminderFromDataSource[0].description))
        assertThat(reminderFromViewModel[0].location, `is`(reminderFromDataSource[0].location))
        assertThat(reminderFromViewModel[0].longitude, `is`(reminderFromDataSource[0].longitude))
        assertThat(reminderFromViewModel[0].latitude, `is`(reminderFromDataSource[0].latitude))

    }

    @Test
    fun loadReminder_loading() {
        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminder_whenUnavaibleOrEmpty() {
        fakeDataSource.shouldReturnError = true

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`("Reminders not found"))
    }
}