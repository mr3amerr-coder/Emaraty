package com.zero.amaraty.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(property: PropertyEntity)

    @Query("SELECT * FROM properties ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :propertyId LIMIT 1")
    suspend fun getById(propertyId: String): PropertyEntity?
}

@Dao
interface UnitDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(unit: UnitEntity)

    @Query("SELECT * FROM units WHERE propertyId = :propertyId ORDER BY floorNumber, unitNumber")
    fun observeForProperty(propertyId: String): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units WHERE id = :unitId LIMIT 1")
    suspend fun getById(unitId: String): UnitEntity?
}

@Dao
interface AuditLogDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun append(entry: AuditLogEntity)

    @Query("SELECT * FROM audit_log ORDER BY occurredAtEpochMillis DESC")
    fun observeRecent(): Flow<List<AuditLogEntity>>
}
