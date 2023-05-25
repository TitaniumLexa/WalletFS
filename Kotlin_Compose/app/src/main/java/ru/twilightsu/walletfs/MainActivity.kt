package ru.twilightsu.walletfs

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.twilightsu.walletfs.entities.Account
import ru.twilightsu.walletfs.entities.CategoryTypeEnum
import ru.twilightsu.walletfs.ui.screens.AccountAddScreen
import ru.twilightsu.walletfs.ui.screens.AccountsScreen
import ru.twilightsu.walletfs.ui.screens.CategoriesScreen
import ru.twilightsu.walletfs.ui.screens.CategoryScreen
import ru.twilightsu.walletfs.ui.screens.TransactionScreen
import ru.twilightsu.walletfs.ui.screens.TransactionsScreen
import ru.twilightsu.walletfs.ui.theme.WalletFSTheme
import java.math.BigDecimal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WalletFSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //AccountAddScreen()
                    //AccountsScreen()
                    AltView()
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun HomeView(
) {
    Scaffold() { contentPadding ->
        Surface(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Hello", Modifier.padding(contentPadding))
                Text(text = "World", Modifier.padding(contentPadding))
            }
            Canvas(modifier = Modifier) {
                drawArc(color = Color.Blue, startAngle = 0f, sweepAngle = 45f, useCenter = true)
                drawArc(color = Color.Red, startAngle = 90f, sweepAngle = 45f, useCenter = true)
                drawArc(color = Color.Green, startAngle = 180f, sweepAngle = 45f, useCenter = true)
                drawArc(color = Color.Yellow, startAngle = 270f, sweepAngle = 45f, useCenter = true)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun HomeViewPreview() {
    HomeView()
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "accounts",
    setIsBackNavigation: (Boolean) -> Unit = {},
    setTopBarTitle: (String) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("accounts") {
            setIsBackNavigation(false)
            setTopBarTitle("Главная")
            AccountsScreen(
                navigateAddAccount = {  navController.navigate("account/0") },
                navigateAccount = { id -> navController.navigate("account/$id") })
        }
        composable(
            route = "account/{accountId}",
            arguments = listOf(navArgument("accountId") { type = NavType.IntType })
        ) {
            setIsBackNavigation(true)
            setTopBarTitle("Счет")
            AccountAddScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = "category/{categoryId}?categoryTypeId={categoryTypeId}",
            arguments = listOf(
                navArgument("categoryTypeId") {
                    type = NavType.IntType
                    defaultValue = CategoryTypeEnum.EXPENSE.ordinal
                },
                navArgument("categoryId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            setIsBackNavigation(true)
            setTopBarTitle("Категория")
            CategoryScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "categories?categoryTypeId={categoryTypeId}",
            arguments = listOf(navArgument("categoryTypeId") {
                type = NavType.IntType
                defaultValue = CategoryTypeEnum.EXPENSE.ordinal
            })
        ) {
            setIsBackNavigation(false)
            setTopBarTitle("Категории")
            CategoriesScreen(
                navigateCategory = { categoryId, categoryTypeId -> navController.navigate("category/$categoryId?categoryTypeId=$categoryTypeId") },
                navigateCategoryType = { categoryTypeId -> navController.navigate("categories?categoryTypeId=$categoryTypeId") }
            )
        }
        composable(
            route = "transaction/{transactionId}?categoryTypeId={categoryTypeId}",
            arguments = listOf(
                navArgument("categoryTypeId") {
                    type = NavType.IntType
                    defaultValue = CategoryTypeEnum.EXPENSE.ordinal
                },
                navArgument("transactionId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            setIsBackNavigation(true)
            setTopBarTitle("Транзакция")
            TransactionScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = "transactions?categoryTypeId={categoryTypeId}",
            arguments = listOf(navArgument("categoryTypeId") {
                type = NavType.IntType
                defaultValue = CategoryTypeEnum.EXPENSE.ordinal
            })
        ) {
            setIsBackNavigation(false)
            setTopBarTitle("Транзакции")
            TransactionsScreen(
                navigateTransaction = { transactionId, categoryTypeId -> navController.navigate("transaction/$transactionId?categoryTypeId=$categoryTypeId") },
                navigateCategoryType = { categoryTypeId -> navController.navigate("transactions?categoryTypeId=$categoryTypeId") })
        }
    }
}

@Composable
fun ExpenseIncomeTabLayout(
    index: Int,
    navigateCategoryType: (Int) -> Unit,

) {
    var tabIndexState by remember { mutableStateOf(index) }

    TabRow(selectedTabIndex = tabIndexState) {
        Tab(
            text = { Text(text = "Expense") },
            selected = tabIndexState == 0,
            onClick = {
                tabIndexState = 0
                navigateCategoryType(2)
            }
        )
        Tab(
            text = { Text(text = "Income") },
            selected = tabIndexState == 1,
            onClick = {
                tabIndexState = 1
                navigateCategoryType(1)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AltView() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    var backNavigation by remember { mutableStateOf(false) }
    var topBarTitle by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet() {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, "") },
                    label = { Text(text = "Главная") },
                    selected = false,
                    onClick = {
                        navController.navigate("accounts")
                        scope.launch { drawerState.close() }
                    })
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Menu, "") },
                    label = { Text(text = "Категории") },
                    selected = false,
                    onClick = {
                        navController.navigate("categories?categoryTypeId=2")
                        scope.launch { drawerState.close() }
                    })
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, "") },
                    label = { Text(text = "Транзакции") },
                    selected = false,
                    onClick = {
                        navController.navigate("transactions?categoryTypeId=2")
                        scope.launch { drawerState.close() }
                    })
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, "") },
                    label = { Text(text = "Бюджет") },
                    selected = false,
                    onClick = {
                        // TODO: navController.navigate("bugdet")
                        scope.launch { drawerState.close() }
                    })
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, "") },
                    label = { Text(text = "Настройки") },
                    selected = false,
                    onClick = {
                        // TODO: navController.navigate("settings")
                        scope.launch { drawerState.close() }
                    })
            }
        }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = topBarTitle) },
                    navigationIcon = {
                        if (backNavigation) {
                            IconButton(onClick = { scope.launch { navController.popBackStack() } }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                    colors = TopAppBarDefaults.smallTopAppBarColors()
                )
            }) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AppNavHost(
                    navController = navController,
                    startDestination = "accounts",
                    setIsBackNavigation = { value -> backNavigation = value },
                    setTopBarTitle = { value -> topBarTitle = value}
                )
            }
        }

    }
}