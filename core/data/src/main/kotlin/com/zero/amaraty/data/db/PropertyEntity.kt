package com.zero.amaraty.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val propertyType: String,
    val governorate: String?,
    val city: String?,
    val addressText: String?,
    val floorCount: Int?,
    val hasElevator: Boolean,
    val hasGarage: Boolean,
    val hasShops: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Entity(
    tableName = "units",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = androidx.room.ForeignKey.RESTRICT,
            onUpdate = androidx.room.ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["propertyId"]), Index(value = ["propertyId", "unitNumber"], unique = true)],
)
data class UnitEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val unitNumber: String,
    val floorNumber: Int,
    val unitType: String,
    /** Area in integer hundredths of a square metre. */
    val areaHundredths: Long?,
    val allocationWeight: Long,
    val occupancyStatus: String,
    val factualNotes: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
