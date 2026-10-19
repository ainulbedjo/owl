package app.moviedb.core.data.repository

import app.moviedb.core.data.local.ThemeDao
import app.moviedb.core.data.local.ThemeSettingsEntity
import app.moviedb.core.model.ThemeMode
import app.moviedb.core.model.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** Keeps the single theme row in memory. */
private class FakeThemeDao : ThemeDao {
    val row = MutableStateFlow<ThemeSettingsEntity?>(null)

    override fun observe(): Flow<ThemeSettingsEntity?> = row

    override suspend fun save(entity: ThemeSettingsEntity) {
        row.value = entity
    }
}

class ThemeRepositoryTest {

    private val dao = FakeThemeDao()
    private val repository = ThemeRepositoryImpl(dao)

    @Test
    fun nothingSavedGivesTheDefaultTheme() = runTest {
        assertEquals(ThemeSettings(), repository.settings.first())
    }

    @Test
    fun savedChoiceIsReadBack() = runTest {
        repository.update(ThemeSettings(mode = ThemeMode.LIGHT, paletteId = "ocean"))

        assertEquals(ThemeSettings(ThemeMode.LIGHT, "ocean"), repository.settings.first())
    }

    @Test
    fun theModeIsStoredAsItsName() = runTest {
        repository.update(ThemeSettings(mode = ThemeMode.DARK, paletteId = "rose"))

        assertEquals("DARK", dao.row.value?.mode)
        assertEquals("rose", dao.row.value?.paletteId)
    }

    @Test
    fun savingAgainReplacesTheOldChoice() = runTest {
        repository.update(ThemeSettings(ThemeMode.LIGHT, "ocean"))
        repository.update(ThemeSettings(ThemeMode.DARK, "forest"))

        assertEquals(ThemeSettings(ThemeMode.DARK, "forest"), repository.settings.first())
    }

    @Test
    fun anUnknownModeNameFallsBackToTheDefaultMode() = runTest {
        dao.row.value = ThemeSettingsEntity(mode = "SEPIA", paletteId = "ocean")

        val settings = repository.settings.first()

        assertEquals(ThemeSettings().mode, settings.mode)
        assertEquals("ocean", settings.paletteId)
    }
}
