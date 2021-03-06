package com.aybarsacar.pokedexjetpackcompose.pokemondetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.aybarsacar.pokedexjetpackcompose.R
import com.aybarsacar.pokedexjetpackcompose.data.remote.responses.Pokemon
import com.aybarsacar.pokedexjetpackcompose.data.remote.responses.Type
import com.aybarsacar.pokedexjetpackcompose.util.Resource
import com.aybarsacar.pokedexjetpackcompose.util.parseStatToAbbr
import com.aybarsacar.pokedexjetpackcompose.util.parseStatToColor
import com.aybarsacar.pokedexjetpackcompose.util.parseTypeToColor
import java.util.*
import kotlin.math.round


@Composable
fun PokemonDetailScreen(
  dominantColor: Color,
  pokemonName: String,
  navController: NavController,
  topPadding: Dp = 20.dp,
  imageSize: Dp = 200.dp,
  viewModel: PokemonDetailViewModel = hiltViewModel()
) {

  val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
    value = viewModel.getPokemonDetail(pokemonName)
  }.value


  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(dominantColor)
      .padding(bottom = 16.dp)
  ) {

    PokemonDetailTopSection(
      navController = navController,
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.2f)
        .align(Alignment.TopCenter)
    )

    PokemonDetailStateWrapper(
      pokemonInfo = pokemonInfo,
      modifier = Modifier
        .fillMaxSize()
        .padding(top = topPadding + imageSize / 2f, start = 16.dp, end = 16.dp, bottom = 16.dp)
        .shadow(10.dp, RoundedCornerShape(10.dp))
        .clip(RoundedCornerShape(10.dp))
        .background(MaterialTheme.colors.surface)
        .padding(16.dp)
        .align(Alignment.BottomCenter),
      loadingModifier = Modifier
        .size(100.dp)
        .align(Alignment.Center)
        .padding(top = topPadding + imageSize / 2f, start = 16.dp, end = 16.dp, bottom = 16.dp)
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
      if (pokemonInfo is Resource.Success) {

        pokemonInfo.data?.sprites?.let {
          Image(
            painter = rememberImagePainter(
              data = it.frontDefault,
              builder = {
                crossfade(true)
              }
            ),
            contentDescription = pokemonInfo.data.name,
            modifier = Modifier
              .size(imageSize)
              .offset(y = topPadding)
          )
        }
      }
    }
  }
}


@Composable
fun PokemonDetailTopSection(
  navController: NavController,
  modifier: Modifier = Modifier
) {

  Box(
    contentAlignment = Alignment.TopStart,
    modifier = modifier.background(
      Brush.verticalGradient(
        listOf(Color.Black, Color.Transparent)
      )
    )
  ) {

    Icon(
      imageVector = Icons.Default.ArrowBack,
      contentDescription = "Back Button",
      tint = Color.White,
      modifier = Modifier
        .size(36.dp)
        .offset(16.dp, 16.dp)
        .clickable {
          // navigate to the previous screen
          navController.popBackStack()
        }
    )
  }
}


@Composable
fun PokemonDetailStateWrapper(
  pokemonInfo: Resource<Pokemon>,
  modifier: Modifier = Modifier,
  loadingModifier: Modifier = Modifier
) {

  when (pokemonInfo) {

    is Resource.Success -> {
      PokemonDetailSection(currentPokemon = pokemonInfo.data!!, modifier = modifier.offset(y = (-20).dp))
    }

    is Resource.Error -> {
      Text(text = pokemonInfo.message!!, color = Color.Red, modifier = modifier)
    }

    is Resource.Loading -> {
      CircularProgressIndicator(color = MaterialTheme.colors.primary, modifier = loadingModifier)
    }
  }
}


@Composable
fun PokemonDetailSection(
  currentPokemon: Pokemon,
  modifier: Modifier = Modifier
) {

  val scrollState = rememberScrollState()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxSize()
      .offset(y = 100.dp)
      .verticalScroll(scrollState)
  ) {

    Text(
      text = "#${currentPokemon.id} ${currentPokemon.name.capitalize(Locale.ROOT)}",
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colors.onSurface,
      fontSize = 30.sp
    )

    PokemonTypeSection(types = currentPokemon.types)

    PokemonDetailDataSection(pokemonWeight = currentPokemon.weight, pokemonHeight = currentPokemon.height)

    PokemonBaseStats(currentPokemon = currentPokemon)

  }
}


