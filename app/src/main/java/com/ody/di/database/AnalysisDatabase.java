package com.ody.di.database;

import android.content.Context;

import com.ody.di.database.entities.Analysis;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Represents the Room database for the Analysis entities. This database provides an abstract layer over SQLite
 * to allow for more robust database access while still providing the benefits of SQLite.
 *
 * <p>This class includes:
 * <ul>
 *     <li>The entities (tables) associated with the database.</li>
 *     <li>The version number of the database, useful for migrations.</li>
 *     <li>Control over whether or not to export the schema.</li>
 * </ul>
 * </p>
 *
 * @author Debidutt Prasad
 */
@Database(entities = {Analysis.class}, version = 1, exportSchema = false)
public abstract class AnalysisDatabase extends RoomDatabase {

    /**
     * Provides access to the DAO (Data Access Object) which includes methods to interact with the Analysis table.
     *
     * @return The DAO associated with the Analysis table.
     */
    public abstract AnalysisDao analysisDao();

    /**
     * Singleton instance of the database. The "volatile" keyword ensures that changes to the INSTANCE variable
     * are immediately visible to all other threads.
     */
    private static volatile AnalysisDatabase INSTANCE;

    /**
     * Retrieves the singleton instance of the AnalysisDatabase. If it doesn't exist, it initializes and returns it.
     *
     * @param context The context used to get the application context and build the database.
     * @return The singleton instance of the AnalysisDatabase.
     */
    public static AnalysisDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AnalysisDatabase.class) {
                if (INSTANCE == null) {
                    // Use "context.getApplicationContext()" to ensure you're using the app context,
                    // which helps to avoid potential memory leaks.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AnalysisDatabase.class, "analysis_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
