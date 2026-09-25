package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SceneItem
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DeepMotionPreview(
    scene: SceneItem?,
    aspectRatioString: String = "16:9",
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showWireframe by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "DeepMotionLoop")
    val characterBounce by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (700 / playbackSpeed).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bounce"
    )

    val limbRotation by infiniteTransition.animateFloat(
        initialValue = -0.35f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (600 / playbackSpeed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Limb"
    )

    val particleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Particles"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("deep_motion_preview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Animation,
                        contentDescription = "DeepMotion",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DeepMotion 3D Cartoon Engine",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isPlaying) "RENDER: 60 FPS" else "PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Animation Viewport
            val containerHeight = if (aspectRatioString == "9:16") 280.dp else 210.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(containerHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
            ) {
                // Background visual render
                Image(
                    painter = painterResource(id = R.drawable.cartoon_scene_scifi),
                    contentDescription = "3D Cartoon Visual Render",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Atmospheric cinematic gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                // 3D Skeletal Rigging / Physics overlay simulation
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width * 0.5f
                    val centerY = size.height * 0.55f + (if (isPlaying) characterBounce else 0f)

                    if (showWireframe) {
                        // Draw animated 3D character skeleton joints
                        val cyan = Color(0xFF00E5FF)
                        val headRadius = 24f
                        val headY = centerY - 65f

                        // Head
                        drawCircle(
                            color = cyan,
                            radius = headRadius,
                            center = Offset(centerX, headY),
                            style = Stroke(width = 3f)
                        )
                        // Spine
                        drawLine(
                            color = cyan,
                            start = Offset(centerX, headY + headRadius),
                            end = Offset(centerX, centerY + 15f),
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                        // Shoulders & Arms
                        val armAngle = if (isPlaying) limbRotation else 0f
                        val leftHand = Offset(centerX - 45f + armAngle * 25f, centerY - 10f - armAngle * 20f)
                        val rightHand = Offset(centerX + 45f - armAngle * 25f, centerY - 10f + armAngle * 20f)
                        drawLine(color = cyan, start = Offset(centerX, centerY - 25f), end = leftHand, strokeWidth = 3f)
                        drawLine(color = cyan, start = Offset(centerX, centerY - 25f), end = rightHand, strokeWidth = 3f)
                        drawCircle(color = Color.Yellow, radius = 5f, center = leftHand)
                        drawCircle(color = Color.Yellow, radius = 5f, center = rightHand)

                        // Legs & Feet
                        val leftFoot = Offset(centerX - 30f - armAngle * 20f, centerY + 70f + armAngle * 10f)
                        val rightFoot = Offset(centerX + 30f + armAngle * 20f, centerY + 70f - armAngle * 10f)
                        drawLine(color = cyan, start = Offset(centerX, centerY + 15f), end = leftFoot, strokeWidth = 3.5f)
                        drawLine(color = cyan, start = Offset(centerX, centerY + 15f), end = rightFoot, strokeWidth = 3.5f)
                        drawCircle(color = Color.Yellow, radius = 5f, center = leftFoot)
                        drawCircle(color = Color.Yellow, radius = 5f, center = rightFoot)
                    }

                    // Floating 3D magic particles
                    for (i in 0 until 6) {
                        val angle = (particleRotation + i * 60) * (Math.PI / 180.0)
                        val dist = 110f + (i % 3) * 15f
                        val px = centerX + (cos(angle) * dist).toFloat()
                        val py = (centerY - 20f) + (sin(angle) * (dist * 0.45f)).toFloat()
                        drawCircle(
                            color = Color(0xFFFFD700).copy(alpha = 0.85f),
                            radius = 3.5f,
                            center = Offset(px, py)
                        )
                    }
                }

                // Overlay Metadata Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(
                            text = "Rig: Stylized Cartoon Biped v4.2",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF22D3EE),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Motion: ${scene?.motionStyle ?: "Dynamic Physics Bounce"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Scene Title Bar at bottom
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = scene?.title ?: "Scene 1: 3D Cartoon Hero Awakening",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Text(
                            text = "${scene?.durationSeconds ?: 15}s • ${aspectRatioString}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("deep_motion_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { showWireframe = !showWireframe },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Toggle Rig Skeleton",
                            tint = if (showWireframe) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            playbackSpeed = when (playbackSpeed) {
                                1.0f -> 1.5f
                                1.5f -> 2.0f
                                else -> 1.0f
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${playbackSpeed}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "Cam: ${scene?.cameraAngle ?: "Dynamic Orbit"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
