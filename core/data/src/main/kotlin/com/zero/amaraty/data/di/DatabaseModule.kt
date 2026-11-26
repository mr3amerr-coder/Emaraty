package com.zero.amaraty.data.di

import android.content.Context
import androidx.room.Room
import com.zero.amaraty.data.db.AmaratyDatabase
import com.zero.amaraty.data.db.AuditLogDao
import com.zero.amaraty.data.db.PropertyDao
import com.zero.amaraty.data.db.UnitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AmaratyDatabase =
        Room.databaseBuilder(context, AmaratyDatabase::class.java, "amaraty.db")
            .build()

    @Provides
    fun providePropertyDao(database: AmaratyDatabase): PropertyDao = database.propertyDao()

    @Provides
    fun provideUnitDao(database: AmaratyDatabase): UnitDao = database.unitDao()

    @Provides
    fun provideAuditLogDao(database: AmaratyDatabase): AuditLogDao = database.auditLogDao()
}
