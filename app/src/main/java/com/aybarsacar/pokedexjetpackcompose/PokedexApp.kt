package com.aybarsacar.pokedexjetpackcompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class PokedexApp : Application() {

  override fun onCreate() {
    super.onCreate()

    // override debugging with Timber
    Timber.plant(Timber.DebugTree())
  }

}