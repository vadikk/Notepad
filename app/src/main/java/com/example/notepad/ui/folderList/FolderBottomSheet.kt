package com.example.notepad.ui.folderList

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notepad.R
import com.example.notepad.domain.models.Folder
import com.example.notepad.ui.theme.NotepadTheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FoldersBottomSheet(
    folderList: List<FolderEntityContract>,
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    hideBottomSheet: () -> Unit,
    createNewFolder: () -> Unit,
    selectFolder: (uid: Int?) -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colors.background,
        scrimColor = MaterialTheme.colors.primary.copy(alpha = 0.7F),

        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background),
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.clickable { hideBottomSheet() }
                )
                Text(
                    text = stringResource(id = R.string.folders),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth()
                ){
                    items(folderList.size) {index ->
                        val isIdle = folderList[index] is FolderEntityContract.FolderIdle
                        val isSelected =
                            if (folderList[index] is FolderEntityContract.FolderIdle) false
                            else (folderList[index] as? FolderEntityContract.FolderItem)?.folder?.isSelected ?: false
                        val folderName =
                            if (folderList[index] is FolderEntityContract.FolderIdle) stringResource(id = R.string.new_folder)
                            else (folderList[index] as? FolderEntityContract.FolderItem)?.folder?.title.orEmpty()
                        val noteCount =
                            if (folderList[index] is FolderEntityContract.FolderIdle) ""
                            else (folderList[index] as? FolderEntityContract.FolderItem)?.folder?.countNote?.toString().orEmpty()

                        FolderItem(
                            folderName = folderName,
                            noteCount = noteCount,
                            isIdle = isIdle,
                            isSelected = isSelected,
                            operations = {
                                if (isIdle) createNewFolder()
                                else selectFolder(
                                    (folderList[index] as? FolderEntityContract.FolderItem)?.folder?.uid
                                )
                            }
                        )
                    }
                }
            }
        }
    ) {}
}

@Composable
fun FolderItem(
    folderName: String,
    noteCount: String,
    isIdle: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    operations: () -> Unit
) {
    val borderModifier =
        if (isSelected) Modifier.border(
            width = 3.dp,
            color = MaterialTheme.colors.onPrimary,
            shape = RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp)
        ) else Modifier

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(90.dp, 120.dp)
                .clip(RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp))
                .background(MaterialTheme.colors.primary)
                .clickable { operations() }
                .then(borderModifier),
            contentAlignment = Alignment.Center
        ){
            if (isIdle) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = folderName,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = noteCount,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.primary
        )
    }
}

@Preview
@Composable
fun FolderItemPreview() {
    NotepadTheme {
        FolderItem(
            folderName = "All notes",
            noteCount = "2",
            isIdle = true,
            isSelected = false,
            operations = {}
        )
    }
}