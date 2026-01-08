package com.techsavvy.tshostelmanagement.navigation

sealed class Screens(val route: String, val title: String = "") {
    object Login : Screens("login", "Login")

    object Admin {
        object Home : Screens("admin_home", "Admin Home")
        object Infrastructure : Screens("admin_infrastructure", "Infrastructure")
        object Hostellers : Screens("admin_hostellers", "Hostellers")
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
        object EditFloor : Screens("admin_edit_floor", "Edit Floor") {
            fun createRoute(floorId: String) = "admin_edit_floor/$floorId"
        }
        object EditRoom : Screens("admin_edit_room", "Edit Room") {
            fun createRoute(roomId: String) = "admin_edit_room/$roomId"
        }
        object DetailsBlock : Screens("admin_details_block", "Block Details") {
            fun createRoute(blockId: String) = "admin_details_block/$blockId"
        }
        object DetailsFloor : Screens("admin_details_floor", "Floor Details") {
            fun createRoute(floorId: String) = "admin_details_floor/$floorId"
        }
        object DetailsRoom : Screens("admin_details_room", "Room Details") {
            fun createRoute(roomId: String) = "admin_details_room/$roomId"
        }
        object AssignHosteller : Screens("admin_assign_hosteller", "Assign Hosteller")
        object AddUser : Screens("admin_add_user", "Add User")

        object Staff : Screens("admin_staff", "Staff")
        object AddStaff : Screens("admin_add_staff", "Add Staff") // Optional alias for clarity
        object AssignTask : Screens("admin_assign_task", "Assign Task")
    }

    object Auth {
        object RegisterUser : Screens("register_user", "Register User")
    }

    object Complaints {
        object Home : Screens("complaints_home", "Complaints Home")
        }

    object Fees {
        object Home : Screens("fees_home", "Fees Home")
    }

    object Reports {
        object Home : Screens("reports_home", "Reports Home")
    }

    object Staff {
        object Home : Screens("staff_home", "Staff Home")
    }

    object Hosteler {
        object Home : Screens("hosteler_home", "Hostel Home")
    }
}