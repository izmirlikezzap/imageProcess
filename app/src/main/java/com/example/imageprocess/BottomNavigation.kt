package com.example.imageprocess

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun BottomNavigationBar(navController: NavHostController,
                        expanded: Boolean,
                        onFabClick: () -> Unit,
                        doubleBack : Boolean = false) {
    val bottomBarHeight = 80.dp
    var showFabs by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomBarHeight)
            .background(Color.White)
            .padding(start = 5.dp, end = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomBarHeight)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon_back_home),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (doubleBack) {

                                    navController.navigateUp()
                                    navController.navigateUp()
                                } else {
                                    navController.popBackStack()
                                }
                            }
                    )
                },
                label = { Text("BACK", fontWeight = FontWeight.Bold) },
                selected = false,
                onClick = { }
            )

            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon_home),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigate("homeScreen") }
                    )
                },
                label = { Text("HOME", fontWeight = FontWeight.Bold) },
                selected = false,
                onClick = {  }
            )

            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon_ai),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { navController.navigate("homeScreen") }
                    )
                },
                label = { Text("AI TOOLS", fontWeight = FontWeight.Bold) },
                selected = false,
                onClick = {  }
            )

        }

    }
}