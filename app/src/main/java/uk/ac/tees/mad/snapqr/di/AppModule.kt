package uk.ac.tees.mad.snapqr.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.snapqr.data.AppDatabase
import uk.ac.tees.mad.snapqr.data.ScanHistoryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRoom(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "snapqr_db")
            .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun providesDao(db: AppDatabase): ScanHistoryDao = db.scanHistoryDao()
}