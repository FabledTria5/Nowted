package dev.fabled.nowted.di

import androidx.room.Room
import dev.fabled.nowted.data.db.NotesDatabase
import dev.fabled.nowted.data.repository.NotesRepositoryImpl
import dev.fabled.nowted.domain.repository.NotesRepository
import dev.fabled.nowted.domain.use_cases.folders.CollectFolders
import dev.fabled.nowted.domain.use_cases.folders.CreateFolder
import dev.fabled.nowted.domain.use_cases.notes.CollectNotes
import dev.fabled.nowted.domain.use_cases.notes.DeleteNote
import dev.fabled.nowted.domain.use_cases.notes.GetNote
import dev.fabled.nowted.domain.use_cases.notes.RestoreNote
import dev.fabled.nowted.domain.use_cases.notes.SaveNote
import dev.fabled.nowted.presentation.ui.navigation.manager.NavigationManager
import dev.fabled.nowted.presentation.ui.navigation.manager.NavigationManagerImpl
import dev.fabled.nowted.presentation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room
            .databaseBuilder(
                context = get(),
                klass = NotesDatabase::class.java,
                name = "notes_database"
            )
            .createFromAsset(databaseFilePath = "database/notes_database.db")
            .build()
    }

    single {
        val database = get<NotesDatabase>()
        database.notesDao()
    }
}

val repositoryModule = module {
    singleOf(::NotesRepositoryImpl) { bind<NotesRepository>() }
}

val useCasesModule = module {
    singleOf(::CreateFolder)
    singleOf(::CollectFolders)
    singleOf(::CollectNotes)
    singleOf(::DeleteNote)
    singleOf(::SaveNote)
    singleOf(::GetNote)
    singleOf(::RestoreNote)
}

val navigationModule = module {
    singleOf(::NavigationManagerImpl) { bind<NavigationManager>() }
}

val appModule = module {
    viewModelOf(::MainViewModel)
}