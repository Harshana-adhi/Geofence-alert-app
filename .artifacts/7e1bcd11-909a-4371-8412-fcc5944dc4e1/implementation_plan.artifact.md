# Implementation Plan - UI Layer for Geofence Demo App

This plan outlines the changes needed to set up the UI for the geofencing demo app, as requested by "Member 2". The changes focus on the layout, string resources, and basic activity logic without implementing geofencing or permission handling.

## User Review Required

> [!IMPORTANT]
> The implementation will use stub logic and TODO comments as requested. No actual geofencing functionality will be added.

## Proposed Changes

### Resources

#### [MODIFY] [strings.xml](file:///C:/Users/user/StudioProjects/Geofence-alert-app/app/src/main/res/values/strings.xml)
- Add the following string resources:
    - `geofence_info_placeholder`
    - `status_not_monitoring`
    - `start_monitoring`
    - `log_placeholder`

#### [MODIFY] [activity_main.xml](file:///C:/Users/user/StudioProjects/Geofence-alert-app/app/src/main/res/layout/activity_main.xml)
- Replace the existing content with a new `ConstraintLayout` structure:
    - `tvGeofenceInfo` (TextView): Top-aligned, bold, 16sp.
    - `tvStatus` (TextView): Below `tvGeofenceInfo`.
    - `btnStartMonitoring` (Button): Below `tvStatus`.
    - `ScrollView`: Below the button, filling the remaining space.
    - `tvLog` (TextView): Monospace, 13sp, inside the `ScrollView`.

### UI Logic

#### [MODIFY] [MainActivity.kt](file:///C:/Users/user/StudioProjects/Geofence-alert-app/app/src/main/java/com/example/geofence_app/MainActivity.kt)
- Initialize view references.
- Implement `btnStartMonitoring` click listener.
- Add `startMonitoring()` function with stub logic and requested TODOs.
- Add `appendLog(message: String)` helper function.
- Maintain existing edge-to-edge configuration.

## Verification Plan

### Automated Tests
- N/A (UI-only stub implementation)

### Manual Verification
- Verify the layout renders correctly in the Android Studio Layout Editor.
- Verify that clicking "Start Monitoring" updates the status text and appends a log entry.
