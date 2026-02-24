package com.peopleinnet.glcameraapp.viewmodel

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MainViewModel
 * Tests frame time tracking, metrics calculation, and state management
 */
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel()
    }

    @Test
    fun `initial state should have default values`() {
        assertEquals(0f, viewModel.frameTimeMs, 0.001f)
        assertEquals(0f, viewModel.avgFrameTimeMs, 0.001f)
        assertEquals(Float.MAX_VALUE, viewModel.minFrameTimeMs, 0.001f)
        assertEquals(0f, viewModel.maxFrameTimeMs, 0.001f)
        assertFalse(viewModel.showMetrics)
    }

    @Test
    fun `updateFrameTime should update current frame time`() {
        val testFrameTime = 16.67f

        viewModel.updateFrameTime(testFrameTime)

        assertEquals(testFrameTime, viewModel.frameTimeMs, 0.001f)
    }

    @Test
    fun `updateFrameTime should calculate average correctly`() {
        viewModel.updateFrameTime(10f)
        viewModel.updateFrameTime(20f)
        viewModel.updateFrameTime(30f)

        val expectedAvg = (10f + 20f + 30f) / 3f
        assertEquals(expectedAvg, viewModel.avgFrameTimeMs, 0.001f)
    }

    @Test
    fun `updateFrameTime should track minimum frame time`() {
        viewModel.updateFrameTime(20f)
        viewModel.updateFrameTime(10f)
        viewModel.updateFrameTime(30f)

        assertEquals(10f, viewModel.minFrameTimeMs, 0.001f)
    }

    @Test
    fun `updateFrameTime should track maximum frame time`() {
        viewModel.updateFrameTime(10f)
        viewModel.updateFrameTime(30f)
        viewModel.updateFrameTime(20f)

        assertEquals(30f, viewModel.maxFrameTimeMs, 0.001f)
    }

    @Test
    fun `updateFrameTime with multiple values should maintain correct statistics`() {
        val frameTimes = listOf(16.67f, 33.33f, 8.33f, 25f, 12.5f)

        frameTimes.forEach { viewModel.updateFrameTime(it) }

        assertEquals(12.5f, viewModel.frameTimeMs, 0.001f) // Last value
        assertEquals(8.33f, viewModel.minFrameTimeMs, 0.001f)
        assertEquals(33.33f, viewModel.maxFrameTimeMs, 0.001f)
        
        val expectedAvg = frameTimes.average().toFloat()
        assertEquals(expectedAvg, viewModel.avgFrameTimeMs, 0.001f)
    }

    @Test
    fun `toggleMetrics should change showMetrics state`() {
        assertFalse(viewModel.showMetrics)

        viewModel.toggleMetrics()
        assertTrue(viewModel.showMetrics)

        viewModel.toggleMetrics()
        assertFalse(viewModel.showMetrics)
    }

    @Test
    fun `toggleMetrics should reset statistics when enabled`() {
        // Set up some initial data
        viewModel.updateFrameTime(10f)
        viewModel.updateFrameTime(20f)
        viewModel.updateFrameTime(30f)

        // Toggle metrics on (should reset)
        viewModel.toggleMetrics()

        // Add new data
        viewModel.updateFrameTime(15f)

        // Check that statistics are based only on new data
        assertEquals(15f, viewModel.frameTimeMs, 0.001f)
        assertEquals(15f, viewModel.avgFrameTimeMs, 0.001f)
        assertEquals(15f, viewModel.minFrameTimeMs, 0.001f)
        assertEquals(15f, viewModel.maxFrameTimeMs, 0.001f)
    }

    @Test
    fun `msToFps should convert milliseconds to FPS correctly`() {
        // 16.67ms = 60 FPS
        val fps60 = viewModel.msToFps(16.67f)
        assertEquals(60f, fps60, 0.1f)

        // 33.33ms = 30 FPS
        val fps30 = viewModel.msToFps(33.33f)
        assertEquals(30f, fps30, 0.1f)

        // 8.33ms = 120 FPS
        val fps120 = viewModel.msToFps(8.33f)
        assertEquals(120f, fps120, 0.5f)
    }

    @Test
    fun `msToFps should return zero for zero milliseconds`() {
        val fps = viewModel.msToFps(0f)
        assertEquals(0f, fps, 0.001f)
    }

    @Test
    fun `msToFps should return zero for negative milliseconds`() {
        val fps = viewModel.msToFps(-10f)
        assertEquals(0f, fps, 0.001f)
    }

    @Test
    fun `msToFps should handle very small values`() {
        // 1ms = 1000 FPS
        val fps = viewModel.msToFps(1f)
        assertEquals(1000f, fps, 0.001f)
    }

    @Test
    fun `msToFps should handle very large values`() {
        // 1000ms = 1 FPS
        val fps = viewModel.msToFps(1000f)
        assertEquals(1f, fps, 0.001f)
    }

    @Test
    fun `consecutive toggleMetrics should reset statistics each time enabled`() {
        // First session
        viewModel.toggleMetrics() // Enable
        viewModel.updateFrameTime(10f)
        assertEquals(10f, viewModel.avgFrameTimeMs, 0.001f)

        viewModel.toggleMetrics() // Disable
        
        // Second session
        viewModel.toggleMetrics() // Enable (should reset)
        viewModel.updateFrameTime(20f)
        assertEquals(20f, viewModel.avgFrameTimeMs, 0.001f)
    }

    @Test
    fun `updateFrameTime should handle zero values`() {
        viewModel.updateFrameTime(0f)

        assertEquals(0f, viewModel.frameTimeMs, 0.001f)
        assertEquals(0f, viewModel.avgFrameTimeMs, 0.001f)
        assertEquals(0f, viewModel.minFrameTimeMs, 0.001f)
        assertEquals(0f, viewModel.maxFrameTimeMs, 0.001f)
    }

    @Test
    fun `updateFrameTime should handle very large values`() {
        val largeValue = 1000f

        viewModel.updateFrameTime(largeValue)

        assertEquals(largeValue, viewModel.frameTimeMs, 0.001f)
        assertEquals(largeValue, viewModel.avgFrameTimeMs, 0.001f)
        assertEquals(largeValue, viewModel.minFrameTimeMs, 0.001f)
        assertEquals(largeValue, viewModel.maxFrameTimeMs, 0.001f)
    }

    @Test
    fun `statistics should be independent across multiple sessions`() {
        // Session 1
        viewModel.toggleMetrics()
        viewModel.updateFrameTime(10f)
        viewModel.updateFrameTime(20f)
        val session1Avg = viewModel.avgFrameTimeMs
        
        viewModel.toggleMetrics() // Disable

        // Session 2
        viewModel.toggleMetrics() // Enable (reset)
        viewModel.updateFrameTime(30f)
        val session2Avg = viewModel.avgFrameTimeMs

        assertNotEquals(session1Avg, session2Avg)
        assertEquals(30f, session2Avg, 0.001f)
    }
}
