package com.techsavvy.tshostelmanagement.navigation

sealed class Screens(val route:String, val title : String = "") {
    object Login : Screens("login","Login")
    object Staff {
        object Home : Screens("staff_home","Staff Home")
    }
    object Admin {
        object Home : Screens("admin_home","Admin Home")
        object AddStaff : Screens("add_staff_admin", "Add Staff")
    }
    object Hosteler {
        object Home : Screens("hosteler_home","Hostel Home")
    }
}