package dev.fabled.nowted.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenContent
import dev.fabled.nowted.presentation.ui.screens.home.HomeScreenState
import dev.fabled.nowted.presentation.ui.theme.NowtedTheme
import dev.fabled.nowted.presentation.utils.TestTags
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupHomeTests() {
        composeTestRule.setContent {
            NowtedTheme {
                HomeScreenContent(
                    modifier = Modifier.fillMaxSize(),
                    homeScreenState = HomeScreenState(),
                    onScreenEvent = {}
                )
            }
        }
    }

    @Test
    fun checkIfBaseElementsDisplayed() {
        composeTestRule.onNodeWithText(text = "Nowted").assertIsDisplayed()
        composeTestRule.onNodeWithTag(testTag = TestTags.ICONS_SEARCH).assertIsDisplayed()
    }

    @Test
    fun checkIfSearchIconTogglesSearchTExtField() {
        composeTestRule.onNodeWithTag(testTag = TestTags.BUTTON_NEW_NOTE).assertExists()
        composeTestRule.onNodeWithTag(testTag = TestTags.BUTTON_NEW_NOTE).assertIsDisplayed()

        composeTestRule.onNodeWithTag(testTag = TestTags.TEXT_FIELD_SEARCH).assertDoesNotExist()

        composeTestRule.onNodeWithTag(testTag = TestTags.ICONS_SEARCH).performClick()

        composeTestRule.onNodeWithTag(testTag = TestTags.TEXT_FIELD_SEARCH).assertExists()
        composeTestRule.onNodeWithTag(testTag = TestTags.TEXT_FIELD_SEARCH).assertIsDisplayed()

        composeTestRule.onNodeWithTag(testTag = TestTags.BUTTON_NEW_NOTE).assertDoesNotExist()
    }

}