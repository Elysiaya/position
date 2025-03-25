package com.example.position
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.position.ui.model.GnssViewModel
import com.example.position.ui.screens.DOYScreen
import com.example.position.ui.screens.NavigationScreen
import com.example.position.ui.screens.PositionScreen
import com.example.position.ui.screens.SatelliteScreen
import com.example.position.ui.theme.PositionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //请求运行时权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        // 获取 GnssViewModel
        val gnssViewModel: GnssViewModel by viewModels(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return GnssViewModel(applicationContext) as T
                    }
                }
            }
        )

        setContent {
            PositionTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentDestination?.route
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigation(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    })
                { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.PositionScreen.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.PositionScreen.route) { PositionScreen(viewModel = gnssViewModel) }
                        composable(Screen.NavigationScreen.route) { NavigationScreen() }
                        composable(Screen.SatelliteScreen.route) { SatelliteScreen(viewModel = gnssViewModel) }
                        composable(Screen.DOYScreen.route) { DOYScreen()  }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigation(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "Position"
                )
            },
            selected = currentRoute == Screen.PositionScreen.route,
            onClick = {
                navController.navigate(Screen.PositionScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "Satellite"
                )
            },
            selected = currentRoute == Screen.PositionScreen.route,
            onClick = {
                navController.navigate(Screen.SatelliteScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "Navigation"
                )
            },
            selected = currentRoute == Screen.PositionScreen.route,
            onClick = {
                navController.navigate(Screen.NavigationScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "DOY"
                )
            },
            selected = currentRoute == Screen.DOYScreen.route,
            onClick = {
                navController.navigate(Screen.DOYScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PositionTheme {
        val navController = rememberNavController()
        val currentRoute = navController.currentDestination?.route
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomNavigation(
                    navController = navController,
                    currentRoute = currentRoute
                )
            })
        { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.PositionScreen.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.PositionScreen.route) { PositionScreen() }
                composable(Screen.NavigationScreen.route) { NavigationScreen() }
                composable(Screen.SatelliteScreen.route) { SatelliteScreen() }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object PositionScreen : Screen("Position")
    data object SatelliteScreen : Screen("Satellite")
    data object NavigationScreen : Screen("Navigation")
    data object DOYScreen : Screen("DOY")
}