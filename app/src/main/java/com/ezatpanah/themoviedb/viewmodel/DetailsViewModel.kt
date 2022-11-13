package com.ezatpanah.themoviedb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezatpanah.themoviedb.db.MoviesEntity
import com.ezatpanah.themoviedb.repository.ApiRepository
import com.ezatpanah.themoviedb.repository.DatabaseRepository
import com.ezatpanah.themoviedb.response.DetailsMovieResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject
constructor(
    private val apiRepository: ApiRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    //Api
    val detailsMovie = MutableLiveData<DetailsMovieResponse>()
    val loading = MutableLiveData<Boolean>()

    fun loadDetailsMovie(id: Int) = viewModelScope.launch {
        loading.postValue(true)
        val response = apiRepository.getMovieDetails(id)
        if (response.isSuccessful) {
            detailsMovie.postValue(response.body())
        }
        loading.postValue(false)
    }

    //Database
    val isFavorite = MutableLiveData<Boolean>()
    suspend fun existMovie(id:Int)= withContext(viewModelScope.coroutineContext){
        databaseRepository.existMovie(id)
    }

    fun favoriteMovie(id:Int, entity: MoviesEntity)=viewModelScope.launch {
        val exists= databaseRepository.existMovie(id)
        if (exists){
            isFavorite.postValue(false)
            databaseRepository.deleteMovie(entity)
        }else{
            isFavorite.postValue(true)
            databaseRepository.insertMovie(entity)
        }
    }



}