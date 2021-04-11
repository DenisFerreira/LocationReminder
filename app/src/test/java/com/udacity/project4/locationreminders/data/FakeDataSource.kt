package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(val dataSource: MutableList<ReminderDTO>) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
var shouldReturnError: Boolean = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError)
            return Result.Error("Reminders not found")
        else
            return Result.Success(dataSource)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        dataSource.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        for(reminder in dataSource)
            if(reminder.id.equals(id))
                return Result.Success(reminder)
        return Result.Error("reminder not found")
    }

    override suspend fun deleteAllReminders() {
        dataSource.clear()
    }

}