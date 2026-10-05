/*
 * Car Browser — GPLv3 derivative. See LICENSE.
 */

package com.kododake.aabrowser.car

import android.car.Car
import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread

/**
 * Registers with [CarUxRestrictionsManager] on AAOS so the platform treats Car Browser as a
 * distraction-optimized app while driving (required to stay launchable when moving).
 */
class CarDrivingUxMonitor(
    private val context: Context,
    private val onStateChanged: (CarDrivingUiState) -> Unit,
) {
    private var car: Car? = null
    private var uxRestrictionsManager: CarUxRestrictionsManager? = null

    private val listener = object : CarUxRestrictionsManager.OnUxRestrictionsChangedListener {
        override fun onUxRestrictionsChanged(restrictions: CarUxRestrictions) {
            deliver(restrictions)
        }
    }

    @MainThread
    fun start() {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            Log.d(TAG, "Not an automotive device; skipping Car UX monitor")
            return
        }
        try {
            val handler = Handler(Looper.getMainLooper())
            car = Car.createCar(
                context,
                handler,
                Car.CAR_WAIT_TIMEOUT_DO_NOT_WAIT,
            ) { _, ready ->
                if (!ready) {
                    Log.w(TAG, "Car service not ready")
                    return@createCar
                }
                val carInstance = car ?: return@createCar
                val manager = carInstance.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE)
                if (manager !is CarUxRestrictionsManager) {
                    Log.w(TAG, "UX restrictions manager unavailable")
                    return@createCar
                }
                uxRestrictionsManager = manager
                manager.registerListener(listener)
                deliver(manager.currentCarUxRestrictions)
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Car UX monitor permission denied", e)
        } catch (e: Exception) {
            Log.w(TAG, "Car UX monitor failed to start", e)
        }
    }

    @MainThread
    fun stop() {
        try {
            uxRestrictionsManager?.unregisterListener()
        } catch (_: Exception) {
            // ignore
        }
        uxRestrictionsManager = null
        try {
            car?.disconnect()
        } catch (_: Exception) {
            // ignore
        }
        car = null
    }

    private fun deliver(restrictions: CarUxRestrictions) {
        val requiresDo = restrictions.isRequiresDistractionOptimization
        val noKeyboard = requiresDo &&
            (restrictions.activeRestrictions and CarUxRestrictions.UX_RESTRICTIONS_NO_KEYBOARD) != 0
        val state = CarDrivingUiState(
            requiresDistractionOptimization = requiresDo,
            blockKeyboard = noKeyboard,
            activeRestrictions = restrictions.activeRestrictions,
        )
        CarDrivingUxRegistry.update(state)
        onStateChanged(state)
    }

    companion object {
        private const val TAG = "CarDrivingUxMonitor"
    }
}
