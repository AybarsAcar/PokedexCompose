package com.aybarsacar.pokedexjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aybarsacar.pokedexjetpackcompose.pokemondetail.PokemonDetailScreen
import com.aybarsacar.pokedexjetpackcompose.pokemonlist.PokemonListScreen
import com.aybarsacar.pokedexjetpackcompose.ui.theme.PokedexJetpackComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      PokedexJetpackComposeTheme {

        // setup navigation
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "pokemon_list_screen") {

          composable("pokemon_list_screen") {
            // render list screen
            PokemonListScreen(navController = navController)
          }

          // params are passed like a url - like building web apps
          composable(
            "pokemon_detail_screen/{dominantColor}/{pokemonName}",
            arguments = listOf(
              navArgument("dominantColor") {
                type = NavType.IntType
              },
              navArgument("pokemonName") {
                type = NavType.StringType
              }
            )
          ) {
            // get the dominant color from the url param passed in
            val dominantColor = remember {
              val color = it.arguments?.getInt("dominantColor")

              color?.let { Color(it) } ?: Color.White
            }

            // get the pokemon name form the url param passed in
            val pokemonName = remember {
              it.arguments?.getString("pokemonName") ?: ""
            }

            // render detail screen
            PokemonDetailScreen(
              dominantColor = dominantColor,
              pokemonName = pokemonName.lowercase(Locale.ROOT),
              navController = navController
            )
          }

        }
      }
    }
  }
}