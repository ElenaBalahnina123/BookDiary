package com.elena_balakhnina.bookdiary.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.startup.Initializer

class AppDbInitializer : Initializer<AppDatabase> {

    override fun create(context: Context): AppDatabase {
        Log.d("AppDbInitializer","call create(...)")

        val db = Room.databaseBuilder(context, AppDatabase::class.java, "books_diary")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        val count = db.genreDao().countGenresSync()
        if(count == 0L) {
            val defaultGenres = getDefaultGenres()
            db.genreDao().insertGenresSync(defaultGenres.map {
                GenreDBEntity(it.key,it.value)
            })
        }

        return db
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    private fun getDefaultGenres() = mapOf(
        "GENRE_NOT_SET" to "Не указан",
        "1BWhTi136dqD8fIQyY56" to "Приключения",
        "1MRhhExOlq1y3E4blnKr" to "Мистика",
        "1Okohhrax4Amh9oSx4YP" to "Фанфик",
        "4ztwSG4Fuaudz9DCrMT3" to "Вестерн",
        "59jmvfu2Wb4pyeMbAZ5q" to "Юмор",
        "7tKyZdOl0ZXU0eM5S8is" to "Художественная литература",
        "8tdOLmMjSkrBGOT6XONt" to "Наука",
        "A762F8e33rXRDPw92Dyd" to "Психология",
        "ByqPKAsYRGX3l2O9qZlf" to "Биография",
        "DTAHWNKNDyRgaJTS5Obq" to "Научная фантастика",
        "EGkhBvf9Sn1FGGgLjzzw" to "Боевик",
        "EoHoR2JsGszvJZH7XrPP" to "Повесть",
        "FjNrFiV5J2ZclFLbtOjk" to "Техника",
        "ItYMC9akFR7q1HnmOMTG" to "Авангардная литература",
        "JCHnbZf0pigz9BwdSNad" to "Комикс, манга",
        "JnRmznnnpe2YzOG5xWPD" to "Любовный роман",
        "O4mCK3ItaRPg60huMYta" to "Сказка",
        "O8CZKN1FR7IA0dHJ6778" to "Поэзия",
        "Ojt7yXJC7hZcEk3zCATe" to "Воспитание",
        "QqrHmUfMYsV7YV5KNZ9e" to "Искусство",
        "R5I7hCX5nTfrOzeJLUBf" to "Триллер",
        "UQ77wr0iRm1d7g5YDRtv" to "Бизнес",
        "UWlsCY9bSiwLWHbcObtu" to "Классика",
        "VGjNpoj72WHP3RyiaYmN" to "Детская литература",
        "W6LdkU9R3dbLPK1dfSpF" to "18+",
        "YkPCR44CBpQYxjGnhByl" to "Питание и кулинария",
        "ZmOZkGL6GaChpoefvHaR" to "Фантастика",
        "b2R2iQNptbh9Kyh9o08w" to "Политика, экономика и право",
        "bCj4KTiWmXid59aQp4vS" to "Журнал, газета",
        "dXnd8Uf0aCijJOXxayTk" to "Young Adult",
        "dvCaw0okFv1VAJ4ABw9b" to "Мифы и легенды",
        "hPi7bYiKZhabDxIIOUNf" to "Энциклопедия",
        "hjotzpjwAQgdych035XP" to "Философия",
        "lx5NGbQz5J5RX7V2tzfN" to "Мода и красота",
        "mNgzTQaQhauamaCsqkoi" to "Роман",
        "pHHozQs9tj2uTdMnNUhE" to "Современная литература",
        "qYJEswtfLR5LzBJ9DUKG" to "Ужасы",
        "rWq9EoyXg8jgiFsZRdLF" to "Учебная литература",
        "tC53iNZ1HUArs7SuqSLp" to "Исторический роман",
        "tYqDn7kJEkP8fHFGdN7t" to "Детектив",
        "zPUR2cFzis19MFtIgUEm" to "Здоровье",
        "zwFrZo27PfIJhMRRI3fo" to "Фэнтези",
    )
}