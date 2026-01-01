package com.techsavvy.tshostelmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.techsavvy.tshostelmanagement.ui.nav.NavGraph
import com.techsavvy.tshostelmanagement.ui.theme.TSHostelManagementTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
