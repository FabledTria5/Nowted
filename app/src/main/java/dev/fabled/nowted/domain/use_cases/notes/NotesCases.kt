package dev.fabled.nowted.domain.use_cases.notes

data class NotesCases(
    val collectNotes: CollectNotes,
    val saveNote: SaveNote,
    val deleteNote: DeleteNote,
    val restoreNote: RestoreNote,
    val getNote: GetNote,
    val toggleNoteFavoriteState: ChangeNoteFavoriteState
)
