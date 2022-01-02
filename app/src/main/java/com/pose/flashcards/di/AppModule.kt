package com.pose.flashcards.di

import android.content.Context
import androidx.room.Room
import com.pose.flashcards.db.FlashcardsDao
import com.pose.flashcards.db.FlashcardsDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): FlashcardsDatabase {
        return Room.databaseBuilder(
            context,
            FlashcardsDatabase::class.java,
            "comp_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideDao(database: FlashcardsDatabase): FlashcardsDao {
        return database.dao()
    }
}
