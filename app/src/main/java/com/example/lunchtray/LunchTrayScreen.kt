/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lunchtray.ui.OrderViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.stringResource



enum class LunchTrayScreen(@StringRes val title: Int) {
    Start(R.string.start_order),
    Entree(R.string.choose_entree),
    SideDish(R.string.choose_side_dish),
    Accompaniment(R.string.choose_accompaniment),
    Checkout(R.string.order_checkout)
}

@Composable
fun LunchTrayAppBar(
    @StringRes currentScreen: LunchTrayScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        modifier = modifier
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp() {
    val navController: NavHostController = rememberNavController()
    val viewModel: OrderViewModel = viewModel()

    val currentScreen = navController.currentBackStackEntryFlow.collectAsState(
        initial = navController.currentBackStackEntry
    ).value?.destination?.route?.let {
        LunchTrayScreen.valueOf(it)
    } ?: LunchTrayScreen.Start

    Scaffold(
        topBar = {
            LunchTrayAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
    navController = navController,
    startDestination = LunchTrayScreen.Start.name,
    modifier = Modifier.padding(innerPadding)
) {
    composable(LunchTrayScreen.Start.name) {
        StartOrderScreen(
            onNext = { navController.navigate(LunchTrayScreen.Entree.name) }
        )
    }
    composable(LunchTrayScreen.Entree.name) {
        EntreeMenuScreen(
            onNext = { navController.navigate(LunchTrayScreen.SideDish.name) },
            onCancel = {
                viewModel.resetOrder()
                navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
            }
        )
    }
    composable(LunchTrayScreen.SideDish.name) {
        SideDishMenuScreen(
            onNext = { navController.navigate(LunchTrayScreen.Accompaniment.name) },
            onCancel = {
                viewModel.resetOrder()
                navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
            }
        )
    }
    composable(LunchTrayScreen.Accompaniment.name) {
        AccompanimentMenuScreen(
            onNext = { navController.navigate(LunchTrayScreen.Checkout.name) },
            onCancel = {
                viewModel.resetOrder()
                navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
            }
        )
    }
    composable(LunchTrayScreen.Checkout.name) {
        CheckoutScreen(
            onSubmit = {
                viewModel.resetOrder()
                navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
            },
            onCancel = {
                viewModel.resetOrder()
                navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
            }
        )
    }
}

    }
}
