package com.elena_balakhnina.bookdiary

import android.graphics.Bitmap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
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

    EditElement(
//        navController = navController,
        onSaveClick = { viewModel.saveClick(navController) },
        bookTitleFlow = viewModel.bookTitleFlow(),
        onTitleChange = viewModel::onTitleChange,
        authorFlow = viewModel.authorFlow(),
        onAuthorChange = viewModel::onAuthorChange,
        onClickGallery = { viewModel.onPickImageFromGallery(pickFromGalleryLauncher) },
        onClickCamera = { viewModel.onPickImageFromCamera(pickFromCameraLauncher) },
        descriptionFlow = viewModel.descriptionFlow(),
        onDescriptionChange = viewModel::onDescriptionChange,
        selectedGenreIndexFlow = viewModel.genreFlow(),
        onGenreChange = viewModel::onGenreSelected,
        ratingFlow = viewModel.ratingFlow(),
        onRatingChanged = viewModel::onRatingSelected,
        dateFlow = viewModel.dateFlow(),
        onDateChanged = viewModel::onDateChanged,
        imageFlow = viewModel.imageFlow(),
        allowRate = viewModel.rateMode,
        onPopBackStack = { navController.popBackStack() }
    )
}