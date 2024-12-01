package com.example.imageprocess.noiseAdditionAndRemoving

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
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
import androidx.navigation.NavHostController
import com.example.imageprocess.BottomNavigationBar
import com.example.imageprocess.LineSlider
import com.example.imageprocess.R
import com.example.imageprocess.colorConversions.ImageSection
import com.example.imageprocess.colorConversions.SheetContent
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "DefaultLocale")
@Composable
fun NoiseAdditionAndRemoving(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    noiseAddtionAndRemovingViewModel: NoiseAddtionAndRemovingViewModel = viewModel()
) {
    val context = LocalContext.current
    val filterTypes = listOf("Salt Noise", "Pepper Noise", "Gaussian Noise", "Uniform Noise")
    var selectedFilterType by remember { mutableStateOf(filterTypes[0]) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var transformedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageLabel by remember { mutableStateOf("") }

    val filterTypes2 = listOf(
        "Motion Deblurring",
        "Median Filter",
        "Gaussian Filter",
        "Min Filter",
        "Max Filter",
        "Average Filter",

    )
    var selectedFilterType2 by remember { mutableStateOf(filterTypes2[0]) }

    var isSwitchChecked by remember { mutableStateOf(false) }
    var noiseLevel by remember { mutableFloatStateOf(.4f) }

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

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

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
                    // Image Section
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

                    // Filter Selection and Application Section
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Switch for Noise Addition/Removal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isSwitchChecked) "Noise Addition" else "Remove Noise",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 48.dp),
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

                            // Noise Addition Section
                            if (isSwitchChecked) {
                                // Noise Types Row
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filterTypes) { filterType ->
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

                                // Noise Level Slider
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 15.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Noise Level: ${(noiseLevel * 10).toInt()}%")


                                    LineSlider(
                                        value = noiseLevel,
                                        onValueChange = {
                                            noiseLevel = String.format("%.2f", it).toFloat()
                                        },
                                        modifier = Modifier

                                            .widthIn(max = 400.dp),
                                        steps = 20,
                                        thumbDisplay = { ( it*10 ).toInt().toString()}
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Apply Noise Button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (selectedImageUri != null) {
                                                val bitmap = MediaStore.Images.Media.getBitmap(
                                                    context.contentResolver,
                                                    selectedImageUri
                                                )
                                                Log.d("NOISE LEVEL","$noiseLevel")
                                                transformedImageBitmap = when (selectedFilterType) {
                                                    "Salt Noise" -> noiseAddtionAndRemovingViewModel.addSaltNoise(bitmap, noiseLevel.toDouble()*10)
                                                    "Pepper Noise" -> noiseAddtionAndRemovingViewModel.addPepperNoise(bitmap, noiseLevel.toDouble()*10)
                                                    "Gaussian Noise" -> noiseAddtionAndRemovingViewModel.addGaussianNoise(bitmap, noiseLevel.toDouble()*10)
                                                    "Uniform Noise" -> noiseAddtionAndRemovingViewModel.addUniformNoise(bitmap, noiseLevel.toDouble()*10)
                                                    else -> bitmap
                                                }

                                                Toast.makeText(
                                                    context,
                                                    "$selectedFilterType applied",
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
                                        backgroundColor = Color(0xFFD5FFEC)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Apply Noise")
                                }
                            }
                            // Noise Removal Section
                            else {
                                // Noise Removal Types Row
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                                if (selectedFilterType2 == "Motion Deblurring") {
                                                    // Show a toast message for Motion Deblurring
                                                    Toast.makeText(
                                                        context,
                                                        "BENDEN NE KÖY OLUR NE DE KASABA ",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    // Process other filters
                                                    val bitmap = MediaStore.Images.Media.getBitmap(
                                                        context.contentResolver,
                                                        selectedImageUri
                                                    )

                                                    transformedImageBitmap = when (selectedFilterType2) {
                                                        "Median Filter" -> noiseAddtionAndRemovingViewModel.applyMedianFilter(bitmap)
                                                        "Gaussian Filter" -> noiseAddtionAndRemovingViewModel.applyGaussianFilter(bitmap)
                                                        "Min Filter" -> noiseAddtionAndRemovingViewModel.applyMinFilter(bitmap)
                                                        "Max Filter" -> noiseAddtionAndRemovingViewModel.applyMaxFilter(bitmap)
                                                        "Average Filter" -> noiseAddtionAndRemovingViewModel.applyAverageFilter(bitmap)
                                                        else -> bitmap
                                                    }

                                                    Toast.makeText(
                                                        context,
                                                        "$selectedFilterType2 applied",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
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
                                        backgroundColor = Color(0xFFD5FFEC)
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