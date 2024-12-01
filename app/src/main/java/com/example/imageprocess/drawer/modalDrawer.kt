package com.example.imageprocess.drawer


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DrawerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imageprocess.R
import com.example.imageprocess.profile.ProfilePersonalInfoStatic


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ModalDrawerHomeScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image at the bottom
        Image(
            painter = painterResource(id = R.drawable.profilebkg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth() // Fill the width of the Box
                .align(Alignment.BottomCenter) // Align to the bottom center of the Box
        )


        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            ProfilePersonalInfoStatic()

            OneDrawe("Profile", R.drawable.icon_profile_screen) {
                coroutineScope.launch {
                    drawerState.close()
                    navController.navigate("")
                }
            }

            OneDrawe("Color Conversions", R.drawable.icon_color_conversion) {
                coroutineScope.launch {
                    drawerState.close()
                    navController.navigate("colorConversionScreen")
                }
            }

            OneDrawe("Image Noise Filtering", R.drawable.icon_noise_filtering) {
                coroutineScope.launch {
                    drawerState.close()
                    navController.navigate("noiseFilteringScreen")
                }
            }
            OneDrawe("Histogram Operations", R.drawable.icon_histogram) {
                coroutineScope.launch {
                    drawerState.close()
                    navController.navigate("histogramOperationsScreen")
                }
            }


            OneDrawe("Image Filtering & Transform", R.drawable.icon_fourier) {
                coroutineScope.launch {
                    drawerState.close()
                    navController.navigate("filterAndTransformScreen")
                }
            }


            OneDrawe("Noise Addition & Removing", R.drawable.icon_image_noise_addition_removing) {
                coroutineScope.launch {
                    drawerState.close()
                    navController.navigate("noiseAdditionRemoving")
                }
            }


            OneDrawe("Image Restoration & Construction", R.drawable.icon_image_restoration) {
                coroutineScope.launch {
                    drawerState.close()
                    navController.navigate("imageRestorationConstruction")
                }
            }





            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun OneDrawe(singleText: String, int: Int, onClick: () -> Unit = {}) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(start = 20.dp, top = 20.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        val maxWidth = maxWidth
        val textSize = when {
            maxWidth < 200.dp -> 14.sp
            maxWidth < 300.dp -> 16.sp
            else -> 20.sp
        }
        val iconSize = when {
            maxWidth < 200.dp -> 24.dp
            maxWidth < 300.dp -> 28.dp
            else -> 30.dp
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = int),
                contentDescription = null,
                Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = singleText,
                fontSize = textSize,
                color = Color.Black,
                maxLines = 1
            )
        }
    }
}

