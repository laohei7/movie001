package com.zero.movie001

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.zero.movie001.ui.theme.Movie001Theme
import com.zero.movie001.util.px
import com.zero.movie001.util.textPx
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Movie001Theme {

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MovieScreen() {
    val nowPlaying = remember {
        listOf(
            R.mipmap.movie1,
            R.mipmap.movie2,
            R.mipmap.movie3,
            R.mipmap.movie3,
            R.mipmap.movie3,
        )
    }
    val comingSoon = remember {
        listOf(
            R.mipmap.movie4,
            R.mipmap.movie5,
            R.mipmap.movie6,
            R.mipmap.movie6,
            R.mipmap.movie6,
        )
    }
    val topMovies = remember {
        listOf(
            R.mipmap.movie7,
            R.mipmap.movie8,
            R.mipmap.movie9,
            R.mipmap.movie9,
            R.mipmap.movie9,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2E1371),
                        Color(0xFF130B2B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Title(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .statusBarsPadding()
                    .fillMaxWidth()
            )

            SearchField(
                modifier = Modifier
                    .padding(horizontal = 20.px())
                    .padding(top = 36.px())
                    .fillMaxWidth()
            )

            MovieRow(
                modifier = Modifier
                    .padding(top = 20.px())
                    .fillMaxWidth(),
                title = "Now Playing",
                movies = nowPlaying
            )

            MovieRow(
                modifier = Modifier
                    .padding(top = 20.px())
                    .fillMaxWidth(),
                title = "Coming Soon",
                movies = comingSoon
            )

            MovieRow(
                modifier = Modifier
                    .padding(top = 20.px())
                    .fillMaxWidth(),
                title = "Top movies",
                movies = topMovies
            )
        }

        // BottomBar
        MovieBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.Transparent)
        )
    }
}

