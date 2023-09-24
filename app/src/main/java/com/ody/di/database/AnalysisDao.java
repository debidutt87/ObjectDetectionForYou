package com.ody.di.database;

import com.ody.di.database.entities.Analysis;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Data Access Object (DAO) for the Analysis entity. This interface provides methods for
 * performing CRUD (Create, Read, Update, Delete) operations on the Analysis table.
 *
 * @author Debidutt Prasad
 */
@Dao
public interface AnalysisDao {

    /**
     * Inserts an analysis into the database.
     *
     * @param analysis The analysis entity to be inserted.
     * @return The newly generated serial number (ID) for the inserted analysis.
     */
    @Insert
    long insertAnalysis(Analysis analysis);

    /**
     * Retrieves an Analysis entry based on a provided serial number.
     *
     * @param serialNum The serial number (ID) of the analysis entry to be retrieved.
     * @return The Analysis entry matching the given serial number, or null if not found.
     */
    @Query("SELECT * FROM Analysis WHERE serialNumber = :serialNum")
    Analysis getAnalysisBySerialNumber(long serialNum);

    /**
     * Deletes an Analysis entry based on a provided serial number.
     *
     * @param serialNum The serial number (ID) of the analysis entry to be deleted.
     * @return The number of rows deleted. Should be 1 if the deletion was successful, 0 otherwise.
     */
    @Query("DELETE FROM Analysis WHERE serialNumber = :serialNum")
    int deleteAnalysisBySerialNumber(long serialNum);

    /**
     * Retrieves all Analysis entries from the database.
     *
     * @return A LiveData list containing all Analysis entries. The list is observed for changes,
     * allowing for automatic UI updates.
     */
    @Query("SELECT * FROM analysis")
    LiveData<List<Analysis>> getAll();
}
