package com.example.imageprocess.bottomSheets

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.imageprocess.BottomNavigationBar
import com.example.imageprocess.R
import com.example.imageprocess.bottomSheets.TransformInfoBottomSheet
import com.example.imageprocess.drawer.ModalDrawerHomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TransformInfoBottomSheet() {
    val methodList = listOf("zero", "random", "keep")
    val paddingModeList = listOf("constant", "reflect", "symmetric", "edge", "wrap")

    // Method explanations
    val methodDescriptions = mapOf(
        "zero" to "Pads the image with zeros.",
        "random" to "Pads the image with random values.",
        "keep" to "No changes made to the image."
    )

    // Padding mode explanations
    val paddingModeDescriptions = mapOf(
        "constant" to "Pads the image with a constant value.",
        "reflect" to "Pads the image by reflecting pixel values.",
        "symmetric" to "Pads with symmetric reflections.",
        "edge" to "Pads using the edge values.",
        "wrap" to "Pads by wrapping the image."
    )

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Method Section
        item {
            Text(text = "Method", fontSize = 16.sp, color = Color(0xFFF36E6E), modifier = Modifier.padding(bottom = 8.dp), fontWeight = FontWeight.Bold)
        }
        items(methodList) { method ->
            ExplanationRow(
                imageResId = R.drawable.icon_method,
                title = method,
                description = methodDescriptions[method] ?: ""
            )
        }
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }



        // Padding Mode Section
        item {
            Text(text = "Padding Mode", fontSize = 16.sp, color = Color(0xFF475AC7), modifier = Modifier.padding(bottom = 8.dp), fontWeight = FontWeight.Bold)
        }
        items(paddingModeList) { mode ->
            ExplanationRow(
                imageResId = R.drawable.icon_method,
                title = mode,
                description = paddingModeDescriptions[mode] ?: ""
            )
        }
        item{
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ExplanationRow(
    imageResId: Int,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image on the left
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.size(40.dp).padding(end = 8.dp)
        )

        // Text on the right
        Column {
            Text(text = title, fontSize = 16.sp, color = Color.Black, overflow = TextOverflow.Ellipsis)
            Text(text = description, fontSize = 14.sp, color = Color.Gray, overflow = TextOverflow.Ellipsis)
        }
    }
}
