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

public class GenerateStairsFromBlock extends EditorActionBase {
    String STAIR_SUFFIX="_stairs";
    String STAIR_BLOCK_SUFFIX="_STAIRS";

    @SuppressWarnings("unused")
    public GenerateStairsFromBlock() {
        this(true);
    }

    public GenerateStairsFromBlock(boolean setupHandler) {
        super(null);
        if(setupHandler) {
            this.setupHandler(new EditorWriteActionHandler(true) {

                @Override
                public void executeWriteAction(Editor editor, @Nullable Caret caret, DataContext dataContext) {

                    String newRegistryName;
                    String newBlockName;
                    String newRegistryLine;

                    final SelectionModel selectionModel = editor.getSelectionModel();

                    selectionModel.selectLineAtCaret();
                    String selectedText = selectionModel.getSelectedText();
                    String registryName = ExtractRegistryNameFromLine(selectedText);
                    String blockName = ExtractBlockNameFromLine(selectedText);

                    if (registryName.isEmpty()) return;
                    newRegistryName = registryName + STAIR_SUFFIX;
                    newBlockName = blockName + STAIR_BLOCK_SUFFIX;

                    assert selectedText != null;
                    newRegistryLine = CreateNewRegistryLine(selectedText,registryName,newRegistryName,blockName,newBlockName);
                    selectedText += newRegistryLine;

                    editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(),selectedText);

                    String projectName = Objects.requireNonNull(editor.getProject()).getName().toLowerCase();
                    String baseResourcePath = editor.getProject().getBasePath();
                    baseResourcePath += "/src/main/resources/assets/" + projectName;
                    String baseDataPath = editor.getProject().getBasePath() + "/src/main/resources/data/"+ projectName;

                    //BlockState
                    String sourceFile ="STAIRS_TEMPLATE_BLOCKSTATE.json";
                    String destFile = baseResourcePath + "/blockstates/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //Main Model
                    sourceFile = "STAIRS_TEMPLATE_MODEL.json";
                    destFile = baseResourcePath + "/models/block/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //INNER STAIR MODEL
                    sourceFile = "STAIRS_TEMPLATE_MODEL_INNER.json";
                    destFile = baseResourcePath + "/models/block/" + newRegistryName + "_inner.json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //OUTER STAIR MODEL
                    sourceFile = "STAIRS_TEMPLATE_MODEL_OUTER.json";
                    destFile = baseResourcePath + "/models/block/" + newRegistryName + "_outer.json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //ITEM MODEL
                    sourceFile = "STAIRS_TEMPLATE_MODEL_ITEM.json";
                    destFile = baseResourcePath + "/models/item/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //DATA: LOOT TABLE
                    sourceFile = "STAIRS_TEMPLATE_LOOT_TABLE.json";
                    destFile = baseDataPath + "/loot_tables/blocks/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //DATA:  RECIPE
                    sourceFile = "STAIRS_TEMPLATE_RECIPE.json";
                    destFile = baseDataPath + "/recipes/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    VirtualFileManager.getInstance().syncRefresh();
                }
            });
        }
    }


}
