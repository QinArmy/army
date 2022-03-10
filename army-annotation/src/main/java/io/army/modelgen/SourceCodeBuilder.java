package io.army.modelgen;

import io.army.lang.Nullable;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

final class SourceCodeBuilder {

    private final Filer filer;

    private String className;

    private StringBuilder builder;

    SourceCodeBuilder(Filer filer) {
        this.filer = filer;
    }

    SourceCodeBuilder reset(TypeElement tableElement, Map<String, VariableElement> fieldMap, @Nullable TypeElement parentElement) {
        return this;
    }

    void build() throws IOException {
        final FileObject fileObject;
        fileObject = this.filer.createSourceFile(this.className + _MetaBridge.META_CLASS_NAME_SUFFIX);
        try (PrintWriter pw = new PrintWriter(fileObject.openOutputStream())) {
            pw.println(this.builder.toString());
        }

    }

}
