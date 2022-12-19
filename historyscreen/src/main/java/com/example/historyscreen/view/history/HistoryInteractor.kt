package com.example.historyscreen.view.history

import com.example.core.viewmodel.Interactor
import com.example.historyscreen.mapSearchResultToResult
import com.example.model.data.AppState
import com.example.model.data.dto.SearchResultDto
import com.example.repository.Repository
import com.example.repository.RepositoryLocal

class HistoryInteractor(
    private val repositoryRemote: Repository<List<SearchResultDto>>,
    private val repositoryLocal: RepositoryLocal<List<SearchResultDto>>
) : Interactor<AppState> {

    override suspend fun getData(
        word: String,
        fromRemoteSource: Boolean
    ): AppState {
        return AppState.Success(
            mapSearchResultToResult(
                if (fromRemoteSource) {
                    repositoryRemote
                } else {
                    repositoryLocal
                }.getData(word)
            )
        )
    }
}
