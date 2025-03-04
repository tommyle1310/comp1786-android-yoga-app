package com.example.universalyogaadmin_comp1786

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.universalyogaadmin_comp1786.data.AppDatabase
import com.example.universalyogaadmin_comp1786.data.Course
import com.example.universalyogaadmin_comp1786.ui.theme.UniversalYogaAdminCOMP1786Theme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

class ConfirmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniversalYogaAdminCOMP1786Theme {
                ConfirmScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ConfirmScreen() {
        val day = intent.getStringExtra("DAY") ?: ""
        val time = intent.getStringExtra("TIME") ?: ""
        val capacity = intent.getStringExtra("CAPACITY") ?: ""
        val duration = intent.getStringExtra("DURATION") ?: ""
        val price = intent.getStringExtra("PRICE") ?: ""
        val type = intent.getStringExtra("TYPE") ?: ""
        val desc = intent.getStringExtra("DESC") ?: ""

        val context = this@ConfirmActivity
        val db = AppDatabase.getDatabase(context)
        val courseDao = db.courseDao()
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = { TopAppBar(title = { Text("Confirm Yoga Course") }) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Day: $day")
                Text("Time: $time")
                Text("Capacity: $capacity")
                Text("Duration: $duration mins")
                Text("Price: £$price")
                Text("Type: $type")
                Text("Description: $desc")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        scope.launch {
                            val course = Course(
                                day = day,
                                time = time,
                                capacity = capacity,
                                duration = duration,
                                price = price,
                                type = type,
                                description = desc
                            )
                            courseDao.insert(course)
                            android.widget.Toast.makeText(context, "Course saved!", android.widget.Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }) {
                        Text("Confirm")
                    }
                    Button(onClick = { finish() }) {
                        Text("Edit")
                    }
                }
            }
        }
    }
}