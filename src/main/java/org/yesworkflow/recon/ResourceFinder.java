package org.yesworkflow.recon;

import java.nio.file.Path;
import java.util.Collection;
import org.yesworkflow.data.UriTemplate;

public interface ResourceFinder {
    Collection<String> findResources(Path baseDirectory, UriTemplate uriTemplate);
}
