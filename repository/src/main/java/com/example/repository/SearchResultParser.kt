package com.example.repository

import com.example.model.data.AppState
import com.example.model.data.dto.SearchResultDto
import com.example.model.data.dto.MeaningsDto
import com.example.repository.room.HistoryEntity

private fun mapResult(
    appState: AppState,
    isOnline: Boolean
): List<SearchResultDto> {
    val newSearchResults = arrayListOf<SearchResultDto>()
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
    newDataModels: ArrayList<SearchResultDto>
) {
    val dataModels: List<SearchResultDto> = appState.data as List<SearchResultDto>
    if (dataModels.isNotEmpty()) {
        if (isOnline) {
            for (searchResult in dataModels) {
                parseOnlineResult(searchResult, newDataModels)
            }
        } else {
            for (searchResult in dataModels) {
                newDataModels.add(SearchResultDto(searchResult.text, arrayListOf()))
            }
        }
    }
}

private fun parseOnlineResult(dataModel: SearchResultDto, newDataModels: ArrayList<SearchResultDto>) {
    if (!dataModel.text.isNullOrBlank() && !dataModel.meanings.isNullOrEmpty()) {
        val newMeanings = arrayListOf<MeaningsDto>()
        for (meaning in dataModel.meanings!!) {
            if (meaning.translation != null && !meaning.translation!!.translation.isNullOrBlank()) {
                newMeanings.add(MeaningsDto(meaning.translation, meaning.imageUrl))
            }
        }
        if (newMeanings.isNotEmpty()) {
            newDataModels.add(SearchResultDto(dataModel.text, newMeanings))
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


fun convertMeaningsToString(meanings: List<MeaningsDto>): String {
    var meaningsSeparatedByComma = String()
    for ((index, meaning) in meanings.withIndex()) {
        meaningsSeparatedByComma += if (index + 1 != meanings.size) {
            String.format("%s%s", meaning.translation?.translation, ", ")
        } else {
            meaning.translation?.translation
        }
    }
    return meaningsSeparatedByComma
}
