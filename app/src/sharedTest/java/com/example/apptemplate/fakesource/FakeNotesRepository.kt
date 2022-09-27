package com.example.apptemplate.fakesource

import com.example.apptemplate.data.repository.NotesRepository
import com.example.apptemplate.data.source.local.room.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FakeNotesRepository @Inject constructor() : NotesRepository {

    private val _notesFlow = MutableStateFlow(LinkedHashMap<Int, NoteEntity>())
    override val notesFlow: Flow<List<NoteEntity>>
        get() = _notesFlow.map { notesMap ->
            notesMap.values.sortedByDescending { it.timeLastEditedMillis }
        }

    override suspend fun getNoteEntityById(id: Int): Result<NoteEntity> {
        return kotlin.runCatching { _notesFlow.value[id]!! }
    }

    override suspend fun insertNoteEntity(entity: NoteEntity) {
        _notesFlow.update {
            LinkedHashMap(it).apply {
                putIfAbsent(entity.id, entity)
            }
        }
    }

    override suspend fun deleteAllNoteEntities() {
        _notesFlow.update {
            LinkedHashMap()
        }
    }

    override suspend fun deleteNoteEntityById(id: Int) {
        _notesFlow.update {
            LinkedHashMap(it).apply {
                remove(id)
            }
        }
    }

    override suspend fun updateNoteEntity(entity: NoteEntity) {
        _notesFlow.update {
            LinkedHashMap(it).apply {
                put(entity.id, entity)
            }
        }
    }
}