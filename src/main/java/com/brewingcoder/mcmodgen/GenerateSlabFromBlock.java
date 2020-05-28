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

public class GenerateSlabFromBlock extends EditorActionBase {

    String SLAB_SUFFIX="_slab";
    String SLAB_BLOCK_SUFFIX="_SLAB";

    @SuppressWarnings("unused")
    public GenerateSlabFromBlock() {
        this(true);
    }

    public GenerateSlabFromBlock(boolean setupHandler) {
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
                    newRegistryName = registryName + SLAB_SUFFIX;
                    newBlockName = blockName + SLAB_BLOCK_SUFFIX;

                    assert selectedText != null;
                    newRegistryLine = CreateNewRegistryLine(selectedText,registryName,newRegistryName,blockName,newBlockName);
                    selectedText += newRegistryLine;

                    editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(),selectedText);

                    String projectName = Objects.requireNonNull(editor.getProject()).getName().toLowerCase();
                    String baseResourcePath = editor.getProject().getBasePath();
                    baseResourcePath += "/src/main/resources/assets/" + projectName;
                    String baseDataPath = editor.getProject().getBasePath() + "/src/main/resources/data/"+ projectName;

                    //BlockState
                    String sourceFile ="SLAB_TEMPLATE_BLOCKSTATE.json";
                    String destFile = baseResourcePath + "/blockstates/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //Main Model
                    sourceFile = "SLAB_TEMPLATE_MODEL.json";
                    destFile = baseResourcePath + "/models/block/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //TOP SLAB MODEL
                    sourceFile = "SLAB_TEMPLATE_MODEL_TOP.json";
                    destFile = baseResourcePath + "/models/block/" + newRegistryName + "_top.json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //ITEM MODEL
                    sourceFile = "SLAB_TEMPLATE_MODEL_ITEM.json";
                    destFile = baseResourcePath + "/models/item/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //DATA: LOOT TABLE
                    sourceFile = "SLAB_TEMPLATE_LOOT_TABLE.json";
                    destFile = baseDataPath + "/loot_tables/blocks/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    //DATA:  RECIPE
                    sourceFile = "SLAB_TEMPLATE_RECIPE.json";
                    destFile = baseDataPath + "/recipes/" + newRegistryName + ".json";
                    MakeProjectFile(sourceFile,destFile,projectName,registryName,newRegistryName);

                    VirtualFileManager.getInstance().syncRefresh();
                }
            });
        }
    }
}
