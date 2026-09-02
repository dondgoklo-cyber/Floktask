package com.taskmanager.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 2 to 3.
 * Adds geolocation reminder support.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add geofence/location fields to tasks
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN geofenceId TEXT
        """)
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN geofenceLatitude REAL
        """)
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN geofenceLongitude REAL
        """)
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN geofenceRadius REAL
        """)
        database.execSQL("""
            ALTER TABLE tasks ADD COLUMN geofenceTransitionType INTEGER
        """)
    }
}
