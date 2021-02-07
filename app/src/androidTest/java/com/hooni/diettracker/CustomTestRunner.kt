package com.hooni.diettracker

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.hooni.diettracker.core.TestBaseApplicationAndroidTest

class CustomTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestBaseApplicationAndroidTest::class.java.name, context)
    }


}