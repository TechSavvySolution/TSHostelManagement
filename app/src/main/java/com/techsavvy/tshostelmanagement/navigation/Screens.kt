package com.techsavvy.tshostelmanagement.navigation

sealed class Screens(val route: String, val title: String = "") {
    object Login : Screens("login", "Login")

    object Admin {
        object Home : Screens("admin_home", "Admin Home")
        object Infrastructure : Screens("admin_infrastructure", "Infrastructure")
        object Hostellers : Screens("admin_hostellers", "Hostellers")
        object Staff : Screens("admin_staff", "Staff")
        object Complaints : Screens("admin_complaints", "Complaints")
        object Fees : Screens("admin_fees", "Fees")
        object Reports : Screens("admin_reports", "Reports")
        object Settings : Screens("admin_settings", "Settings")
        object Profile : Screens("admin_profile", "Profile")
        object Notifications : Screens("admin_notifications", "Notifications")
        object AddBlock : Screens("admin_add_block", "Add Block")
        object AddFloor : Screens("admin_add_floor", "Add Floor")
        object AddRoom : Screens("admin_add_room", "Add Room")
        object EditBlock : Screens("admin_edit_block", "Edit Block") {
            fun createRoute(blockId: String) = "admin_edit_block/$blockId"
        }
        object EditFloor : Screens("admin_edit_floor/{floorId}", "Edit Floor") {
            fun createRoute(floorId: String) = "admin_edit_floor/$floorId"
        }
        object EditRoom : Screens("admin_edit_room/{roomId}", "Edit Room") {
            fun createRoute(roomId: String) = "admin_edit_room/$roomId"
        }
    }

    object Staff {
        object Home : Screens("staff_home", "Staff Home")
    }

    object Hosteler {
        object Home : Screens("hosteler_home", "Hostel Home")
    }
}