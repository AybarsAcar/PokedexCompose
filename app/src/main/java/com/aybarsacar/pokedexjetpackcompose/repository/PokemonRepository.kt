package com.aybarsacar.pokedexjetpackcompose.repository

import com.aybarsacar.pokedexjetpackcompose.data.remote.PokeApi
import com.aybarsacar.pokedexjetpackcompose.data.remote.responses.Pokemon
import com.aybarsacar.pokedexjetpackcompose.data.remote.responses.PokemonList
import com.aybarsacar.pokedexjetpackcompose.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject


/**
 * API calls - Create an interface if you wish
 * network request actual implementations
 * scoped to the lifetime of the activity
 */
@ActivityScoped
class PokemonRepository @Inject constructor(
  private val _api: PokeApi
) {

  suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {

    val response = try {
      _api.getPokemonList(limit, offset)
    } catch (e: Exception) {
      return Resource.Error(e.localizedMessage ?: "An error occurred")
    }

    return Resource.Success(response)
  }


  suspend fun getPokemonDetail(name: String): Resource<Pokemon> {

    val response = try {
      _api.getPokemonDetail(name)
    } catch (e: Exception) {
      return Resource.Error(e.localizedMessage ?: "An error occurred")
    }

    return Resource.Success(response)
  }
}