@Composable
fun PokemonTypeSection(
  types: List<Type>
) {

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
  ) {

    for (type in types) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
          .clip(CircleShape)
          .background(color = parseTypeToColor(type))
          .height(35.dp)
      ) {

        Text(text = type.type.name.capitalize(Locale.ROOT), fontSize = 18.sp)
      }
    }
  }
}


@Composable
fun PokemonDetailDataSection(
  pokemonWeight: Int,
  pokemonHeight: Int,
  sectionHeight: Dp = 80.dp
) {

  // use remember because we dont want to execute this calculation at each render
  val pokemonWeightInKg = remember { round(pokemonWeight * 100f) / 1000f }
  val pokemonHeightInM = remember { round(pokemonHeight * 100f) / 1000f }

  Row(
    modifier = Modifier.fillMaxWidth()
  ) {
    PokemonDetailDataItem(
      dataValue = pokemonWeightInKg,
      dataUnit = "kg",
      dataIcon = painterResource(id = R.drawable.ic_weight),
      modifier = Modifier.weight(1f)
    )

    Spacer(
      modifier = Modifier
        .size(1.dp, sectionHeight)
        .background(Color.LightGray)
    )

    PokemonDetailDataItem(
      dataValue = pokemonHeightInM,
      dataUnit = "m",
      dataIcon = painterResource(id = R.drawable.ic_height),
      modifier = Modifier.weight(1f)
    )
  }
}


@Composable
fun PokemonDetailDataItem(
  dataValue: Float,
  dataUnit: String,
  dataIcon: Painter,
  modifier: Modifier = Modifier
) {

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
  ) {

    Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colors.onSurface)

    Spacer(modifier = Modifier.height(8.dp))

    Text(text = "$dataValue $dataUnit", color = MaterialTheme.colors.onSurface)
  }
}


@Composable
fun PokemonStat(
  statName: String,
  statValue: Int,
  statMaxValue: Int,
  statColor: Color,
  height: Dp = 28.dp,
  animDuration: Int = 1000,
  animDelay: Int = 0
) {

  var animationPlayed by remember { mutableStateOf(false) }

  val currentPercent = animateFloatAsState(
    targetValue = if (animationPlayed) {
      statValue / statMaxValue.toFloat() // final percentage amount
    } else {
      0f
    },
    animationSpec = tween(animDuration, animDelay),
  )

  // this will run once because we pass a constant value
  LaunchedEffect(key1 = true) {
    animationPlayed = true
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(height = height)
      .clip(CircleShape)
      .background(
        if (isSystemInDarkTheme()) {
          Color(0xFF505050)
        } else {
          Color.LightGray
        }
      )
  ) {

    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(currentPercent.value)
        .clip(CircleShape)
        .background(statColor)
        .padding(horizontal = 8.dp)
    ) {

      Text(text = statName, fontWeight = FontWeight.Bold)

      Text(text = (currentPercent.value * statMaxValue).toInt().toString(), fontWeight = FontWeight.Bold)
    }
  }
}


@Composable
fun PokemonBaseStats(
  currentPokemon: Pokemon,
  animDelayPerItem: Int = 100
) {

  val maxBaseStat = remember { currentPokemon.stats.maxOf { it.baseStat } }

  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(text = "Base Stats:", fontSize = 20.sp, color = MaterialTheme.colors.onSurface)

    Spacer(modifier = Modifier.height(4.dp))

    // loop over the stats and display the stat item
    for (i in currentPokemon.stats.indices) {
      val stat = currentPokemon.stats[i]

      PokemonStat(
        statName = parseStatToAbbr(stat),
        statValue = stat.baseStat,
        statMaxValue = maxBaseStat,
        statColor = parseStatToColor(stat),
        animDelay = i * animDelayPerItem
      )

      Spacer(modifier = Modifier.height(8.dp))
    }
  }

}