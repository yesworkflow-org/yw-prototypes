package org.yesworkflow.save;

import java.io.InputStream;

public interface IYwSerializer {
    String Serialize(Object object);
    InputStream SerializeToInputStream(Object object);
}
