package dev.fabled.nowted.di

import androidx.room.Room
import dev.fabled.nowted.data.db.NotesDatabase
import dev.fabled.nowted.data.repository.FoldersRepositoryImpl
import dev.fabled.nowted.data.repository.NotesRepositoryImpl
import dev.fabled.nowted.domain.repository.FoldersRepository
import dev.fabled.nowted.domain.repository.NotesRepository
import dev.fabled.nowted.domain.use_cases.folders.CollectFolders
import dev.fabled.nowted.domain.use_cases.folders.CreateFolder
import dev.fabled.nowted.domain.use_cases.folders.FoldersCases
import dev.fabled.nowted.domain.use_cases.notes.ChangeNoteFavoriteState
import dev.fabled.nowted.domain.use_cases.notes.CollectNotes
import dev.fabled.nowted.domain.use_cases.notes.DeleteNote
import dev.fabled.nowted.domain.use_cases.notes.GetFavoriteNotes
import dev.fabled.nowted.domain.use_cases.notes.GetNote
import dev.fabled.nowted.domain.use_cases.notes.NotesCases
import dev.fabled.nowted.domain.use_cases.notes.RestoreNote
import dev.fabled.nowted.domain.use_cases.notes.SaveNote
import dev.fabled.nowted.domain.use_cases.recents.AddRecent
import dev.fabled.nowted.domain.use_cases.recents.CollectRecents
import dev.fabled.nowted.domain.use_cases.recents.RecentsCases
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

val testDatabaseModule = module {
    single {
        Room
            .inMemoryDatabaseBuilder(context = get(), klass = NotesDatabase::class.java)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        val database = get<NotesDatabase>()
        database.notesDao()
    }
}

val repositoryModule = module {
    singleOf(::NotesRepositoryImpl) { bind<NotesRepository>() }
    singleOf(::FoldersRepositoryImpl) { bind<FoldersRepository>() }
}

val useCasesModule = module {
    singleOf(::CollectRecents)
    singleOf(::AddRecent)

    singleOf(::CreateFolder)
    singleOf(::CollectFolders)

    singleOf(::CollectNotes)
    singleOf(::GetFavoriteNotes)
    singleOf(::DeleteNote)
    singleOf(::SaveNote)
    singleOf(::GetNote)
    singleOf(::RestoreNote)
    singleOf(::ChangeNoteFavoriteState)

    singleOf(::FoldersCases)
    singleOf(::NotesCases)
    singleOf(::RecentsCases)
}

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}

val testModules = module {
    includes(testDatabaseModule, repositoryModule, useCasesModule, viewModelModule)
}

val productionModules = module {
    includes(databaseModule, repositoryModule, useCasesModule, viewModelModule)
}