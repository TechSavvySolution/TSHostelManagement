package com.techsavvy.tshostelmanagement.navigation

sealed class Screens(val route: String, val title: String = "") {
    object Splash : Screens("splash", "Splash")
    object Login : Screens("login", "Login")

    object Admin {
        object Announcements : Screens("admin_announcements", "Announcements")
        object AddAnnouncement : Screens("admin_add_announcement", "Add Announcement")
        object EditAnnouncement : Screens("admin_edit_announcement", "Edit Announcement") {
            fun createRoute(id: String) = "admin_edit_announcement/$id"
        }
        object Home : Screens("admin_home", "Admin Home")
        object Infrastructure : Screens("admin_infrastructure", "Infrastructure")
        object Hostellers : Screens("admin_hostellers", "Hostellers")
        object Complaints : Screens("admin_complaints", "Complaints")
        object Fees : Screens("admin_fees", "Fees")
        object Reports : Screens("admin_reports", "Reports")
        object Settings : Screens("admin_settings", "Settings")
        object Profile : Screens("admin_profile", "Profile")
        object About : Screens("admin_about", "About Us")
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
        object EditUser : Screens("admin_edit_user", "Edit User") {
            fun createRoute(uid: String) = "admin_edit_user/$uid"
        }

        object Staff : Screens("admin_staff", "Staff")
        object AddStaff : Screens("admin_add_staff", "Add Staff") // Optional alias for clarity
        object EditStaff : Screens("admin_edit_staff", "Edit Staff") {
            fun createRoute(uid: String) = "admin_edit_staff/$uid"
        }
        object AssignTask : Screens("admin_assign_task", "Assign Task")
        object MessMenu : Screens("admin_mess_menu", "Mess Menu")
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
        object Complaints : Screens("staff_complaints", "My Tasks")
        object ComplaintDetails : Screens("staff_complaint_details", "Complaint Details") {
            fun createRoute(complaintId: String) = "staff_complaint_details/$complaintId"
        }
        object Chat : Screens("staff_chat", "Chat") {
            fun createRoute(complaintId: String) = "staff_chat/$complaintId"
        }
        // ADD THIS:
        object Profile : Screens("staff_profile", "My Profile")
        object Settings : Screens("staff_settings", "Settings")
        object About : Screens("staff_about", "About Us")
    }

    object Hosteler {
        object Announcements : Screens("hosteler_announcements", "Announcements")
        object Home : Screens("hosteler_home", "Hostel Home")
        object Complaints : Screens("hosteler_complaints", "My Complaints")
        object ComplaintDetail : Screens("hosteler_complaint_detail", "Complaint Detail") {
            fun createRoute(complaintId: String) = "hosteler_complaint_detail/$complaintId"
        }
        object Chat : Screens("hosteler_chat", "Chat") {
            fun createRoute(complaintId: String) = "hosteler_chat/$complaintId"
        }
        object RaiseComplaint : Screens("raise_complaint", "Raise Complaint")
        object Profile : Screens("hosteler_profile", "My Profile")
        object Settings : Screens("hosteler_settings", "Settings")
        object Fees : Screens("hosteler_fees", "My Fees")
        object About : Screens("hosteler_about", "About Us")
        object Roommates : Screens("hosteler_roommates", "My Room")
    }
}

