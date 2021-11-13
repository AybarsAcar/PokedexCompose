package com.aybarsacar.pokedexjetpackcompose.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.aybarsacar.pokedexjetpackcompose.data.model.PokedexListEntry
import com.aybarsacar.pokedexjetpackcompose.repository.PokemonRepository
import com.aybarsacar.pokedexjetpackcompose.util.Constants
import com.aybarsacar.pokedexjetpackcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class PokemonListViewModel @Inject constructor(private val _repository: PokemonRepository) : ViewModel() {

  private var _currentPage = 0

  var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
  var loadError = mutableStateOf("")
  var isLoading = mutableStateOf(false)
  var endReached = mutableStateOf(false)


  init {
    // first initial load
    loadPokemonPaginated()
  }


  /**
   * loads the next batch of Pokemon
   */
  fun loadPokemonPaginated() {
    viewModelScope.launch {

      isLoading.value = true

      val result = _repository.getPokemonList(Constants.PAGE_SIZE, _currentPage * Constants.PAGE_SIZE)

      when (result) {
        is Resource.Success -> {

          endReached.value = _currentPage * Constants.PAGE_SIZE >= result.data!!.count

          val pokedexEntries = result.data.results.mapIndexed { index, entry ->
            val number = if (entry.url.endsWith("/")) {
              entry.url.dropLast(1).takeLastWhile { it.isDigit() }
            } else {
              entry.url.takeLastWhile { it.isDigit() }
            }

            val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

            PokedexListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
          }

          _currentPage++

          loadError.value = ""
          isLoading.value = false
          pokemonList.value = pokedexEntries

        }

        is Resource.Error -> {
          loadError.value = result.message!!
          isLoading.value = false
        }
      }
    }
  }


  fun calculateDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {

    val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

    Palette.from(bitmap).generate { palette ->
      palette?.dominantSwatch?.rgb?.let {
        onFinish(Color(it))
      }
    }
  }
}