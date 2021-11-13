package com.aybarsacar.pokedexjetpackcompose.data.remote

import com.aybarsacar.pokedexjetpackcompose.data.remote.responses.Pokemon
import com.aybarsacar.pokedexjetpackcompose.data.remote.responses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Api calls for the pokemon
 */
interface PokeApi {

  @GET("pokemon")
  suspend fun getPokemonList(
    @Query("limit") limit: Int,
    @Query("offset") offset: Int
  ): PokemonList


  @GET("pokemon/{name}")
  suspend fun getPokemonDetail(
    @Path("name") name: String
  ): Pokemon

}