/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
 • The sleep database is abstract because the room library will implement it for us and will make it
 a singleton
 • In entities parameter you can add more then just one data class and then each one will have a
 table that accept this class instances.
 • You have to update the version number each time you change the schema of the database.
 • exportSchema will save the schema of the database to a folder which help you track the version
 changes of you schema
 */
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase(): RoomDatabase(){

    // • Each table should have a dao
    abstract val sleepDatabaseDao : SleepDatabaseDao

    companion object{
        @Volatile
        // • @Volatile means that the instance of the database is always up to date and the same for all
        // execution threads.
        private var INSTANCE : SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase{
            // • inside a synchronized block just one thread can enter.
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    // • The Migration function is used when the you update your app version and in the
                    // new version you have changed the schema of the database.
                    // Migration code is set of instructions to convert the old database to the new one.
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance // this is smart cast because databaseBuilder returns RoomDatabase
            }
        }
    }

}
