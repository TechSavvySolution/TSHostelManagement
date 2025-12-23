package com.techsavvy.tshostelmanagement

import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.techsavvy.tshostelmanagement.navigation.NavGraph
import com.techsavvy.tshostelmanagement.ui.theme.TSHostelManagementTheme

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TSHostelManagementTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
