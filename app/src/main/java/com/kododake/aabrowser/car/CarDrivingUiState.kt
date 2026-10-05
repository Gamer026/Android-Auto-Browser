/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.car

import androidx.compose.runtime.mutableStateOf

data class CarDrivingUiState(
    val requiresDistractionOptimization: Boolean = false,
    val blockKeyboard: Boolean = false,
    val activeRestrictions: Int = 0,
) {
    val isDrivingLimited: Boolean
        get() = requiresDistractionOptimization
}

object CarDrivingUxRegistry {
    val drivingLimited = mutableStateOf(false)
    val keyboardBlocked = mutableStateOf(false)

    fun update(state: CarDrivingUiState) {
        drivingLimited.value = state.isDrivingLimited
        keyboardBlocked.value = state.blockKeyboard
    }

    fun reset() {
        drivingLimited.value = false
        keyboardBlocked.value = false
    }
}
