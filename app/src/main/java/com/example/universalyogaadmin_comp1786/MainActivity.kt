package com.example.universalyogaadmin_comp1786

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import com.example.universalyogaadmin_comp1786.data.Instance
import com.example.universalyogaadmin_comp1786.data.ApiService
import com.example.universalyogaadmin_comp1786.data.UploadData
import com.example.universalyogaadmin_comp1786.ui.theme.UniversalYogaAdminCOMP1786Theme
import kotlinx.coroutines.flow.first // Thêm import
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.res.painterResource
import androidx.activity.compose.rememberLauncherForActivityResult

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniversalYogaAdminCOMP1786Theme {
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val courseDao = db.courseDao()
                val courses by courseDao.getAllCourses().collectAsState(initial = emptyList())
                var screenState by remember { mutableStateOf("Courses") }

                when (screenState) {
                    "Courses" -> CourseScreen(courseDao, courses) { screenState = it }
                    "Search" -> SearchScreen(courseDao) { screenState = "Courses" }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    courseDao: com.example.universalyogaadmin_comp1786.data.CourseDao,
    courses: List<Course>,
    onScreenChange: (String) -> Unit
) {
    var day by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    val instances by (selectedCourse?.let { courseDao.getInstancesForCourse(it.id) } ?: flowOf(emptyList())).collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(if (selectedCourse == null) "Yoga Courses" else "Instances for ${selectedCourse?.type}") }) }
    ) { paddingValues ->
        if (selectedCourse == null) {
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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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

                TextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 10:00)") }, modifier = Modifier.fillMaxWidth())
                TextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Capacity") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                TextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (mins)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                TextField(value = price, onValueChange = { price = it }, label = { Text("Price (£)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())

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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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

                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Description (optional)") }, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        if (day.isEmpty() || time.isEmpty() || capacity.isEmpty() || duration.isEmpty() || price.isEmpty() || type.isEmpty()) {
                            android.widget.Toast.makeText(context, "Please fill all required fields", android.widget.Toast.LENGTH_SHORT).show()
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

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(courses) { course ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${course.day} ${course.time} - ${course.type} (£${course.price})")
                            Row {
                                Button(onClick = { scope.launch { courseDao.delete(course) } }) { Text("Delete") }
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
                                }) { Text("Edit") }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { selectedCourse = course }) { Text("Instances") }
                            }
                        }
                    }
                }

                Button(onClick = { onScreenChange("Search") }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Search Instances")
                }

                Button(
                    onClick = {
                        scope.launch {
                            val networkAvailable = isNetworkAvailable(context)
                            if (networkAvailable) {
                                val retrofit = Retrofit.Builder()
                                    .baseUrl("https://your-neondb-api.com/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                val service = retrofit.create(ApiService::class.java)
                                val allInstances = courses.flatMap { course -> courseDao.getInstancesForCourse(course.id).first() }
                                val data = UploadData(courses, allInstances)
                                service.uploadData(data)
                                android.widget.Toast.makeText(context, "Uploaded!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                android.widget.Toast.makeText(context, "No network!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Upload to Cloud")
                }

                Button(onClick = { scope.launch { courseDao.resetDatabase() } }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Reset Database")
                }
            }
        } else {
            InstanceScreen(selectedCourse!!, courseDao, instances) { selectedCourse = null }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstanceScreen(
    course: Course,
    courseDao: com.example.universalyogaadmin_comp1786.data.CourseDao,
    instances: List<Instance>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var date by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri = Uri.parse("file://temp_photo.jpg") // Placeholder
        }
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = date, onValueChange = { date = it }, label = { Text("Date (e.g. 17/10/2023)") }, modifier = Modifier.fillMaxWidth())
        TextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher") }, modifier = Modifier.fillMaxWidth())
        TextField(value = comments, onValueChange = { comments = it }, label = { Text("Comments (optional)") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                if (date.isEmpty() || teacher.isEmpty()) {
                    android.widget.Toast.makeText(context, "Please fill all required fields", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        val instance = Instance(courseId = course.id, date = date, teacher = teacher, comments = comments)
                        courseDao.insertInstance(instance)
                        date = ""
                        teacher = ""
                        comments = ""
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Instance")
        }

        Button(
            onClick = {
                val tempUri = Uri.parse("file://temp_photo.jpg") // Placeholder
                cameraLauncher.launch(tempUri)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Take Photo")
        }

        if (photoUri != null) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Captured Photo",
                modifier = Modifier.size(100.dp)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(instances) { instance ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${instance.date} - ${instance.teacher}")
                    Row {
                        Button(onClick = { scope.launch { courseDao.deleteInstance(instance) } }) { Text("Delete") }
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back to Courses")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(courseDao: com.example.universalyogaadmin_comp1786.data.CourseDao, onBack: () -> Unit) {
    val context = LocalContext.current
    var teacherPrefix by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    val instances by courseDao.searchInstances(teacherPrefix, date, day).collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Search Instances") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(value = teacherPrefix, onValueChange = { teacherPrefix = it }, label = { Text("Teacher (prefix)") }, modifier = Modifier.fillMaxWidth())
            TextField(value = date, onValueChange = { date = it }, label = { Text("Date (e.g. 17/10/2023)") }, modifier = Modifier.fillMaxWidth())
            TextField(value = day, onValueChange = { day = it }, label = { Text("Day of Week") }, modifier = Modifier.fillMaxWidth())

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(instances) { instance ->
                    Text("${instance.date} - ${instance.teacher}", modifier = Modifier.padding(8.dp))
                }
            }

            Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Back")
            }
        }
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    return true // Placeholder
}