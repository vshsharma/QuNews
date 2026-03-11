package com.miva.qunews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.miva.qunews.data.local.entity.NewsEntity

@Database(
    entities = [NewsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase: RoomDatabase() {
    abstract val newsDao: NewsDao
}