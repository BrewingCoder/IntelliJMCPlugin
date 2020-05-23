package com.brewingcoder.mcmodgen;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.brewingcoder.mcmodgen.Helpers.*;
import static com.brewingcoder.mcmodgen.Helpers.MakeProjectFile;

public class GenerateDataForBlock extends EditorActionBase {


    @SuppressWarnings("unused")
    public GenerateDataForBlock() {
        this(true);
    }

    public GenerateDataForBlock(boolean setupHandler) {
        super(null);
        if(setupHandler) {
            this.setupHandler(new EditorWriteActionHandler(true) {

                @Override
                public void executeWriteAction(Editor editor, @Nullable Caret caret, DataContext dataContext) {

                    final SelectionModel selectionModel = editor.getSelectionModel();

                    selectionModel.selectLineAtCaret();
                    String selectedText = selectionModel.getSelectedText();
                    String registryName = ExtractRegistryNameFromLine(selectedText);
                    String blockName = ExtractBlockNameFromLine(selectedText);

                    if (registryName.isEmpty()) return;

                    assert selectedText != null;

                    String projectName = Objects.requireNonNull(editor.getProject()).getName().toLowerCase();
                    String baseResourcePath = editor.getProject().getBasePath();
                    baseResourcePath += "/src/main/resources/assets/" + projectName;
                    String baseDataPath = editor.getProject().getBasePath() + "/src/main/resources/data/"+ projectName;

                    //BlockState
                    String sourceFile ="BLOCK_TEMPLATE_BLOCKSTATE.json";
                    String destFile = baseResourcePath + "/blockstates/" + registryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,registryName);

                    //Model
                    sourceFile = "BLOCK_TEMPLATE_MODEL.json";
                    destFile = baseResourcePath + "/models/block/" + registryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,registryName);

                    //ITEM MODEL
                    sourceFile = "BLOCK_TEMPLATE_MODEL_ITEM.json";
                    destFile = baseResourcePath + "/models/item/" + registryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,registryName);

                    //DATA: LOOT TABLE
                    sourceFile = "SLAB_TEMPLATE_LOOT_TABLE.json";
                    destFile = baseDataPath + "/loot_tables/blocks/" + registryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,registryName);

                    VirtualFileManager.getInstance().syncRefresh();
                }
            });
        }
    }
}

