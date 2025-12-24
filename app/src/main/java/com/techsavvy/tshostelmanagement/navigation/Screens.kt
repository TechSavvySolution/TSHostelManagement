package com.techsavvy.tshostelmanagement.navigation

sealed class Screens(val route:String, val title : String = "") {
    object Login : Screens("login","Login")
    object Staff {
        object Home : Screens("staff_home","Staff Home")
    }
    object Admin {
        object Home : Screens("admin_home","Admin Home")
        object Blocks : Screens("admin_blocks", "Blocks")
        object Floors : Screens("admin_floors", "Floors")
        object Rooms : Screens("admin_rooms", "Rooms")
        object Hostellers : Screens("admin_hostellers", "Hostellers")
        object Staff : Screens("admin_staff", "Staff")
        object Settings : Screens("admin_settings", "Settings")
        object AddBlock : Screens("admin_add_block", "Add Block")
        object AddFloor : Screens("admin_add_floor", "Add Floor")
        object AddRoom : Screens("admin_add_room", "Add Room")
    }
    object Hosteler {
        object Home : Screens("hosteler_home","Hostel Home")
    }
}