package com.aybarsacar.pokedexjetpackcompose.pokemondetail

import androidx.lifecycle.ViewModel
import com.aybarsacar.pokedexjetpackcompose.data.remote.responses.Pokemon
import com.aybarsacar.pokedexjetpackcompose.repository.PokemonRepository
import com.aybarsacar.pokedexjetpackcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PokemonDetailViewModel @Inject constructor(private val _repository: PokemonRepository) : ViewModel() {

  suspend fun getPokemonDetail(pokemonName: String): Resource<Pokemon> {

    return _repository.getPokemonDetail(pokemonName)

  }

}