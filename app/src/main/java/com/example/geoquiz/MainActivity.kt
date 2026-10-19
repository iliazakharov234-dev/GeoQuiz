package com.example.geoquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.geoquiz.ui.theme.GeoQuizTheme

// ============================================================
//  МОДЕЛЬ ВОПРОСА
// ============================================================
data class Question(
    val text: String,
    val answer: Boolean
)

// Список вопросов
val questions = listOf(
    Question("Canberra is the capital of Australia.", true),
    Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
    Question("The Suez Canal connects the Red Sea and the Indian Ocean.", true),
    Question("The source of the Nile River is in Egypt.", false),
    Question("The Amazon River is the longest river in the Americas.", false),
    Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
)

// ============================================================
//  MAIN ACTIVITY
// ============================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ============================================================
//  UI
// ============================================================
@Composable
fun QuizScreen(modifier: Modifier = Modifier) {
    // Текущий индекс вопроса
    var currentIndex by remember { mutableIntStateOf(0) }

    // Ответил ли пользователь на текущий вопрос
    var answered by remember { mutableStateOf(false) }

    // Количество правильных ответов
    var correctCount by remember { mutableIntStateOf(0) }

    // Показывать ли финальное окно
    var showResult by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentIndex]
    val isLastQuestion = currentIndex == questions.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Номер вопроса
        Text(
            "Вопрос ${currentIndex + 1} из ${questions.size}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Текст вопроса
        Text(
            currentQuestion.text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // Кнопки True / False — видны, пока не ответили
        if (!answered) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        answered = true
                        if (currentQuestion.answer) correctCount++
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("True")
                }

                Button(
                    onClick = {
                        answered = true
                        if (!currentQuestion.answer) correctCount++
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("False")
                }
            }
        } else {
            // После ответа показываем результат по вопросу
            Text(
                text = if (
                    (currentQuestion.answer && correctCount > 0) ||
                    (!currentQuestion.answer)
                ) "Ответ принят" else "Ответ принят",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(24.dp))

        // Кнопка Next — скрыта на последнем вопросе
        if (!isLastQuestion) {
            Button(
                onClick = {
                    currentIndex++
                    answered = false
                },
                enabled = answered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next")
            }
        }
    }

    // Финальное окно с результатом
    if (showResult) {
        AlertDialog(
            onDismissRequest = { showResult = false },
            title = { Text("Тест завершён") },
            text = {
                Text("Правильных ответов: $correctCount из ${questions.size}")
            },
            confirmButton = {
                TextButton(onClick = {
                    // Сброс теста
                    currentIndex = 0
                    answered = false
                    correctCount = 0
                    showResult = false
                }) {
                    Text("Заново")
                }
            }
        )
    }

    // Проверяем, не пора ли показать результат
    LaunchedEffect(answered) {
        if (answered && isLastQuestion) {
            showResult = true
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    GeoQuizTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            QuizScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}