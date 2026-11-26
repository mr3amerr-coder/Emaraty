package com.zero.amaraty.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PropertyEntity::class, UnitEntity::class, AuditLogEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AmaratyDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun unitDao(): UnitDao
    abstract fun auditLogDao(): AuditLogDao
}
