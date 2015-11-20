package org.yesworkflow.recon;

import java.util.Collection;
import org.yesworkflow.data.UriTemplate;

public interface ResourceFinder {
    Collection<String> findResources(String baseUri, UriTemplate uriTemplate);
}
