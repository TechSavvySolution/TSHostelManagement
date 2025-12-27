package com.techsavvy.tshostelmanagement.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.techsavvy.tshostelmanagement.ui.hostel.HostelViewModel

@Composable
fun AddBlockScreen(viewModel: HostelViewModel = hiltViewModel()) {
    var blockName by remember { mutableStateOf("") }
    var blockAlias by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = blockName,
            onValueChange = { blockName = it },
            label = { Text("Block Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = blockAlias,
            onValueChange = { blockAlias = it },
            label = { Text("Block Alias (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.addBlock(blockName, blockAlias.ifBlank { null })
                blockName = ""
                blockAlias = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Block")
        }
    }
}
