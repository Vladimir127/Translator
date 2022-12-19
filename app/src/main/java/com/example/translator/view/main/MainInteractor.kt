package com.example.translator.view.main

import com.example.model.data.AppState
import com.example.model.data.dto.SearchResultDto
import com.example.core.viewmodel.Interactor
import com.example.translator.utils.mapSearchResultToResult

class MainInteractor(
    private val repositoryRemote: com.example.repository.Repository<List<SearchResultDto>>,
    private val repositoryLocal: com.example.repository.RepositoryLocal<List<SearchResultDto>>
) : Interactor<AppState> {

    override suspend fun getData(word: String, fromRemoteSource: Boolean): AppState {
        val appState: AppState
        if (fromRemoteSource) {
            appState = AppState.Success(mapSearchResultToResult(repositoryRemote.getData(word)))
            repositoryLocal.saveToDB(appState)
        } else {
            appState = AppState.Success(mapSearchResultToResult(repositoryLocal.getData(word)))
        }
        return appState
    }
}
