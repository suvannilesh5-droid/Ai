package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VideoProjectEntity
import com.example.data.model.SceneItem
import com.example.data.model.UserProfile
import com.example.data.repository.MusicRepository
import com.example.ui.components.DeepMotionPreview
import com.example.ui.components.MusicPlayerCard
import com.example.ui.theme.DeepMotionCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.YouTubeRed
import com.example.ui.viewmodel.PipelineState

@Composable
fun CreatePipelineScreen(
    pipelineState: PipelineState,
    userProfile: UserProfile,
    isVoicePlaying: Boolean,
    onTriggerPipeline: (String) -> Unit,
    onPlayVoiceover: (String) -> Unit,
    onStopVoiceover: () -> Unit,
    modifier: Modifier = Modifier
) {
    val musicRepo = remember { MusicRepository() }
    var selectedVideoType by remember { mutableStateOf("SHORT") }
    var selectedSceneIndex by remember { mutableIntStateOf(0) }

    val scenes = if (pipelineState.activeScenes.isNotEmpty()) {
        pipelineState.activeScenes
    } else {
        rememberDefaultScenes(selectedVideoType)
    }

    val activeTrack = pipelineState.activeMusicTrack ?: musicRepo.getAllTracks().first()
    val activeScene = scenes.getOrNull(selectedSceneIndex) ?: scenes.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("create_pipeline_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Selector Tab (Morning Short vs Evening Long)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Video Blueprint Pipeline Format",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TabRow(
                        selectedTabIndex = if (selectedVideoType == "SHORT") 0 else 1,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedVideoType == "SHORT",
                            onClick = { selectedVideoType = "SHORT" },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Videocam, contentDescription = "Short", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Morning Short (9:16)")
                                }
                            }
                        )
                        Tab(
                            selected = selectedVideoType == "LONG",
                            onClick = { selectedVideoType = "LONG" },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Tv, contentDescription = "Long", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Evening Long (16:9)")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onTriggerPipeline(selectedVideoType) },
                        enabled = !pipelineState.isRunning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_pipeline_now_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Run")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (pipelineState.isRunning) "Running 9-Step Pipeline (${pipelineState.currentStepIndex}/9)..." else "Generate 3D Cartoon Video Now",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Step Progression Tracker (Steps 1 through 9)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "9-Step Automation Architecture",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (pipelineState.isRunning) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (pipelineState.isRunning) "PROCESSING" else "STANDBY READY",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (pipelineState.isRunning) MaterialTheme.colorScheme.primary else SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { if (pipelineState.isRunning) pipelineState.currentStepIndex / 9f else 1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step badges row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(getBlueprintSteps()) { step ->
                            val isActive = pipelineState.currentStepIndex == step.index
                            val isCompleted = pipelineState.currentStepIndex > step.index || (!pipelineState.isRunning && pipelineState.currentStepIndex == 9)

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    isActive -> MaterialTheme.colorScheme.primary
                                    isCompleted -> SuccessGreen.copy(alpha = 0.15f)
                                    else -> MaterialTheme.colorScheme.surface
                                },
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = step.icon,
                                        contentDescription = step.name,
                                        tint = when {
                                            isActive -> Color.White
                                            isCompleted -> SuccessGreen
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${step.index}. ${step.name}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when {
                                            isActive -> Color.White
                                            isCompleted -> SuccessGreen
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 6: Interactive 3D Cartoon DeepMotion Animation Preview
        item {
            Text(
                text = "Step 6: 3D Cartoon Animation (DeepMotion)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            DeepMotionPreview(
                scene = activeScene,
                aspectRatioString = if (selectedVideoType == "SHORT") "9:16" else "16:9"
            )
        }

        // STEP 4 & 5: Scene-by-Scene Breakdown Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Steps 4 & 5: Scene Breakdown (${scenes.size} Scenes)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Prompt & Concept Ready",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(scenes.indices.toList()) { index ->
                    val sc = scenes[index]
                    val isSelected = selectedSceneIndex == index
                    OutlinedCard(
                        onClick = { selectedSceneIndex = index },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.width(130.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Scene ${sc.sceneNumber}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = sc.title,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                            Text(
                                text = "${sc.durationSeconds}s • ${sc.cameraAngle.take(12)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Active Scene Details Card
        activeScene?.let { scene ->
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Scene ${scene.sceneNumber}: ${scene.title}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Visual Prompt:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = scene.prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Character Motion & Action:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = DeepMotionCyan
                        )
                        Text(
                            text = "${scene.characterAction} (${scene.motionStyle})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // STEP 7: Audio & Non-Copyright Background Music
        item {
            Text(
                text = "Step 7: Audio Narration & Non-Copyright Music",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            MusicPlayerCard(
                track = activeTrack,
                voiceoverScript = activeScene?.voiceover,
                isVoicePlaying = isVoicePlaying,
                onPlayVoiceover = onPlayVoiceover,
                onStopVoiceover = onStopVoiceover
            )
        }

        // STEP 3 & 8: Market Analysis & Content Filling (SEO Title, Description, Tags)
        item {
            Text(
                text = "Step 8: Automated YouTube Content Filling",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YouTube Metadata (Auto-Generated)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = YouTubeRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "SEO 100/100",
                                style = MaterialTheme.typography.labelSmall,
                                color = YouTubeRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val displayTitle = pipelineState.generatedVideo?.title ?: (if (selectedVideoType == "SHORT") "⏰ The 10-Second Time Glitch! #Shorts #3DCartoon" else "The Quantum Toymaker | Lumina Chronicles (Full 3D Movie)")
                    val displayTags = pipelineState.generatedVideo?.tags ?: "3danimation, cartoon, deepmotion, shorts, cgi, pixar, viral, blender"

                    Text(
                        text = "Title:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "SEO Tags:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = displayTags,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

data class BlueprintStep(val index: Int, val name: String, val icon: ImageVector)

private fun getBlueprintSteps(): List<BlueprintStep> = listOf(
    BlueprintStep(1, "OAuth Auth", Icons.Default.Person),
    BlueprintStep(2, "Background Lock", Icons.Default.LockOpen),
    BlueprintStep(3, "Market AI", Icons.Default.Analytics),
    BlueprintStep(4, "Scene Prompts", Icons.Default.Description),
    BlueprintStep(5, "Photo Gen", Icons.Default.Image),
    BlueprintStep(6, "DeepMotion 3D", Icons.Default.Animation),
    BlueprintStep(7, "Voice & Music", Icons.Default.MusicNote),
    BlueprintStep(8, "Metadata Filling", Icons.Default.AutoAwesome),
    BlueprintStep(9, "Schedule Upload", Icons.Default.CloudUpload)
)

private fun rememberDefaultScenes(videoType: String): List<SceneItem> {
    return if (videoType == "SHORT") {
        listOf(
            SceneItem(1, "The Neon Spark", "3D Pixar style boy inventor discovering glowing gadget, vertical 9:16", "Pip thought it was an ordinary morning gadget, until it started glowing blue!", 15, "Dynamic Stumble & Look Around", "Low Angle Tilt", "Neon Glow", "Curious inspection", "cartoon_scene_scifi"),
            SceneItem(2, "Time Loop Sneeze", "Humorous 3D cartoon robot cat jumping in mid air with confetti", "One tiny sneeze, and suddenly time began rewinding at supersonic speed!", 15, "Acrobatic Jump & Twist", "Dynamic Tracking", "Rainbow Sparkles", "Mid-air cartoon stretch", "cartoon_scene_scifi"),
            SceneItem(3, "Timeline Saved", "Heroic 3D animated high five with stars", "And just like that, the timeline was saved! Double tap if you survived the loop.", 15, "Celebratory Dance Loop", "Hero Low Angle", "Golden Sunset", "High-five spin", "cartoon_scene_scifi")
        )
    } else {
        listOf(
            SceneItem(1, "Dawn in Lumina", "Expansive 3D cartoon steampunk metropolis with airships, 16:9 wide shot", "Every morning in Lumina, a thousand clockwork hearts beat as one.", 40, "Ambient Walk Cycle", "Wide Crane Shot", "Golden Sunrise", "Walking with purpose", "cartoon_scene_scifi"),
            SceneItem(2, "Glitch in Core", "Intricate 3D cartoon mechanism with sparking emerald energy", "Deep within the Great Gear Chamber, Toby detected an impossible harmonic resonance.", 45, "Crouch & Tool Manipulate", "Over-The-Shoulder", "Emerald Lighting", "Adjusting levers", "cartoon_scene_scifi"),
            SceneItem(3, "Flight of Dragon", "Boy riding mechanical brass dragon through futuristic skies", "To reach the apex before total shutdown, they took the high altitude skyways!", 50, "Full Body Lean Physics", "Chase Camera", "Dramatic Clouds", "Steering reins in flight", "cartoon_scene_scifi")
        )
    }
}
