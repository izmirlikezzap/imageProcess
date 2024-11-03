package com.example.imageprocess

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.DrawerState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.imageprocess.viewModel.ImageProcessViewModel
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    imageProcessViewModel: ImageProcessViewModel = viewModel()
) {

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {

        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerHomeScreen(drawerState = drawerState, coroutineScope = coroutineScope, navController = navController)
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Home Screen") },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        backgroundColor = Color(0xFFF3F3F3)
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        expanded = expanded,
                        onFabClick = { expanded = !expanded }
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    item{
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp))
                    }



                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp) // Arada 10.dp boşluk
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.icon_ikc_ai),
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                    }

                    item{
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp))
                    }
                    item{
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CustomCard(
                                    title = "Color Conversions",
                                    description = "Apply color conversions techniques.",
                                    image = R.drawable.icon_color_conversion,
                                    borderColor = Color(0xFFFFF282),
                                    modifier = Modifier.weight(1f),
                                    callBack = { navController.navigate("colorConversionScreen") }
                                )

                                CustomCard(
                                    title = "Image Noise Filtering",
                                    description = "Apply noise filtering techniques.",
                                    image = R.drawable.icon_noise_filtering,
                                    borderColor = Color(0xFF8699FF),
                                    modifier = Modifier.weight(1f),
                                    callBack = { navController.navigate("noiseFilteringScreen") }
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(25.dp))
                    }
                    item{
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CustomCard(
                                    title = "Histogram Equalization",
                                    description = "Apply histogram equalization techniques.",
                                    image = R.drawable.icon_histogram,
                                    borderColor = Color(0xFFFF9800),
                                    modifier = Modifier.weight(0.5f),
                                    callBack = { navController.navigate("histogramOperationsScreen") }
                                )


                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(25.dp))
                    }

                    item {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.LightGray)) {
                                    append("designed by ")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color(
                                    0xFF404140
                                )
                                )) {
                                    append("Mustafa Melik Ayanoğlu")
                                }
                            },
                            fontSize = 16.sp
                        )
                    }



                    item {
                        Spacer(modifier = Modifier.height(115.dp))
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CustomCard(
    title: String,
    description: String,
    image: Int,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Gray,
    modifier: Modifier = Modifier,
    callBack: () -> Unit
) {
    var titleFontSize by remember { mutableStateOf(12.sp) }

    Card(
        modifier = modifier
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { callBack() },
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.hasVisualOverflow && titleFontSize > 8.sp) {
                        titleFontSize = titleFontSize * 0.9f
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
