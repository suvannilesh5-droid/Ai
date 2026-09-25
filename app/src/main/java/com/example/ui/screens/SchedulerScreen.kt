package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ScheduleConfigEntity
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(
    scheduleConfig: ScheduleConfigEntity,
    onSaveSchedule: (
        isAutomationActive: Boolean,
        morningHour: Int,
        morningMinute: Int,
        morningNiche: String,
        eveningHour: Int,
        eveningMinute: Int,
        eveningNiche: String,
        dynamicMusic: Boolean,
        deepMotionRig: String,
        runInBackground: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isAutomationActive by remember(scheduleConfig) { mutableStateOf(scheduleConfig.isAutomationActive) }
    var runInBackground by remember(scheduleConfig) { mutableStateOf(scheduleConfig.runInBackgroundWhenLocked) }
    var dynamicMusic by remember(scheduleConfig) { mutableStateOf(scheduleConfig.dynamicMusicSelection) }

    // Morning Short settings
    var morningHour by remember(scheduleConfig) { mutableIntStateOf(scheduleConfig.morningHour) }
    var morningMinute by remember(scheduleConfig) { mutableIntStateOf(scheduleConfig.morningMinute) }
    var morningNiche by remember(scheduleConfig) { mutableStateOf(scheduleConfig.morningNiche) }

    // Evening Long settings
    var eveningHour by remember(scheduleConfig) { mutableIntStateOf(scheduleConfig.eveningHour) }
    var eveningMinute by remember(scheduleConfig) { mutableIntStateOf(scheduleConfig.eveningMinute) }
    var eveningNiche by remember(scheduleConfig) { mutableStateOf(scheduleConfig.eveningNiche) }

    var deepMotionRig by remember(scheduleConfig) { mutableStateOf(scheduleConfig.deepMotionEnginePreset) }

    val nicheOptions = listOf(
        "Viral 3D Cartoon Shorts",
        "Sci-Fi & Animated Fable Mysteries",
        "Funny Animal Cartoon Adventures",
        "Cyberpunk Robot Satire",
        "Educational & Moral 3D Stories"
    )

    val rigOptions = listOf(
        "DeepMotion Animate 3D (Cartoon Rig)",
        "Pixar Stylized Biped v4.2",
        "Unreal Engine Toon Dynamics",
        "Expressive Facial Motion Rig"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("scheduler_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Automation Toggle Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "24/7 Automation Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Triggers automatically and publishes according to schedules",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isAutomationActive,
                            onCheckedChange = { isAutomationActive = it },
                            modifier = Modifier.testTag("master_automation_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Run in background when locked toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (runInBackground) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Lock Safe",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Run In Background When Locked",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Uses AlarmManager exact wake-lock & foreground service",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = runInBackground,
                            onCheckedChange = { runInBackground = it }
                        )
                    }
                }
            }
        }

        // STEP 2.1: Morning Short Video Timer
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Shorts",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2.1 Morning Short Video Timer",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "9:16 Vertical (<60s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Time Picker Inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = String.format("%02d", morningHour),
                            onValueChange = { str ->
                                val v = str.toIntOrNull()
                                if (v != null && v in 0..23) morningHour = v
                            },
                            label = { Text("Hour (0-23)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = String.format("%02d", morningMinute),
                            onValueChange = { str ->
                                val v = str.toIntOrNull()
                                if (v != null && v in 0..59) morningMinute = v
                            },
                            label = { Text("Minute (0-59)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = morningNiche,
                        onValueChange = { morningNiche = it },
                        label = { Text("Morning Content Niche") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // STEP 2.1: Evening Long Video Timer
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = "Long Form",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2.1 Evening Long Video Timer",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "16:9 Movie (Multi-scene)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = String.format("%02d", eveningHour),
                            onValueChange = { str ->
                                val v = str.toIntOrNull()
                                if (v != null && v in 0..23) eveningHour = v
                            },
                            label = { Text("Hour (0-23)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = String.format("%02d", eveningMinute),
                            onValueChange = { str ->
                                val v = str.toIntOrNull()
                                if (v != null && v in 0..59) eveningMinute = v
                            },
                            label = { Text("Minute (0-59)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = eveningNiche,
                        onValueChange = { eveningNiche = it },
                        label = { Text("Evening Content Niche") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // STEP 7.2: Dynamic Music & DeepMotion Rig Configuration
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI Sound & 3D Animation Settings",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "7.2 Dynamic Music Rotation",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Guarantees a distinct non-copyright music track for every new video",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = dynamicMusic,
                            onCheckedChange = { dynamicMusic = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deepMotionRig,
                        onValueChange = { deepMotionRig = it },
                        label = { Text("DeepMotion 3D Rig Preset") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Save & Apply Button
        item {
            Button(
                onClick = {
                    onSaveSchedule(
                        isAutomationActive,
                        morningHour,
                        morningMinute,
                        morningNiche,
                        eveningHour,
                        eveningMinute,
                        eveningNiche,
                        dynamicMusic,
                        deepMotionRig,
                        runInBackground
                    )
                    Toast.makeText(context, "Schedules updated & background alarms armed!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_schedule_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Arm Automation Timers", fontWeight = FontWeight.Bold)
            }
        }
    }
}
