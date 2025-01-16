package com.example.imageprocess

import ImageRestorationViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imageprocess.colorConversions.ColorConversionScreen
import com.example.imageprocess.colorConversions.ImageTransformingScreen
import com.example.imageprocess.histogram.HistogramOperationsScreen
import com.example.imageprocess.filterAndTransform.FilterAndTransformScreen
import com.example.imageprocess.filterAndTransform.ImageTransformViewModel
import com.example.imageprocess.imageRestoration.ImageRestorationScreen
import com.example.imageprocess.morphologicalTransform.MorphologicalTransformScreen
import com.example.imageprocess.morphologicalTransform.MorphologicalTransformViewModel
import com.example.imageprocess.noiseAdditionAndRemoving.NoiseAdditionAndRemoving
import com.example.imageprocess.noiseAdditionAndRemoving.NoiseAddtionAndRemovingViewModel
import com.example.imageprocess.noiseFiltering.NoiseFilteringScreen
import com.example.imageprocess.ui.theme.ImageProcessTheme
import com.example.imageprocess.viewModel.ImageProcessViewModel

class MainActivity  : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageProcessTheme {
                MyApp()

        }
    }
}


@Composable
fun MyApp(
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val imageProcessViewModel: ImageProcessViewModel = viewModel()
    val imageTransformViewModel : ImageTransformViewModel = viewModel()
    val noiseAddtionAndRemovingViewModel : NoiseAddtionAndRemovingViewModel = viewModel()
    val imageRestorationViewModel : ImageRestorationViewModel = viewModel()
    val morphologicalTransformViewModel : MorphologicalTransformViewModel = viewModel()



    NavHost(navController, startDestination = "homeScreen") {

            composable("homeScreen"){
                HomeScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState, imageProcessViewModel = imageProcessViewModel)
            }
            composable("imageTransformingScreen"){
                ImageTransformingScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState, imageProcessViewModel = imageProcessViewModel)
            }

            composable("histogramOperationsScreen"){
                HistogramOperationsScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState)
            }

            composable("colorConversionScreen"){
                ColorConversionScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState,  imageProcessViewModel = imageProcessViewModel)
            }



            composable("filterAndTransformScreen"){
                FilterAndTransformScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState,  imageTransformViewModel = imageTransformViewModel)
            }
            composable("noiseFilteringScreen"){
                NoiseFilteringScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState,  imageProcessViewModel = imageProcessViewModel)
            }

            composable("noiseAdditionRemoving"){
                NoiseAdditionAndRemoving(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState,  noiseAddtionAndRemovingViewModel = noiseAddtionAndRemovingViewModel)
            }
            composable("imageRestorationConstruction"){
                NoiseFilteringScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState,  imageProcessViewModel = imageProcessViewModel)
            }

            composable("imageRestorationScreen"){
                ImageRestorationScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState,  imageRestorationViewModel = imageRestorationViewModel)
            }

            composable("morpholohicalTransform"){
                MorphologicalTransformScreen(navController = navController, coroutineScope = coroutineScope, drawerState = drawerState,  morphologicalTransformViewModel = morphologicalTransformViewModel)
            }




        }
    }
}


