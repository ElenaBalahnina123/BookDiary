package com.elena_balakhnina.bookdiary

import android.graphics.Bitmap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.elena_balakhnina.bookdiary.edit.EditElement
import com.elena_balakhnina.bookdiary.edit.EditElementViewModel

@Composable
fun BookEditor(navController: NavController) {
    val viewModel = hiltViewModel<EditElementViewModel>()

    val pickFromGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        viewModel.onImageFromGalleryPicked(activityResult)
    }

    val pickFromCameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview(),
        ) {
            if (it != null) {
                viewModel.onPhotoPicturePreviewReady(it)
            }
        }

    val screenData by viewModel.uiFlow.collectAsState()

    EditElement(
        onSaveClick = { viewModel.saveClick(navController) },
        onTitleChange = viewModel::onTitleChange,
        onAuthorChange = viewModel::onAuthorChange,
        onClickGallery = { viewModel.onPickImageFromGallery(pickFromGalleryLauncher) },
        onClickCamera = { viewModel.onPickImageFromCamera(pickFromCameraLauncher) },
        onDescriptionChange = viewModel::onDescriptionChange,
        onGenreChange = viewModel::onGenreSelected,
        onRatingChanged = viewModel::onRatingSelected,
        onDateChanged = viewModel::onDateChanged,
        onPopBackStack = { navController.popBackStack() },
        data = screenData
    )
}