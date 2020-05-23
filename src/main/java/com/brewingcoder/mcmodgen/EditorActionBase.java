package com.brewingcoder.mcmodgen;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

public abstract class EditorActionBase extends EditorAction {

    protected EditorActionBase(EditorActionHandler defaultHandler){
        super(defaultHandler);
    }

    protected Class getActionClass() {
        return getClass();
    }
}
