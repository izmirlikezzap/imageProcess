package com.example.imageprocess.morphologicalTransform

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.imageprocess.BottomNavigationBar
import com.example.imageprocess.R
import com.example.imageprocess.colorConversions.ImageSection
import com.example.imageprocess.colorConversions.SheetContent
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MorphologicalTransformScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    morphologicalTransformViewModel: MorphologicalTransformViewModel
) {
    val context = LocalContext.current
    val filterTypes = listOf("Dilation", "Erosion", "Dilation + Erosion", "Erosion + Dilation")
    var selectedFilter by remember { mutableStateOf(filterTypes[0]) }

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

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerHomeScreen(drawerState = drawerState, coroutineScope = coroutineScope, navController = navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Morphological Transformations") },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
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
                        backgroundColor = Color(0xFF65ECAF)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_add_photo),
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(60.dp)
                        )
                    }
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
                                        Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onLongPressTransformedImage = {
                                    if (transformedImageBitmap != null) {
                                        sheetImageLabel = "Transformed Image"
                                        sheetImageBitmap = transformedImageBitmap
                                        coroutineScope.launch { bottomSheetState.show() }
                                    } else {
                                        Toast.makeText(context, "No transformed image available", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(20.dp)) }

                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filterTypes) { filterType ->
                                    Button(
                                        onClick = { selectedFilter = filterType },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = if (selectedFilter == filterType)
                                                Color(0xFFBBE1FF) else Color.LightGray
                                        ),
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text(filterType)
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(20.dp)) }

                        item {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        if (selectedImageUri != null) {
                                            val bitmap = MediaStore.Images.Media.getBitmap(
                                                context.contentResolver,
                                                selectedImageUri
                                            )
                                            transformedImageBitmap = when (selectedFilter) {
                                                "Dilation" -> morphologicalTransformViewModel.applyDilation(bitmap)
                                                "Erosion" -> morphologicalTransformViewModel.applyErosion(bitmap)
                                                "Dilation + Erosion" -> morphologicalTransformViewModel.applyDilationThenErosion(bitmap)
                                                "Erosion + Dilation" -> morphologicalTransformViewModel.applyErosionThenDilation(bitmap)
                                                else -> bitmap
                                            }
                                            Toast.makeText(context, "$selectedFilter applied", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Please select an image first", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD5FFEC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Apply Transformation")
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(200.dp).fillMaxWidth())
                        }
                    }
                }
            }
        )
    }
}