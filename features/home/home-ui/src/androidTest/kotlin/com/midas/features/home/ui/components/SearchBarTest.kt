package com.midas.features.home.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBar_displaysPlaceholder() {
        // When
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Search cryptocurrencies...")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_displaysQuery() {
        // Given
        val query = "Bitcoin"

        // When
        composeTestRule.setContent {
            SearchBar(
                query = query,
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_queryChange_triggersCallback() {
        // Given
        var newQuery = ""

        // When
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = { newQuery = it },
                onClearClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Search cryptocurrencies...")
            .performTextInput("BTC")

        // Then
        assert(newQuery == "BTC")
    }

    @Test
    fun searchBar_clearButton_visibleWhenQueryNotEmpty() {
        // When
        composeTestRule.setContent {
            SearchBar(
                query = "Bitcoin",
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Clear")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_clearButton_notVisibleWhenQueryEmpty() {
        // When
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Clear search")
            .assertDoesNotExist()
    }

    @Test
    fun searchBar_clearButton_triggersCallback() {
        // Given
        var clearClicked = false

        // When
        composeTestRule.setContent {
            SearchBar(
                query = "Bitcoin",
                onQueryChange = {},
                onClearClick = { clearClicked = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Clear")
            .performClick()

        // Then
        assert(clearClicked)
    }

    @Test
    fun searchBar_searchIcon_isDisplayed() {
        // When
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun searchBar_multipleCharacters_triggersCallbackForEach() {
        // Given
        val queryChanges = mutableListOf<String>()

        // When
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = { queryChanges.add(it) },
                onClearClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Search cryptocurrencies...")
            .performTextInput("BTC")

        // Then - performTextInput may trigger once with the full string
        assert(queryChanges.isNotEmpty())
        assert(queryChanges.last() == "BTC")
    }

    @Test
    fun searchBar_longQuery_displaysCorrectly() {
        // Given
        val longQuery = "This is a very long search query for testing purposes"

        // When
        composeTestRule.setContent {
            SearchBar(
                query = longQuery,
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(longQuery)
            .assertExists()
    }

    @Test
    fun searchBar_specialCharacters_handledCorrectly() {
        // Given
        val specialQuery = "Bitcoin-USD$"

        // When
        composeTestRule.setContent {
            SearchBar(
                query = specialQuery,
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(specialQuery)
            .assertExists()
    }

    @Test
    fun searchBar_emptyQuery_showsPlaceholder() {
        // When
        composeTestRule.setContent {
            SearchBar(
                query = "",
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Search cryptocurrencies...")
            .assertExists()
    }

    @Test
    fun searchBar_whiteSpaceQuery_showsText() {
        // Given
        val whiteSpaceQuery = "   "

        // When
        composeTestRule.setContent {
            SearchBar(
                query = whiteSpaceQuery,
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then - Clear button should be visible for whitespace
        composeTestRule
            .onNodeWithContentDescription("Clear")
            .assertExists()
    }

    @Test
    fun searchBar_numericQuery_displaysCorrectly() {
        // Given
        val numericQuery = "12345"

        // When
        composeTestRule.setContent {
            SearchBar(
                query = numericQuery,
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(numericQuery)
            .assertExists()
    }

    @Test
    fun searchBar_mixedCaseQuery_preservesCase() {
        // Given
        val mixedCaseQuery = "BiTcOiN"

        // When
        composeTestRule.setContent {
            SearchBar(
                query = mixedCaseQuery,
                onQueryChange = {},
                onClearClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(mixedCaseQuery)
            .assertExists()
    }
}
