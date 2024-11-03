package com.example.imageprocess.colorConversions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.imageprocess.BottomNavigationBar
import com.example.imageprocess.R
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import com.example.imageprocess.viewModel.ImageProcessViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.platform.LocalDensity
import processImage


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageTransformingScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    imageProcessViewModel: ImageProcessViewModel = viewModel()
) {
    Log.d("ImageTransformingScreen", "inside ImageTransformingScreen")

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var transformedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sheetImageLabel by remember { mutableStateOf("") }

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

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val imageWidth = (screenWidth - 30.dp - 32.dp) / 2
    val imageHeight = imageWidth * 0.75f
    val rowHeight = imageHeight + 32.dp

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = sheetImageLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (sheetImageBitmap != null) {
                    Image(
                        bitmap = sheetImageBitmap!!.asImageBitmap(),
                        contentDescription = sheetImageLabel,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("No image to display", textAlign = TextAlign.Center)
                }
            }
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
                        title = { Text(text = "Image Transforming Screen") },
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
                },
                floatingActionButton = {
                    if (!bottomSheetState.isVisible) { // FAB sadece BottomSheet kapalıyken görünür
                        FloatingActionButton(
                            onClick = {
                                launcher.launch("image/*") // Galeriyi açan kod
                            },
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
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            // Selected Image Column
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        // Single tap: Image picker açılıyor
                                        launcher.launch("image/*")
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                // Long press: selected image sheet'te gösteriliyor
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
                                            }
                                        )
                                    }
                            ) {
                                Text(
                                    "SELECTED IMAGE",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(7.dp))
                                if (selectedImageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = "Selected Image",
                                        modifier = Modifier.size(imageWidth, imageHeight)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_image_placeholder),
                                        contentDescription = "Placeholder Image",
                                        modifier = Modifier.size(imageWidth, imageHeight)
                                    )
                                }
                            }

                            // Transformed Image Column
                            if (selectedImageUri != null) {
                                Spacer(modifier = Modifier.width(15.dp))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {
                                                    // Long press: transformed image sheet'te gösteriliyor
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
                                ) {
                                    Text(
                                        "TRANSFORMED IMAGE",
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(7.dp))
                                    if (transformedImageBitmap != null) {
                                        Image(
                                            bitmap = transformedImageBitmap!!.asImageBitmap(),
                                            contentDescription = "Transformed Image",
                                            modifier = Modifier.size(imageWidth, imageHeight)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(imageWidth, imageHeight)
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("No transformed image yet", textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(35.dp))
                    }

                    item {
                        HeightAndWidthPicker(viewModel = imageProcessViewModel)
                    }

                    item {
                        Button(onClick = {
                            coroutineScope.launch {
                                if (selectedImageUri != null) {
                                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImageUri)
                                    transformedImageBitmap = processImage(
                                        bitmap,
                                        Pair(imageProcessViewModel.width.value, imageProcessViewModel.height.value)
                                    )
                                    Toast.makeText(context, "Image processed", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Text(text = "Apply")
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(115.dp))
                    }
                }
            }
        }
    }
}





@Composable
fun HeightAndWidthPicker(
    viewModel: ImageProcessViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Width Picker",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            WheelPicker(
                items = (1..99).map { it.toString() }, // 1'den 99'a kadar sayılar
//                modifier = Modifier.height(150.dp), // Genişlik ayarı aynı yüksekliği kullanıyor
                initialSelectedIndex = 3,
                onSelectionChanged = { index, item ->
                    // Seçim değiştiğinde viewModel'e width güncellemesi
                    viewModel.updateDimensions(newHeight = viewModel.height.value, newWidth = item.toInt())
                    println("Selected width: $item at index $index")
                },
                lineColor = Color(0xFF7AFF80)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Column(
            modifier = Modifier.weight(1f), // Eşit genişlik
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Height Picker",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            WheelPicker(
                items = (1..99).map { it.toString() }, // 1'den 99'a kadar sayılar
//                modifier = Modifier.height(150.dp), // Yükseklik ayarı
                initialSelectedIndex = 3,
                onSelectionChanged = { index, item ->
                    // Seçim değiştiğinde viewModel'e height güncellemesi
                    viewModel.updateDimensions(newHeight = item.toInt(), newWidth = viewModel.width.value)
                    println("Selected height: $item at index $index")
                },
                lineColor = Color(0xFFEE9A9A)
            )
        }


    }
}




@Composable
fun WheelPicker(
    items: List<String>,
    modifier: Modifier = Modifier,
    initialSelectedIndex: Int = 0,
    onSelectionChanged: (Int, String) -> Unit,
    lineColor: Color = Color.Green
) {
    val itemHeight = 40.dp
    val visibleItems = 3
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val itemHeightPx = with(density) { itemHeight.toPx() }

    var selectedIndex by remember { mutableStateOf(initialSelectedIndex) }

    LaunchedEffect(Unit) {
        listState.scrollToItem(maxOf(initialSelectedIndex - 1, 0))
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val centralIndex = when {
                    offset > itemHeightPx / 2 -> index + 1
                    else -> index
                }
                if (centralIndex != selectedIndex && centralIndex in items.indices) {
                    selectedIndex = centralIndex
                    onSelectionChanged(centralIndex, items[centralIndex])
                }
            }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItems)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            itemsIndexed(items) { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = 15.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Selection indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.Center)
                .drawWithContent {
                    drawContent()
                    // Üst çizgi
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = with(density) { 1.5.dp.toPx() }
                    )
                    // Alt çizgi
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = with(density) { 1.5.dp.toPx() }
                    )
                }
        )
    }
}