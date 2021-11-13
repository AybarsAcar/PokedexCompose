package com.aybarsacar.pokedexjetpackcompose.pokemonlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.aybarsacar.pokedexjetpackcompose.R
import com.aybarsacar.pokedexjetpackcompose.data.model.PokedexListEntry
import com.aybarsacar.pokedexjetpackcompose.ui.theme.RobotoCondensed


@Composable
fun PokemonListScreen(
  navController: NavController,
) {

  Surface(
    color = MaterialTheme.colors.background,
    modifier = Modifier.fillMaxSize()
  ) {

    Column {

      Spacer(modifier = Modifier.height(20.dp))

      Image(
        painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
        contentDescription = "logo",
        modifier = Modifier
          .fillMaxWidth()
          .align(CenterHorizontally)
      )

      SearchBar(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        hint = "Search",
        onSearch = {}
      )

      Spacer(modifier = Modifier.height(16.dp))

      PokemonList(navController = navController)


    }
  }
}


@Composable
fun SearchBar(
  modifier: Modifier = Modifier,
  hint: String = "",
  onSearch: (String) -> Unit
) {

  var text by remember { mutableStateOf("") }
  var isHintDisplayed by remember { mutableStateOf(hint != "") }

  Box(modifier = modifier) {

    BasicTextField(
      value = text,
      onValueChange = {
        text = it
        onSearch(text)
      },
      maxLines = 1,
      singleLine = true,
      textStyle = TextStyle(color = Color.Black),
      modifier = Modifier
        .fillMaxWidth()
        .shadow(5.dp, CircleShape)
        .background(Color.White, CircleShape)
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .onFocusChanged {
          isHintDisplayed = !it.isFocused
        }
    )

    if (isHintDisplayed) {
      Text(
        text = hint, color = Color.LightGray, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
      )
    }
  }
}


@Composable
fun PokemonList(
  navController: NavController,
  viewModel: PokemonListViewModel = hiltViewModel()
) {
  val pokemonList by remember { viewModel.pokemonList }
  val endReached by remember { viewModel.endReached }
  val loadError by remember { viewModel.loadError }
  val isLoading by remember { viewModel.isLoading }


  LazyColumn(contentPadding = PaddingValues(16.dp)) {

    // since it's a grid 2 pokemon entry is a one item in the LazyColumn
    val itemCount = if (pokemonList.size % 2 == 0) pokemonList.size / 2 else pokemonList.size / 2 + 1

    items(itemCount) {

      if (it >= itemCount - 1 && !endReached && !isLoading) {
        // we scrolled to the bottom so paginate
        // which will add to the lazy column
        viewModel.loadPokemonPaginated()
      }

      PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
    }
  }

  Box(
    contentAlignment = Center,
    modifier = Modifier.fillMaxSize()
  ) {

    if (isLoading) {
      CircularProgressIndicator(color = MaterialTheme.colors.primary)
    }

    if (loadError.isNotEmpty()) {
      RetrySection(error = loadError) {
        viewModel.loadPokemonPaginated()
      }
    }

  }
}


@ExperimentalCoilApi
@Composable
fun PokedexEntry(
  entry: PokedexListEntry,
  navController: NavController,
  modifier: Modifier = Modifier,
  viewModel: PokemonListViewModel = hiltViewModel()
) {

  val defaultDominantColor = MaterialTheme.colors.surface

  var dominantColor by remember { mutableStateOf(defaultDominantColor) }

  Box(
    modifier = modifier
      .shadow(5.dp)
      .clip(RoundedCornerShape(10.dp))
      .aspectRatio(1f)
      .background(
        Brush.verticalGradient(
          listOf(dominantColor, defaultDominantColor)
        )
      )
      .clickable {
        // navigate to detail
        navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}")
      },
    contentAlignment = Alignment.Center
  ) {

    Column {

      /* val request = ImageRequest.Builder(LocalContext.current)
         .data(entry.imageUrl)
         .target {

           println("HERE INSIDE")

           viewModel.calculateDominantColor(it) { color ->
             // TODO: this block does not execute
             dominantColor = color
           }
         }
         .build()

       val imageLoader = ImageLoader(LocalContext.current)

       val imagePainter = rememberCoilPainter(
         request = request,
         imageLoader = imageLoader
       ) */

      val painter = rememberImagePainter(data = entry.imageUrl, builder = {
        crossfade(true)
      })

      if (painter.state is ImagePainter.State.Loading) {

        CircularProgressIndicator(
          modifier = Modifier
            .size(64.dp)
            .align(Alignment.CenterHorizontally),
          color = Color.LightGray
        )
      }

      Image(
        painter = painter,
        contentDescription = entry.pokemonName,
        modifier = Modifier
          .size(120.dp)
          .align(Alignment.CenterHorizontally)
      )



      Text(
        modifier = Modifier.fillMaxWidth(),
        text = entry.pokemonName,
        fontFamily = RobotoCondensed,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
      )
    }

  }
}


@Composable
fun PokedexRow(
  rowIndex: Int,
  entries: List<PokedexListEntry>,
  navController: NavController
) {

  Column {
    Row {

      PokedexEntry(entry = entries[rowIndex * 2], navController = navController, modifier = Modifier.weight(1f))

      Spacer(modifier = Modifier.width(16.dp))

      if (entries.size > rowIndex * 2 + 2) {
        // at least 2 more entries we can display
        PokedexEntry(entry = entries[rowIndex * 2 + 1], navController = navController, modifier = Modifier.weight(1f))
      } else {
        Spacer(modifier = Modifier.weight(16f))
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

  }

}


@Composable
fun RetrySection(
  error: String,
  onRetry: () -> Unit
) {
  Column {
    Text(error, color = Color.Red, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = { onRetry() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
      Text(text = "Retry")
    }
  }
}