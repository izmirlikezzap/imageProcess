package com.example.imageprocess.imageRestoration

import ImageRestorationViewModel
import com.example.imageprocess.filterAndTransform.ImageTransformViewModel
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
fun ImageRestorationScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    imageRestorationViewModel: ImageRestorationViewModel
) {
    val context = LocalContext.current

    val filterTypes2 =
        listOf("Inverse Filter", "Wiener Filter", "Least Squares Filter", "Geometric Mean Filter", "Arithmetic Mean Filter")
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
                    title = { Text(text = "Image Restoration & Construction") },
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
                                                    "Inverse Filter" -> imageRestorationViewModel.applyInverseFilter(bitmap)
                                                    "Wiener Filter" -> imageRestorationViewModel.applyWeinerFilter(bitmap)
                                                    "Least Squares Filter" -> imageRestorationViewModel.applyLeastSquaresFilter(bitmap)
                                                    "Geometric Mean Filter" -> imageRestorationViewModel.applyGeometricMeanFilter(bitmap)
                                                    "Arithmetic Mean Filter" -> imageRestorationViewModel.applyArithmeticMeanFilter(bitmap)

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