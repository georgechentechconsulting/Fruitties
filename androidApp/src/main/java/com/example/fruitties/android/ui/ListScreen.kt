/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.fruitties.android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fruitties.android.R
import com.example.fruitties.android.di.App
import com.example.fruitties.database.CartItemDetails
import com.example.fruitties.model.Fruittie
import com.example.fruitties.viewmodel.MainViewModel

@Composable
fun ListScreen() {
    // Instantiate a ViewModel with a dependency on the AppContainer.
    // To make ViewModel compatible with KMP, the ViewModel factory must
    // create an instance without referencing the Android Application.
    // Here we put the KMP-compatible AppContainer into the extras
    // so it can be passed to the ViewModel factory.
    val app = LocalContext.current.applicationContext as App
    val extras = remember(app) {
        MutableCreationExtras().apply {
            set(MainViewModel.APP_CONTAINER_KEY, app.container)
        }
    }
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory,
        extras = extras,
    )

    val uiState by viewModel.uiState.collectAsState()
    val cartState by viewModel.cartUiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.frutties),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .weight(1.0f),
                    textAlign = TextAlign.Center,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            var expanded by remember { mutableStateOf(false) }
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cart has ${cartState.itemList.count()} items",
                    modifier = Modifier.weight(1f).padding(12.dp),
                )
                Button(onClick = { expanded = !expanded }) {
                    Text(text = if (expanded) "collapse" else "expand")
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut(animationSpec = tween(1000)),
            ) {
                CartDetailsView(cartState.itemList, viewModel::removeItemFromCart)
            }

            LazyColumn {
                items(items = uiState.itemList, key = { it.id }) { item ->
                    FruittieItem(
                        item = item,
                        onAddToCart = viewModel::addItemToCart,
                        onDelToCart = viewModel::removeItemFromCart // Add removal functionality
                    )
                }
            }
        }
    }
}

@Composable
fun FruittieItem(
    item: Fruittie,
    onAddToCart: (fruittie: Fruittie) -> Unit,
    onDelToCart: (fruittie: Fruittie) -> Unit, // Added for removing an item
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .heightIn(min = 96.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = item.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                )
                Text(
                    text = item.fullName,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.calories,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { onAddToCart(item) }) {
                    Text(stringResource(R.string.add))
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Button(onClick = { onDelToCart(item) }) { // Add remove button
                    Text(stringResource(R.string.remove))
                }
            }
        }
    }
}

@Composable
fun CartDetailsView(
    cart: List<CartItemDetails>?,
    onRemove: (fruittie: Fruittie) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cart.isNullOrEmpty()) {
        Text(text = "Cart is empty", modifier = Modifier.padding(16.dp))
    } else {
        var totalCalories by remember { mutableStateOf(0) }

        totalCalories = cart.sumBy {
            it.fruittie.calories.replace(" calories", "").toInt() * it.count
        }

        Column(modifier.padding(horizontal = 32.dp)) {
            cart.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "${item.fruittie.name} : ${item.count}")
                    Button(onClick = { onRemove(item.fruittie) }) {
                        Text(text = stringResource(R.string.remove))
                    }
                }
            }
            Text(text = "Total Calories: $totalCalories")
        }
    }
}
