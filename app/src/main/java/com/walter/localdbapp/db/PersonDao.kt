package com.walter.localdbapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person)

    @Query("SELECT * FROM person WHERE id = :id")
    suspend fun getPersonById(id: Int): Person?

    @Query("SELECT * FROM person WHERE email = :email")
    suspend fun getPersonByEmail(email: String): Person?

    @Query("SELECT * FROM person")
    suspend fun getAllPersons(): List<Person>

    @Query("DELETE FROM person WHERE id = :id")
    suspend fun deletePersonById(id: Int)

    @Query("DELETE FROM person")
    suspend fun deleteAllPersons()

    @Query("SELECT COUNT(*) FROM person")
    suspend fun getCount(): Int

}