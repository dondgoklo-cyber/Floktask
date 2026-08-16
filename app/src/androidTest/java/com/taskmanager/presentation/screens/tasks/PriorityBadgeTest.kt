package com.taskmanager.presentation.screens.tasks

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.taskmanager.domain.model.Priority
import com.taskmanager.presentation.components.PriorityBadge
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI test for a critical rendering flow (issue 47: no UI tests).
 * Verifies the priority badge displays the correct P1/P2/P3 label.
 */
@RunWith(AndroidJUnit4::class)
class PriorityBadgeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun highPriorityShowsLabel() {
        composeRule.setContent {
            MaterialTheme { Surface { PriorityBadge(Priority.HIGH) } }
        }
        composeRule.onNodeWithText("HIGH").assertIsDisplayed()
    }

    @Test
    fun mediumPriorityShowsLabel() {
        composeRule.setContent {
            MaterialTheme { Surface { PriorityBadge(Priority.MEDIUM) } }
        }
        composeRule.onNodeWithText("MEDIUM").assertIsDisplayed()
    }

    @Test
    fun lowPriorityShowsLabel() {
        composeRule.setContent {
            MaterialTheme { Surface { PriorityBadge(Priority.LOW) } }
        }
        composeRule.onNodeWithText("LOW").assertIsDisplayed()
    }
}
