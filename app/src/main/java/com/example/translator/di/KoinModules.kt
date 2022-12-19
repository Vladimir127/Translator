package com.example.translator.di

import androidx.room.Room
import com.example.historyscreen.view.history.HistoryActivity
import com.example.historyscreen.view.history.HistoryInteractor
import com.example.historyscreen.view.history.HistoryViewModel
import com.example.model.data.DataModel
import com.example.repository.room.HistoryDataBase
import com.example.repository.RetrofitImplementation
import com.example.translator.view.main.MainActivity
import com.example.translator.view.main.MainInteractor
import com.example.translator.view.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
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
    scope(named<MainActivity>()) {
        scoped { MainInteractor(get(), get()) }
        viewModel { MainViewModel(get()) }
    }
}

val historyScreen = module {
    scope(named<HistoryActivity>()) {
        scoped { HistoryInteractor(get(), get()) }
        viewModel { HistoryViewModel(get()) }
    }
}
