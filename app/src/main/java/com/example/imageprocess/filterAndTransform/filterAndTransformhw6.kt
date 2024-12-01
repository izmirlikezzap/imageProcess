package com.example.imageprocess.filterAndTransform

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DrawerState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun FilterAndTransformScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    imageTransformViewModel: ImageTransformViewModel
) {
    val context = LocalContext.current

    val filterTypes2 =
        listOf("High-Pass Filter", "Thresholding", "Spectrum", "Butterworth", "Butterworth Filter", "Inverse Fourier Transform")
    var selectedFilterAndTransformType by remember { mutableStateOf(filterTypes2[0]) }

    // Dönüştürme işlemi için ViewModel çağrısı
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
                    title = { Text(text = "Filter & Transform") },
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


                        item{

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filterTypes2) { filterType ->
                                    Button(
                                        onClick = { selectedFilterAndTransformType = filterType },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = if (selectedFilterAndTransformType == filterType)
                                                Color(0xFFBBE1FF) else Color.LightGray
                                        ),
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text(filterType)
                                    }
                                }
                            }
                        }






                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        item{
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        if (selectedImageUri != null) {
                                            val bitmap = MediaStore.Images.Media.getBitmap(
                                                context.contentResolver,
                                                selectedImageUri
                                            )

                                            transformedImageBitmap =
                                                when (selectedFilterAndTransformType) {
                                                    "High-Pass Filter" -> imageTransformViewModel.applyHighPassFilter(bitmap)
                                                    "Thresholding" -> imageTransformViewModel.applyThresholding(bitmap, 128)
                                                    "Butterworth" -> imageTransformViewModel.applyButterworthFilter(bitmap, cutoffFrequency = 50, order = 2)
                                                    "Inverse Fourier Transform" -> imageTransformViewModel.applyInverseFourierTransform(bitmap)
                                                    else -> bitmap
                                                }


                                            Toast.makeText(
                                                context,
                                                "$selectedFilterAndTransformType filter applied",
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