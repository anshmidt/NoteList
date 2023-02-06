package com.anshmidt.notelist.repository

import com.anshmidt.notelist.database.ListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ListRepository {

    fun getAllLists(): Flow<List<ListEntity>> {
        return emptyFlow()
    }
}