@Composable
fun MovieBottomBar(
    modifier: Modifier = Modifier
) {
    val icons = remember {
        listOf(
            R.mipmap.nav_home,
            R.mipmap.nav_location,
            R.mipmap.nav_ticket,
            R.mipmap.nav_category,
            R.mipmap.nav_profile,
        )
    }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var containerHeight by remember { mutableIntStateOf(0) }
    var containerWidth by remember { mutableIntStateOf(0) }
    val tabWidthList = remember { mutableListOf<Int>() }
    var gap by remember { mutableIntStateOf(0) }
    var indicatorWidth by remember { mutableIntStateOf(0) }
    val animatedOffsetX by animateIntAsState(
        targetValue = gap * (selectedIndex + 1) + tabWidthList.let { list ->
            val value = if (list.size < selectedIndex + 1) {
                0
            } else {
                list.take(selectedIndex + 1).sumOf { it }
            }
            value - if (list.size > selectedIndex) {
                list[selectedIndex] / 2
            } else {
                0
            }
        } - indicatorWidth / 2,
//        animationSpec = tween(200)
    )
    val animatedOffsetYList = remember { mutableListOf<Animatable<Float, AnimationVector1D>>() }

    LaunchedEffect(selectedIndex) {
        if (tabWidthList.isEmpty()) {
            return@LaunchedEffect
        }
        // 使用结构并发控制动画并行启动
        launch {
            // 保证选中的Nav动画第一时间能启动
            val initialY = (indicatorWidth * 0.3f).roundToInt() - tabWidthList[selectedIndex] / 2
            animatedOffsetYList[selectedIndex].animateTo(
                initialY.toFloat(),
                animationSpec = spring(visibilityThreshold = 1f)
//                    animationSpec = tween(200)
            )
        }
        launch {
            // 重置其他未选中的Nav
            animatedOffsetYList.fastForEachIndexed { index, animatable ->
                if (index == selectedIndex) {
//                val initialY = (indicatorWidth * 0.3f).roundToInt() - tabWidthList[index] / 2
//                animatable.animateTo(
//                    initialY.toFloat(),
//                    animationSpec = spring(visibilityThreshold = 1f)
////                    animationSpec = tween(200)
//                )
                } else {
                    val initialY = containerHeight / 3 - tabWidthList[index] / 3
                    animatable.animateTo(
                        initialY.toFloat(),
                        animationSpec = spring(visibilityThreshold = 1f)
//                    animationSpec = tween(200)
                    )
                }
            }
        }

    }

    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->
        val tabs = subcompose("tabs") {
            icons.fastForEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(44.px())
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedIndex = index
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.px())
                    )
                }
            }
        }.map { it.measure(constraints) }
        val background = subcompose("bg") {
            Box(
                Modifier
                    .fillMaxWidth()
                    .blur(100.px())
                    .height(90.px())
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2E1371),
                                Color(0xFF130B2B)
                            )
                        )
                    )
                    .background(Color.White.copy(0.2f))
                    .border(
                        width = 1.px(),
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(0.dp)
                    )
            )
        }.map { it.measure(constraints) }

        val indicator = subcompose("indicator") {
            Box(
                Modifier
                    .size(80.px())
                    .shadow(8.px(), shape = CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2E1371),
                                Color(0xFF130B2B)
                            )
                        )
                    )
                    .background(Color.White.copy(0.2f))
                    .border(
                        width = 2.px(),
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF60FFCA),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }.map { it.measure(constraints) }

        val height = background.first().height
        val width = background.first().width
        val totalTabWidth = tabs.sumOf { it.width }
        val totalGap = width - totalTabWidth
        val tabGap = totalGap / (tabs.size + 1)
        val indicatorHeight = indicator.first().height
        containerHeight = height
        containerWidth = width
        tabs.fastForEachIndexed { index, placeable ->
            if (selectedIndex == index) {
                val initialY = (indicatorHeight * 0.3f).roundToInt() - placeable.height / 2
                animatedOffsetYList.add(Animatable(initialY.toFloat()))
            } else {
                val initialY = height / 3 - placeable.height / 3
                animatedOffsetYList.add(Animatable(initialY.toFloat()))
            }
            tabWidthList.add(placeable.width)
        }
        gap = tabGap
        indicatorWidth = indicatorHeight

        layout(width, height) {
            background.fastForEach { it.place(0, 0) }
            indicator.fastForEach {
                it.place(
//                    tabGap + tabs.first().width / 2 - it.width / 2,
                    animatedOffsetX,
                    -it.height / 5
                )
            }
            var xPosition = tabGap
            tabs.fastForEachIndexed { index, placeable ->
//                height / 3 - it.height / 3
//                val yPosition = if (selectedIndex == index) {
//                    (indicatorHeight * 0.3f).roundToInt() - placeable.height / 2
//                } else {
//                    height / 3 - placeable.height / 3
//                }
                placeable.place(xPosition, animatedOffsetYList[index].value.roundToInt())
                xPosition += placeable.width + tabGap
            }
        }
    }
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier
) {
    var field by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = modifier,
        value = field,
        onValueChange = { field = it },
        shape = RoundedCornerShape(10.px()),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFF767680).copy(.12f),
            focusedContainerColor = Color(0xFF767680).copy(.12f),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
        ),
        leadingIcon = {
            Image(
                painter = painterResource(R.mipmap.icon_search),
                contentDescription = null,
                modifier = Modifier.size(16.px())
            )
        },
        trailingIcon = {
            Image(
                painter = painterResource(R.mipmap.icon_microphone),
                contentDescription = null,
                modifier = Modifier.size(16.px())
            )
        },
        placeholder = {
            Text(
                text = "Search",
                color = Color.White.copy(.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 17.textPx()
            )
        }
    )
}

@Composable
fun Title(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Choose Movie",
        modifier = modifier,
        fontSize = 20.textPx(),
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(0.87f),
        textAlign = TextAlign.Center
    )
}


@Composable
fun MovieRow(
    modifier: Modifier = Modifier,
    title: String,
    movies: List<Int>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.px())
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 17.textPx(),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.px())
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.px())
        ) {
            item { Spacer(Modifier) }
            items(movies) { movie ->
                Image(
                    painter = painterResource(movie),
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.px())
                        .height(130.px())
                        .clip(RoundedCornerShape(20.px())),
                    contentScale = ContentScale.Crop
                )
            }
            item { Spacer(Modifier) }
        }
    }

}