package com.zero.amaraty.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_log",
    indices = [Index(value = ["entityType", "entityId"]), Index(value = ["occurredAtEpochMillis"])],
)
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val entityType: String,
    val entityId: String,
    val action: String,
    val occurredAtEpochMillis: Long,
    /** Stores non-sensitive change metadata only; never store credentials or identity numbers. */
    val safeSummary: String,
)
