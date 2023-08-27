package com.pose.flashcat.di

import android.content.Context
import androidx.room.Room
import com.pose.flashcat.db.FlashcardsDatabase
import com.pose.flashcat.db.dao.DecksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun injectDatabase(@ApplicationContext context: Context): FlashcardsDatabase {
        return Room
            .databaseBuilder(context, FlashcardsDatabase::class.java, "flashcat_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun injectFlashcardsDao(database: FlashcardsDatabase): DecksDao {
        return database.decks()
    }
}
