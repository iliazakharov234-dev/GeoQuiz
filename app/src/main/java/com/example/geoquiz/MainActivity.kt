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

@Composable
fun QuizScreen(modifier: Modifier = Modifier) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentIndex]
    val isLastQuestion = currentIndex == questions.lastIndex

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Вопрос ${currentIndex + 1} из ${questions.size}", fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))

        Text(currentQuestion.text, fontSize = 22.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        if (!answered) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    answered = true
                    isCorrect = currentQuestion.answer
                    if (isCorrect) correctCount++
                }, modifier = Modifier.weight(1f)) { Text("True") }

                Button(onClick = {
                    answered = true
                    isCorrect = !currentQuestion.answer
                    if (isCorrect) correctCount++
                }, modifier = Modifier.weight(1f)) { Text("False") }
            }
        } else {
            Text(
                if (isCorrect) "✅ Верно!" else "❌ Неверно",
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        if (!isLastQuestion) {
            Button(
                onClick = {
                    currentIndex++
                    answered = false
                },
                enabled = answered,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Next") }
        }
    }

    // Всплывающая панель (AlertDialog)
    if (showResult) {
        AlertDialog(
            onDismissRequest = { showResult = false },
            title = { Text("Тест завершён") },
            text = { Text("Правильных ответов: $correctCount из ${questions.size}") },
            confirmButton = {
                TextButton(onClick = {
                    currentIndex = 0
                    answered = false
                    correctCount = 0
                    showResult = false
                }) { Text("Заново") }
            }
        )
    }

    // Показ диалога после ответа на последний вопрос
    LaunchedEffect(answered, isLastQuestion) {
        if (answered && isLastQuestion) {
            kotlinx.coroutines.delay(1000) // небольшая задержка, чтобы юзер увидел "Верно/Неверно"
            showResult = true
        }
    }
}
