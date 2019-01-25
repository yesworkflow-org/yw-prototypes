package org.yesworkflow.save;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.mockito.junit.MockitoJUnitRunner;
import org.yesworkflow.YesWorkflowTestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@RunWith(MockitoJUnitRunner.class)
public class TestHash extends YesWorkflowTestCase 
{
    @Test
    public void testURIHash() throws NoSuchAlgorithmException, IOException {
        URI uri =  URI.create("examples/clean_name_date/date_val_log.txt");

        Hash hash = new Hash("md5");
        String test = hash.getHash(uri);
        assertEquals(test, "9e1de71fbdbb8c680f5729360cf220c7");
    }

    @Test
    public void testPathHash() throws NoSuchAlgorithmException, IOException {
        Path path = Paths.get("." ,"examples/clean_name_date/date_val_log.txt");

        Hash hash = new Hash("md5");
        String test = hash.getHash(path);
        assertEquals(test, "9e1de71fbdbb8c680f5729360cf220c7");
    }

    @Test
    public void testStringPathHash() throws NoSuchAlgorithmException, IOException {
        String path = "examples/clean_name_date/date_val_log.txt";

        Hash hash = new Hash("md5");
        String test = hash.getHash(path);
        assertEquals(test, "9e1de71fbdbb8c680f5729360cf220c7");
    }

    @Test
    public void testStringHash() {
        String testString = "This is a string used to test String Hashing.";

        String hashedString = Hash.getStringHash(testString);
        assertEquals(hashedString, "1213258105");

    }
}