package com.amirmuhsin.listinghelper.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amirmuhsin.listinghelper.data.db.dao.PhotoPairDao
import com.amirmuhsin.listinghelper.data.db.dao.ProductDao
import com.amirmuhsin.listinghelper.data.db.model.PhotoPairEntity
import com.amirmuhsin.listinghelper.data.db.model.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        PhotoPairEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun photoPairDao(): PhotoPairDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "listing_helper.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // For development; remove or replace with real Migrations in prod
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
