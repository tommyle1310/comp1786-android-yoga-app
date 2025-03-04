package com.example.universalyogaadmin_comp1786

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.universalyogaadmin_comp1786.data.AppDatabase
import com.example.universalyogaadmin_comp1786.data.Course
import com.example.universalyogaadmin_comp1786.ui.theme.UniversalYogaAdminCOMP1786Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniversalYogaAdminCOMP1786Theme {
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val courseDao = db.courseDao()
                val courses by courseDao.getAllCourses().collectAsState(initial = emptyList()) // Fix lỗi Type mismatch

                CourseScreen(courseDao, courses)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    courseDao: com.example.universalyogaadmin_comp1786.data.CourseDao,
    courses: List<Course>
) {
    var day by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Fix lỗi CoroutineScope

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Yoga Courses") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Form nhập liệu
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            var expandedDay by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedDay,
                onExpandedChange = { expandedDay = !expandedDay }
            ) {
                TextField(
                    value = day,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Day of the Week") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDay,
                    onDismissRequest = { expandedDay = false }
                ) {
                    days.forEach { dayOption ->
                        DropdownMenuItem(
                            text = { Text(dayOption) },
                            onClick = {
                                day = dayOption
                                expandedDay = false
                            }
                        )
                    }
                }
            }

            TextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time (e.g. 10:00)") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Capacity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (mins)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price (£)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            val types = listOf("Flow Yoga", "Aerial Yoga", "Family Yoga")
            var expandedType by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType }
            ) {
                TextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type of Class") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    types.forEach { typeOption ->
                        DropdownMenuItem(
                            text = { Text(typeOption) },
                            onClick = {
                                type = typeOption
                                expandedType = false
                            }
                        )
                    }
                }
            }

            TextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (day.isEmpty() || time.isEmpty() || capacity.isEmpty() || duration.isEmpty() || price.isEmpty() || type.isEmpty()) {
                        android.widget.Toast.makeText(
                            context,
                            "Please fill all required fields",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(context, ConfirmActivity::class.java).apply {
                            putExtra("DAY", day)
                            putExtra("TIME", time)
                            putExtra("CAPACITY", capacity)
                            putExtra("DURATION", duration)
                            putExtra("PRICE", price)
                            putExtra("TYPE", type)
                            putExtra("DESC", desc)
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Save")
            }

            // Danh sách courses
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(courses) { course ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${course.day} ${course.time} - ${course.type} (£${course.price})")
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    courseDao.delete(course)
                                }
                            }) {
                                Text("Delete")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val intent = Intent(context, ConfirmActivity::class.java).apply {
                                    putExtra("DAY", course.day)
                                    putExtra("TIME", course.time)
                                    putExtra("CAPACITY", course.capacity)
                                    putExtra("DURATION", course.duration)
                                    putExtra("PRICE", course.price)
                                    putExtra("TYPE", course.type)
                                    putExtra("DESC", course.description)
                                }
                                context.startActivity(intent)
                            }) {
                                Text("Edit")
                            }
                        }
                    }
                }
            }

            // Reset Database
            Button(
                onClick = {
                    scope.launch {
                        courseDao.resetDatabase()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Database")
            }
        }
    }
}