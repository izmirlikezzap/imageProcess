package com.example.imageprocess.colorConversions

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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.imageprocess.BottomNavigationBar
import com.example.imageprocess.R
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import com.example.imageprocess.viewModel.ImageProcessViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.input.pointer.pointerInput


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ColorConversionScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    imageProcessViewModel: ImageProcessViewModel
) {
    val context = LocalContext.current

    // Dropdown için seçenekler (RGB -> Diğer renk uzayları)
    val colorSpaces = listOf("HSI", "HSV", "LAB", "YIQ")
    val operations = listOf(
        "Reflection (Vertical)",
        "Reflection (Horizontal)",
        "Reflection (Both)",
        "Resize",
        "Crop",
        "Shifting"
    )

    // Dropdown seçimleri için durumlar
    var selectedOperation by remember { mutableStateOf(operations[2]) }
    var fromColorSpace by remember { mutableStateOf("RGB") }
    var toColorSpace by remember { mutableStateOf(colorSpaces[0]) }

    // Dönüştürme işlemi için ViewModel çağrısı
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var transformedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageLabel by remember { mutableStateOf("") }

    var startX by remember { mutableStateOf("") }
    var endX by remember { mutableStateOf("") }
    var startY by remember { mutableStateOf("") }
    var endY by remember { mutableStateOf("") }

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

    // Bottom sheet state for displaying images
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerHomeScreen(drawerState = drawerState, coroutineScope = coroutineScope, navController = navController)
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Color Conversion") },
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
                        backgroundColor = Color(0xFF65ECAF),
                        content = {
                            Image(
                                painter = painterResource(id = R.drawable.icon_add_photo),
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    )
                }
            },
            content = {
                ModalBottomSheetLayout(
                    sheetState = bottomSheetState,
                    sheetContent = {
                        SheetContent(sheetImageLabel, sheetImageBitmap)
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
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
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "No image selected",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onLongPressTransformedImage = {
                                    if (transformedImageBitmap != null) {
                                        sheetImageLabel = "Transformed Image"
                                        sheetImageBitmap = transformedImageBitmap
                                        coroutineScope.launch { bottomSheetState.show() }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "No transformed image available",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }


                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        item {
                            ConversionSection(
                                colorSpaces = colorSpaces,
                                toColorSpace = toColorSpace,
                                onColorSelected = { toColorSpace = it },
                                onConvertClick = {
                                    coroutineScope.launch {
                                        if (selectedImageUri != null) {
                                            val bitmap = MediaStore.Images.Media.getBitmap(
                                                context.contentResolver,
                                                selectedImageUri
                                            )
                                            val transformedBitmap =
                                                imageProcessViewModel.convertColorSpace(
                                                    bitmap,
                                                    fromColorSpace,
                                                    toColorSpace
                                                )
                                            transformedImageBitmap = transformedBitmap
                                            Toast.makeText(
                                                context,
                                                "Image converted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "No image selected",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(50.dp))
                        }

                        item {
                            OperationSection(
                                operations = operations,
                                selectedOperation = selectedOperation,
                                onOperationSelected = { selectedOperation = it },
                                onApplyClick = {
                                    coroutineScope.launch {
                                        if (selectedImageUri != null) {
                                            val bitmap = MediaStore.Images.Media.getBitmap(
                                                context.contentResolver,
                                                selectedImageUri
                                            )

                                            val startXInt = startX.toIntOrNull() ?: 0
                                            val endXInt = endX.toIntOrNull() ?: bitmap.width
                                            val startYInt = startY.toIntOrNull() ?: 0
                                            val endYInt = endY.toIntOrNull() ?: bitmap.height

                                            when (selectedOperation) {
                                                "Resize" -> {
                                                    val resizedBitmap =
                                                        imageProcessViewModel.applyResize(
                                                            bitmap,
                                                            startXInt,
                                                            endXInt,
                                                            startYInt,
                                                            endYInt
                                                        )
                                                    transformedImageBitmap = resizedBitmap
                                                    Toast.makeText(
                                                        context,
                                                        "Image resized",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                "Crop" -> {
                                                    val croppedBitmap =
                                                        imageProcessViewModel.applyCrop(
                                                            bitmap,
                                                            startXInt,
                                                            endXInt,
                                                            startYInt,
                                                            endYInt
                                                        )
                                                    transformedImageBitmap = croppedBitmap
                                                    Toast.makeText(
                                                        context,
                                                        "Image cropped",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                "Shifting" -> {
                                                    val shiftedBitmap =
                                                        imageProcessViewModel.applyShifting(
                                                            bitmap,
                                                            startXInt,
                                                            endXInt,
                                                            startYInt,
                                                            endYInt
                                                        )
                                                    transformedImageBitmap = shiftedBitmap
                                                    Toast.makeText(
                                                        context,
                                                        "Image shifted",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                else -> {
                                                    Toast.makeText(
                                                        context,
                                                        "Operation not implemented",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "No image selected",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(35.dp))
                        }
                        item{
                            Text(
                                "Crop Operation:",
                                modifier = Modifier.padding(bottom = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {


                            CropInputFields(
                                startX = startX,
                                endX = endX,
                                startY = startY,
                                endY = endY,
                                onStartXChange = { startX = it },
                                onEndXChange = { endX = it },
                                onStartYChange = { startY = it },
                                onEndYChange = { endY = it }
                            )
                        }

                        item {
                            Spacer(
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SheetContent(sheetImageLabel: String, sheetImageBitmap: Bitmap?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 6.dp, top = 16.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = sheetImageLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (sheetImageBitmap != null) {
            Image(
                bitmap = sheetImageBitmap.asImageBitmap(),
                contentDescription = sheetImageLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(sheetImageBitmap.width.toFloat() / sheetImageBitmap.height)
            )
        } else {
            Text("No image to display", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ImageSection(
    selectedImageUri: Uri?,
    transformedImageBitmap: Bitmap?,
    onImageClick: () -> Unit,
    onLongPressSelectedImage: () -> Unit,
    onLongPressTransformedImage: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (transformedImageBitmap == null) Arrangement.Center else Arrangement.SpaceEvenly
    ) {
        ImageColumn(
            label = "SELECTED IMAGE",
            imageUri = selectedImageUri,
            onClick = onImageClick,
            onLongPress = onLongPressSelectedImage
        )

        if (transformedImageBitmap != null) {
            ImageColumn(
                label = "TRANSFORMED IMAGE",
                imageBitmap = transformedImageBitmap,
                onClick = onImageClick,
                onLongPress = onLongPressTransformedImage
            )
        }
    }
}


@Composable
fun ImageColumn(
    label: String,
    imageUri: Uri? = null,
    imageBitmap: Bitmap? = null,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            }
    ) {
        Text(
            label,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(7.dp))
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = label,
                modifier = Modifier.size(150.dp)
            )
        } else if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = label,
                modifier = Modifier.size(150.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.icon_image_placeholder),
                contentDescription = "Placeholder Image",
                modifier = Modifier.size(150.dp)
            )
        }
    }
}

@Composable
fun ConversionSection(
    colorSpaces: List<String>,
    toColorSpace: String,
    onColorSelected: (String) -> Unit,
    onConvertClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Convert RGB to:",
                modifier = Modifier.padding(bottom = 4.dp),
                fontWeight = FontWeight.Bold
            )
            DropdownMenu(
                colorSpaces = colorSpaces,
                selectedOption = toColorSpace,
                onColorSelected = onColorSelected
            )
        }

        Button(
            onClick = onConvertClick,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 22.dp)
        ) {
            Text("Convert")
        }
    }
}

@Composable
fun OperationSection(
    operations: List<String>,
    selectedOperation: String,
    onOperationSelected: (String) -> Unit,
    onApplyClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Select Operation:",
                modifier = Modifier.padding(bottom = 4.dp),
                fontWeight = FontWeight.Bold
            )
            WheelPicker(
                items = operations,
                initialSelectedIndex = operations.indexOf(selectedOperation),
                onSelectionChanged = { _, item -> onOperationSelected(item) },
                modifier = Modifier.height(120.dp)
            )
        }

        Button(
            onClick = onApplyClick,
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(start = 16.dp, bottom = 36.dp)
        ) {
            Text("Apply")
        }
    }
}

@Composable
fun CropInputFields(
    startX: String,
    endX: String,
    startY: String,
    endY: String,
    onStartXChange: (String) -> Unit,
    onEndXChange: (String) -> Unit,
    onStartYChange: (String) -> Unit,
    onEndYChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = startX,
            onValueChange = onStartXChange,
            label = { Text("Start X") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        OutlinedTextField(
            value = endX,
            onValueChange = onEndXChange,
            label = { Text("End X") },
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = startY,
            onValueChange = onStartYChange,
            label = { Text("Start Y") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        OutlinedTextField(
            value = endY,
            onValueChange = onEndYChange,
            label = { Text("End Y") },
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}


@Composable
fun DropdownMenu(
    colorSpaces: List<String>,
    selectedOption: String,
    onColorSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Button(
        modifier = Modifier.width(200.dp),
        onClick = { expanded = true },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF7FAF8))
    ) {
        Text(selectedOption)
    }
    androidx.compose.material.DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        colorSpaces.forEach { colorSpace ->
            androidx.compose.material.DropdownMenuItem(
                onClick = {
                    onColorSelected(colorSpace)
                    expanded = false
                }
            ) {
                Text(text = colorSpace)
            }
        }
    }
}