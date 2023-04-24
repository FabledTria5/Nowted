package dev.fabled.nowted

import android.app.Application
import dev.fabled.nowted.di.appModule
import dev.fabled.nowted.di.databaseModule
import dev.fabled.nowted.di.repositoryModule
import dev.fabled.nowted.di.useCasesModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@MainApplication)
            modules(
                appModule,
                databaseModule,
                repositoryModule,
                useCasesModule,
            )
        }
    }

}