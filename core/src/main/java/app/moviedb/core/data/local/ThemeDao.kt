package app.moviedb.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeDao {
    @Query("SELECT * FROM theme_settings WHERE id = ${ThemeSettingsEntity.SINGLE_ROW_ID}")
    fun observe(): Flow<ThemeSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: ThemeSettingsEntity)
}
