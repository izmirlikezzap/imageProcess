package com.example.imageprocess.noiseFiltering
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imageprocess.BottomNavigationBar
import com.example.imageprocess.R
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import com.example.imageprocess.viewModel.ImageProcessViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Switch

import com.example.imageprocess.colorConversions.ImageSection
import com.example.imageprocess.colorConversions.SheetContent
import com.example.imageprocess.viewModel.NoiseFilteringViewModel

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoiseFilteringScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    imageProcessViewModel: ImageProcessViewModel = viewModel(),
    noiseFilteringViewModel : NoiseFilteringViewModel = viewModel()
) {
    val context = LocalContext.current
    val filterTypes = listOf("Min", "Max", "Average", "Median")
    var selectedFilterType by remember { mutableStateOf(filterTypes[0]) }


    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var transformedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageLabel by remember { mutableStateOf("") }

    val filterTypes2 =
        listOf("Laplacian", "Sobel Gradient", "Smoothing", "Masking", "Sharpening", "Power-Law")
    var selectedFilterType2 by remember { mutableStateOf(filterTypes2[0]) }

    var isSwitchChecked by remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                Toast.makeText(context, "Image selected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerHomeScreen(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController
            )
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Noise Filtering") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch { drawerState.open() }
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
                    expanded = false,
                    onFabClick = { }
                )
            },
            floatingActionButton = {
                if (!bottomSheetState.isVisible) {
                    FloatingActionButton(
                        onClick = { launcher.launch("image/*") },
                        backgroundColor = Color(0xFFABDAFF)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_add_photo),
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        ) {
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetContent = {
                    SheetContent(sheetImageLabel, sheetImageBitmap)
                },
                sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Görüntü Bölümü
                    item {
                        ImageSection(
                            selectedImageUri = selectedImageUri,
                            transformedImageBitmap = transformedImageBitmap,
                            onImageClick = { launcher.launch("image/*") },
                            onLongPressSelectedImage = {
                                if (selectedImageUri != null) {
                                    val bitmap = MediaStore.Images.Media.getBitmap(
                                        context.contentResolver,
                                        selectedImageUri
                                    )
                                    sheetImageLabel = "Selected Image"
                                    sheetImageBitmap = bitmap
                                    coroutineScope.launch { bottomSheetState.show() }
                                }
                            },
                            onLongPressTransformedImage = {
                                if (transformedImageBitmap != null) {
                                    sheetImageLabel = "Filtered Image"
                                    sheetImageBitmap = transformedImageBitmap
                                    coroutineScope.launch { bottomSheetState.show() }
                                }
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    // Filtre Seçimi ve Uygulama Bölümü
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Filter Type",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f).padding(start = 48.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFFF8A81)
                                )

                                Switch(
                                    checked = isSwitchChecked,
                                    onCheckedChange = { isChecked ->
                                        isSwitchChecked = isChecked
                                    }
                                )
                            }


                            Spacer(modifier = Modifier.height(8.dp))

                            if (isSwitchChecked) {

                                // Switch açıkken gösterilecek alternatif işlem adımları
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Butonlar arasında boşluk eklemek için
                                ) {
                                    items(filterTypes2) { filterType ->
                                        Button(
                                            onClick = { selectedFilterType2 = filterType },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = if (selectedFilterType2 == filterType)
                                                    Color(0xFFBBE1FF) else Color.LightGray
                                            ),
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            Text(filterType)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))




                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (selectedImageUri != null) {
                                                val bitmap = MediaStore.Images.Media.getBitmap(
                                                    context.contentResolver,
                                                    selectedImageUri
                                                )

                                                transformedImageBitmap =
                                                    when (selectedFilterType2) {
                                                        "Min" -> noiseFilteringViewModel.applyMinFilter(
                                                            bitmap
                                                        )

                                                        "Max" -> noiseFilteringViewModel.applyMaxFilter(
                                                            bitmap
                                                        )

                                                        "Average" -> noiseFilteringViewModel.applyAverageFilter(
                                                            bitmap
                                                        )

                                                        "Median" -> noiseFilteringViewModel.applyMedianFilter(
                                                            bitmap
                                                        )

                                                        "Laplacian" -> noiseFilteringViewModel.applyLaplacianFilter(
                                                            bitmap
                                                        )

                                                        "Sobel Gradient" -> noiseFilteringViewModel.applySobelGradientFilter(
                                                            bitmap
                                                        )

                                                        "Smoothing" -> noiseFilteringViewModel.applySmoothingFilter(
                                                            bitmap
                                                        )

                                                        "Masking" -> noiseFilteringViewModel.applyMasking(
                                                            bitmap
                                                        )

                                                        "Sharpening" -> noiseFilteringViewModel.applySharpening(
                                                            bitmap
                                                        )

                                                        "Power-Law" -> noiseFilteringViewModel.applyPowerLawTransformation(
                                                            bitmap
                                                        )

                                                        else -> bitmap // Varsayılan durum
                                                    }

                                                Toast.makeText(
                                                    context,
                                                    "$selectedFilterType2 filter applied",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Please select an image first",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(
                                            0xFFD5FFEC
                                        )
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Apply Filter")
                                }
                            } else {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    filterTypes.forEach { filterType ->
                                        Button(
                                            onClick = { selectedFilterType = filterType },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = if (selectedFilterType == filterType)
                                                    Color(0xFFBBE1FF) else Color.LightGray
                                            ),
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            Text(filterType)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (selectedImageUri != null) {
                                                val bitmap = MediaStore.Images.Media.getBitmap(
                                                    context.contentResolver,
                                                    selectedImageUri
                                                )
                                                transformedImageBitmap =
                                                    noiseFilteringViewModel.applyNoiseFilter(
                                                        bitmap,
                                                        selectedFilterType
                                                    )
                                                Toast.makeText(
                                                    context,
                                                    "$selectedFilterType filter applied",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Please select an image first",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(
                                            0xFFD5FFEC
                                        )
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Apply Filter")
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}


