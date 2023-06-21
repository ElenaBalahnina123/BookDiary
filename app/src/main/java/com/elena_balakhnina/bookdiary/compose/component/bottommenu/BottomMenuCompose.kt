package com.elena_balakhnina.bookdiary.compose.component.bottommenu

import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomMenuCompose(navController: NavController) {
    val bottomMenuItemsList = prepareBottomMenu()

    BottomNavigation() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomMenuItemsList.forEach { menuItem ->
            BottomNavigationItem(
                selected = currentRoute == menuItem.screen_route,
                unselectedContentColor = Color.White.copy(0.6f),
                onClick = {
                    navController.navigate(menuItem.screen_route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = menuItem.icon),
                        modifier = Modifier.width(30.dp),
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = menuItem.title, fontSize = 12.sp)
                },

                )
        }
    }
}

private fun prepareBottomMenu(): List<BottomNavItem> {
    val bottomMenuItemsList = arrayListOf<BottomNavItem>()

    bottomMenuItemsList.add(BottomNavItem.Books)
    bottomMenuItemsList.add(BottomNavItem.Planned)
    bottomMenuItemsList.add(BottomNavItem.Favorite)

    return bottomMenuItemsList
}


