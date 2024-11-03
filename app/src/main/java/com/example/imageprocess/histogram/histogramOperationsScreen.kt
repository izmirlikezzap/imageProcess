package com.example.imageprocess.histogram

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Switch
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.imageprocess.BottomNavigationBar
import com.example.imageprocess.R
import com.example.imageprocess.colorConversions.ImageSection
import com.example.imageprocess.colorConversions.OperationSection
import com.example.imageprocess.colorConversions.SheetContent
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import com.example.imageprocess.viewModel.ImageProcessViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HistogramOperationsScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    histogramOperationsViewModel: HistogramOperationsViewModel = viewModel()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var selectedTab by remember { mutableStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var transformedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageLabel by remember { mutableStateOf("") }

    val operations = listOf(
        "Histogram Stretching", "Histogram Equalization"
    )

    var selectedOperation by remember { mutableStateOf(operations[0]) }

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
                Column {
                    TopAppBar(
                        title = { Text(text = "Histogram Equalization") },
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
                    TabRow(
                        selectedTabIndex = selectedTab,
                        backgroundColor = Color(0xFFF3F3F3),
                        contentColor = Color(0xFF6F2A2A)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Histogram Operations") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Histogram Intensity") }
                        )
                    }
                }
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
                when (selectedTab) {
                    0 -> HistogramTab(
                        selectedImageUri = selectedImageUri,
                        transformedImageBitmap = transformedImageBitmap,
                        operations = operations,
                        selectedOperation = selectedOperation,
                        onOperationSelected = { selectedOperation = it },
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
                        },
                        onApplyClick = {
                            if (selectedImageUri != null) {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    context.contentResolver,
                                    selectedImageUri
                                )
                                transformedImageBitmap = when (selectedOperation) {
                                    "Histogram Stretching" -> histogramOperationsViewModel.applyHistogramStretching(bitmap)
                                    "Histogram Equalization" -> histogramOperationsViewModel.applyHistogramEqualization(bitmap)
                                    else -> bitmap
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please select an image first",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                    1 -> AnalysisTab(
                        selectedImageUri = selectedImageUri,
                        transformedImageBitmap = transformedImageBitmap,
                        onImageClick = { launcher.launch("image/*") },
                        onLongPressSelectedImage = { /* Handle long press on selected image */ },
                        onLongPressTransformedImage = { /* Handle long press on transformed image */ },
                        onApplyFilter = { bitmap, whiteLevel, blackLevel ->
                            coroutineScope.launch {
                                transformedImageBitmap = histogramOperationsViewModel.applyTransferFunction(bitmap, whiteLevel.toInt(), blackLevel.toInt())
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistogramTab(
    selectedImageUri: Uri?,
    transformedImageBitmap: Bitmap?,
    operations: List<String>,
    selectedOperation: String,
    onOperationSelected: (String) -> Unit,
    onImageClick: () -> Unit,
    onLongPressSelectedImage: () -> Unit,
    onLongPressTransformedImage: () -> Unit,
    onApplyClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ImageSection(
                selectedImageUri = selectedImageUri,
                transformedImageBitmap = transformedImageBitmap,
                onImageClick = onImageClick,
                onLongPressSelectedImage = onLongPressSelectedImage,
                onLongPressTransformedImage = onLongPressTransformedImage
            )
        }
        item { Spacer(modifier = Modifier.height(40.dp)) }

        item {
            OperationSection(
                operations = operations,
                selectedOperation = selectedOperation,
                onOperationSelected = onOperationSelected,
                onApplyClick = onApplyClick
            )
        }

        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}





@Composable
fun AnalysisTab(
    selectedImageUri: Uri?,
    transformedImageBitmap: Bitmap?,
    onImageClick: () -> Unit,
    onLongPressSelectedImage: () -> Unit,
    onLongPressTransformedImage: () -> Unit,
    onApplyFilter: (Bitmap, Float, Float) -> Unit
) {
    val context = LocalContext.current
    var whiteRange by remember { mutableStateOf(125f) }
    var blackRange by remember { mutableStateOf(55f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ImageSection(
                selectedImageUri = selectedImageUri,
                transformedImageBitmap = transformedImageBitmap,
                onImageClick = onImageClick,
                onLongPressSelectedImage = onLongPressSelectedImage,
                onLongPressTransformedImage = onLongPressTransformedImage
            )
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }

        // White Range Slider with minimum 3-pixel difference check
        item {
            RangeSlider(
                label = "White Range",
                value = whiteRange,
                onValueChange = { newValue ->
                    if (newValue - blackRange >= 3) {
                        whiteRange = newValue
                    }
                },
                sliderColor = Color(0xFFFF8787)
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Black Range Slider with minimum 3-pixel difference check
        item {
            RangeSlider(
                label = "Black Range",
                value = blackRange,
                onValueChange = { newValue ->
                    if (whiteRange - newValue >= 3) {
                        blackRange = newValue
                    }
                },
                sliderColor = Color(0xFFA9FFD8)
            )
        }

        item {
            Button(
                onClick = {
                    if (selectedImageUri != null) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            context.contentResolver,
                            selectedImageUri
                        )

                        onApplyFilter(bitmap, whiteRange, blackRange)
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFF8B7)),
                modifier = Modifier.fillMaxWidth(0.4f)
            ) {
                Text("APPLY FILTER", fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(120.dp)) }
    }
}

@Composable
fun RangeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    sliderColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "$label ➡️ (${value.toInt()})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = sliderColor,
                activeTrackColor = sliderColor
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}