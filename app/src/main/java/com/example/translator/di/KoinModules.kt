package com.example.translator.di

import androidx.room.Room
import com.example.model.data.DataModel
import com.example.repository.room.HistoryDataBase
import com.example.repository.RetrofitImplementation
import com.example.translator.view.main.MainInteractor
import com.example.translator.view.main.MainViewModel
import org.koin.dsl.module

val application = module {
    single { Room.databaseBuilder(get(), HistoryDataBase::class.java, "HistoryDB").build() }
    single { get<HistoryDataBase>().historyDao() }
    single<com.example.repository.Repository<List<DataModel>>> {
        com.example.repository.RepositoryImplementation(
            RetrofitImplementation()
        )
    }
    single<com.example.repository.RepositoryLocal<List<DataModel>>> {
        com.example.repository.RepositoryImplementationLocal(
            com.example.repository.RoomDataBaseImplementation(get())
        )
    }
}

val mainScreen = module {
    factory { MainViewModel(get()) }
    factory { MainInteractor(get(), get()) }
}

val historyScreen = module {
    factory { com.example.historyscreen.view.history.HistoryViewModel(get()) }
    factory {
        com.example.historyscreen.view.history.HistoryInteractor(
            get(),
            get()
        )
    }
}
