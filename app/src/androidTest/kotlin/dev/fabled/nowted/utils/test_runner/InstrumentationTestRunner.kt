package dev.fabled.nowted.utils.test_runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dev.fabled.nowted.TestApplication

@Suppress("unused")
class InstrumentationTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }

}