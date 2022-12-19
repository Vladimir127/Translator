package com.example.translator.utils

import com.example.model.data.AppState
import com.example.model.data.dto.SearchResultDto
import com.example.model.data.dto.MeaningsDto
import com.example.model.data.userdata.DataModel
import com.example.model.data.userdata.Meaning
import com.example.model.data.userdata.TranslatedMeaning
import com.example.repository.room.HistoryEntity

fun mapSearchResultToResult(searchResults: List<SearchResultDto>):
        List<DataModel> {
    return searchResults.map { searchResult ->
        var meanings: List<Meaning> = listOf()
        searchResult.meanings?.let { // Дополнительная проверка для
            // HistoryScreen, так как там сейчас не
            // отображаются значения
            meanings = it.map { meaningsDto ->
                Meaning(
                    TranslatedMeaning(
                        meaningsDto.translation?.translation ?: ""
                    ),
                    meaningsDto.imageUrl ?: ""
                )
            }
        }
        DataModel (searchResult.text ?: "", meanings)
    }
}

fun parseOnlineSearchResults(appState: AppState): AppState {
    return AppState.Success(mapResult(appState, true))
}

private fun mapResult(
    appState: AppState,
    isOnline: Boolean
): List<DataModel> {
    val newSearchResults = arrayListOf<DataModel>()
    when (appState) {
        is AppState.Success -> {
            getSuccessResultData(appState, isOnline, newSearchResults)
        }
    }
    return newSearchResults
}

private fun getSuccessResultData(
    appState: AppState.Success,
    isOnline: Boolean,
    newDataModels: ArrayList<DataModel>
) {
    val dataModels: List<DataModel> = appState.data as List<DataModel>
    if (dataModels.isNotEmpty()) {
        if (isOnline) {
            for (searchResult in dataModels) {
                parseOnlineResult(searchResult, newDataModels)
            }
        } else {
            for (searchResult in dataModels) {
                newDataModels.add(DataModel(searchResult.text, arrayListOf()))
            }
        }
    }
}

private fun parseOnlineResult(
    searchDataModel: DataModel,
    newSearchDataModels: ArrayList<DataModel>
) {
    if (searchDataModel.text.isNotBlank() && searchDataModel.meanings.isNotEmpty()) {
        val newMeanings = arrayListOf<Meaning>()
        newMeanings.addAll(searchDataModel.meanings.filter { it.translatedMeaning.translatedMeaning.isNotBlank() })
        if (newMeanings.isNotEmpty()) {
            newSearchDataModels.add(
                DataModel(
                    searchDataModel.text,
                    newMeanings
                )
            )
        }
    }
}

fun mapHistoryEntityToSearchResult(list: List<HistoryEntity>): List<SearchResultDto> {
    val searchResult = ArrayList<SearchResultDto>()
    if (!list.isNullOrEmpty()) {
        for (entity in list) {
            searchResult.add(SearchResultDto(entity.word, null))
        }
    }
    return searchResult
}

fun convertDataModelSuccessToEntity(appState: AppState): HistoryEntity? {
    return when (appState) {
        is AppState.Success -> {
            val searchResult = appState.data
            if (searchResult.isNullOrEmpty() || searchResult[0].text.isNullOrEmpty()) {
                null
            } else {
                HistoryEntity(searchResult[0].text, null)
            }
        }
        else -> null
    }
}


fun convertMeaningsToString(meanings: List<Meaning>): String {
    var meaningsSeparatedByComma = String()
    for ((index, meaning) in meanings.withIndex()) {
        meaningsSeparatedByComma += if (index + 1 != meanings.size) {
            String.format("%s%s", meaning.translatedMeaning.translatedMeaning, ", ")
        } else {
            meaning.translatedMeaning.translatedMeaning
        }
    }
    return meaningsSeparatedByComma
}
