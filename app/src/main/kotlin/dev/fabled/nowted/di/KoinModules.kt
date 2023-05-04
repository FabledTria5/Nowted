package dev.fabled.nowted.di

import androidx.room.Room
import dev.fabled.nowted.data.db.NotesDatabase
import dev.fabled.nowted.data.db.dao.FoldersDao
import dev.fabled.nowted.data.db.dao.NotesDao
import dev.fabled.nowted.data.dispatchers.AndroidDispatchers
import dev.fabled.nowted.data.repository.FoldersRepositoryImpl
import dev.fabled.nowted.data.repository.NotesRepositoryImpl
import dev.fabled.nowted.domain.dispatchers.AppDispatchers
import dev.fabled.nowted.domain.repository.FoldersRepository
import dev.fabled.nowted.domain.repository.NotesRepository
import dev.fabled.nowted.domain.use_cases.common.GetCurrentFolder
import dev.fabled.nowted.domain.use_cases.common.GetCurrentNoteName
import dev.fabled.nowted.domain.use_cases.common.OpenNote
import dev.fabled.nowted.domain.use_cases.home.AddRecent
import dev.fabled.nowted.domain.use_cases.home.CollectFolders
import dev.fabled.nowted.domain.use_cases.home.CollectRecents
import dev.fabled.nowted.domain.use_cases.home.CreateFolder
import dev.fabled.nowted.domain.use_cases.home.OpenFolder
import dev.fabled.nowted.domain.use_cases.note.ArchiveNote
import dev.fabled.nowted.domain.use_cases.note.ChangeNoteFavoriteState
import dev.fabled.nowted.domain.use_cases.note.GetCurrentNote
import dev.fabled.nowted.domain.use_cases.note.RemoveNote
import dev.fabled.nowted.domain.use_cases.note.RestoreNote
import dev.fabled.nowted.domain.use_cases.note.UpdateOrCreateNote
import dev.fabled.nowted.domain.use_cases.notes_list.GetFavoriteNotes
import dev.fabled.nowted.domain.use_cases.notes_list.GetNotesFromCurrentFolder
import dev.fabled.nowted.presentation.ui.screens.home.HomeViewModel
import dev.fabled.nowted.presentation.ui.screens.note.NoteViewModel
import dev.fabled.nowted.presentation.ui.screens.notes_list.NotesListViewModel
import dev.fabled.nowted.presentation.ui.screens.restore.RestoreViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Module for database dependencies. Creates [NotesDatabase], provides [NotesDao] and [FoldersDao]
 */
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
        val database: NotesDatabase = get()
        database.notesDao()
    }

    single {
        val database: NotesDatabase = get()
        database.foldersDao()
    }
}

/**
 * Provides repositories
 */
val repositoryModule = module {
    singleOf(::NotesRepositoryImpl) { bind<NotesRepository>() }
    singleOf(::FoldersRepositoryImpl) { bind<FoldersRepository>() }
}

/**
 * Provides use cases
 */
val useCasesModule = module {
    single { GetCurrentFolder(foldersRepository = get()) }
    single { GetCurrentNoteName(notesRepository = get()) }
    single { OpenNote(notesRepository = get()) }

    single { AddRecent(notesRepository = get()) }
    single { CollectFolders(foldersRepository = get()) }
    single { CollectRecents(notesRepository = get()) }
    single { CreateFolder(foldersRepository = get()) }
    single { OpenFolder(foldersRepository = get()) }

    single { GetCurrentNoteName(notesRepository = get()) }
    single { GetCurrentNote(notesRepository = get()) }
    single { ChangeNoteFavoriteState(notesRepository = get()) }
    single { RemoveNote(notesRepository = get(), foldersRepository = get()) }
    single { RestoreNote(foldersRepository = get()) }
    single { UpdateOrCreateNote(notesRepository = get()) }
    single { ArchiveNote(foldersRepository = get()) }

    single { GetNotesFromCurrentFolder(notesRepository = get()) }
    single { GetFavoriteNotes(notesRepository = get()) }
}

/**
 * Provides utilities, as [AppDispatchers]
 */
val utilsModule = module {
    singleOf(::AndroidDispatchers) { bind<AppDispatchers>() }
}

/**
 * Provides ViewModels
 */
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::NotesListViewModel)
    viewModelOf(::NoteViewModel)
    viewModelOf(::RestoreViewModel)
}

/**
 * Provides all other modules
 */
val productionModules = module {
    includes(databaseModule, repositoryModule, useCasesModule, viewModelModule, utilsModule)
}