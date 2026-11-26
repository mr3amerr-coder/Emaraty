package com.zero.amaraty.data.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AmaratyDatabaseTest {
    private lateinit var database: AmaratyDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AmaratyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun unitCannotExistWithoutItsProperty() = runBlocking {
        val unit = sampleUnit(propertyId = "missing-property")
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking { database.unitDao().insert(unit) }
        }
    }

    @Test
    fun propertyAndUnitPersistWithHundredthsOfSquareMetre() = runBlocking {
        val property = sampleProperty()
        val unit = sampleUnit(propertyId = property.id, areaHundredths = 8_750L)

        database.propertyDao().insert(property)
        database.unitDao().insert(unit)

        assertNotNull(database.propertyDao().getById(property.id))
        assertEquals(8_750L, database.unitDao().getById(unit.id)?.areaHundredths)
    }

    @Test
    fun duplicateUnitNumberWithinSamePropertyIsRejected() = runBlocking {
        val property = sampleProperty()
        database.propertyDao().insert(property)
        database.unitDao().insert(sampleUnit(id = "unit-1", propertyId = property.id))

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                database.unitDao().insert(sampleUnit(id = "unit-2", propertyId = property.id))
            }
        }
    }

    @Test
    fun auditDaoExposesAppendAndReadButNoUpdateOrDeleteOperations() = runBlocking {
        val entry = AuditLogEntity(
            id = "audit-1",
            entityType = "PROPERTY",
            entityId = "property-1",
            action = "CREATED",
            occurredAtEpochMillis = 1_000L,
            safeSummary = "Created property record",
        )
        database.auditLogDao().append(entry)
        assertEquals(listOf(entry), database.auditLogDao().observeRecent().first())
    }

    private fun sampleProperty() = PropertyEntity(
        id = "property-1",
        name = "عقاري",
        propertyType = "BUILDING_UNION",
        governorate = "Cairo",
        city = "Nasr City",
        addressText = null,
        floorCount = 5,
        hasElevator = true,
        hasGarage = false,
        hasShops = false,
        createdAtEpochMillis = 1_000L,
        updatedAtEpochMillis = 1_000L,
    )

    private fun sampleUnit(
        id: String = "unit-1",
        propertyId: String,
        areaHundredths: Long? = null,
    ) = UnitEntity(
        id = id,
        propertyId = propertyId,
        unitNumber = "101",
        floorNumber = 1,
        unitType = "RESIDENTIAL",
        areaHundredths = areaHundredths,
        allocationWeight = 1_000L,
        occupancyStatus = "OCCUPIED",
        factualNotes = null,
        createdAtEpochMillis = 1_000L,
        updatedAtEpochMillis = 1_000L,
    )
}

