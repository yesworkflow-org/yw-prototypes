package org.yesworkflow.recon;

import java.io.File;
import java.nio.file.Path;

import org.yesworkflow.model.Model;

public class Run {

    public final Model model;
    public final Path runDirectoryBase;
    
    public Run(Model model, Path base) {
        this.model = model;
        this.runDirectoryBase = base;
    }

    public Run(Model model, String base) {
        this(model, new File(base).toPath());
    }

    public Run(Model model) {
        this(model, "");
    }
}
