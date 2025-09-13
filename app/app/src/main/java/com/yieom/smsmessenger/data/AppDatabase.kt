package com.yieom.smsmessenger.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yieom.smsmessenger.data.dao.MessageDao
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.util.Arrays

@Database(entities = [com.yieom.smsmessenger.data.model.Message::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, passphrase_char_array: CharArray): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase_char_array))
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sms_messenger_db"
                    )
                        .fallbackToDestructiveMigration() // 실제 앱에서는 마이그레이션 전략을 구현해야 합니다.
                        .openHelperFactory(factory)
                        .build()
                    INSTANCE = instance
                    // Passphrase를 더 이상 메모리에 유지하지 않도록 초기화합니다.
                    Arrays.fill(passphrase_char_array, ' ')
                }
                return instance
            }
        }
    }
}