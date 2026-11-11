package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.NewsArticle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(onBack: () -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Global", "National (India)", "State")
    
    val indianStates = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", 
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", 
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", 
        "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", 
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", 
        "Uttar Pradesh", "Uttarakhand", "West Bengal"
    )
    
    var selectedState by remember { mutableStateOf(indianStates[0]) }
    var expandedStateMenu by remember { mutableStateOf(false) }

    val allArticles = remember(selectedState) {
        mapOf(
            0 to listOf(
                NewsArticle("Global Tech Breakthrough", "Scientists discover a new AI processing method...", "https://google.com", "https://picsum.photos/400/200?random=1", "TechDaily", "2h ago"),
                NewsArticle("World Market Trends", "Global markets are shifting...", "https://google.com", "https://picsum.photos/400/200?random=2", "FinancePost", "4h ago")
            ),
            1 to listOf(
                NewsArticle("ISRO's Next Big Mission", "India's space agency prepares for the next lunar exploration...", "https://google.com", "https://picsum.photos/400/200?random=10", "IndiPress", "1h ago"),
                NewsArticle("New Economic Growth in India", "The manufacturing sector shows a 12% rise this quarter...", "https://google.com", "https://picsum.photos/400/200?random=11", "BharatTimes", "3h ago"),
                NewsArticle("India's Cricket Victory", "The national team secures a historic win in the series...", "https://google.com", "https://picsum.photos/400/200?random=12", "SportIndia", "5h ago")
            ),
            2 to listOf(
                NewsArticle("$selectedState Local Development", "Infrastructure projects in $selectedState receive new funding...", "https://google.com", "https://picsum.photos/400/200?random=13", "${selectedState} News", "30m ago"),
                NewsArticle("Cultural Festival in $selectedState", "Residents celebrate the annual heritage week with joy...", "https://google.com", "https://picsum.photos/400/200?random=14", "StateMirror", "4h ago")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (selectedTabIndex == 2) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    OutlinedCard(
                        onClick = { expandedStateMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Select State: $selectedState")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(
                        expanded = expandedStateMenu,
                        onDismissRequest = { expandedStateMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        indianStates.forEach { state ->
                            DropdownMenuItem(
                                text = { Text(state) },
                                onClick = {
                                    selectedState = state
                                    expandedStateMenu = false
                                }
                            )
                        }
                    }
                }
            }

            val currentArticles = allArticles[selectedTabIndex] ?: emptyList()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(currentArticles) { article ->
                    NewsItem(article)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun NewsItem(article: NewsArticle) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(article.url) }
            .padding(16.dp)
    ) {
        AsyncImage(
            model = article.urlToImage,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = article.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${article.sourceName} • ${article.publishedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            IconButton(onClick = { /* Share logic */ }) {
                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(20.dp))
            }
        }
    }
}
