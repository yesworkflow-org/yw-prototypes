package org.yesworkflow.save;

import org.junit.Test;
import org.junit.Assert;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.save.data.TestData;
import org.yesworkflow.save.data.TestDto;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class TestSaveUtility extends YesWorkflowTestCase
{
    @Test
    public void testJSONSerializer_serialize()
    {
        IYwSerializer serializer = new JSONSerializer();

        TestDto testDto = new TestDto("first", "second", "third");
        String expectedOutput = TestData.testDtoJson;
        String actualOutput = serializer.Serialize(testDto);

        Assert.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testJSONSerializer_deserialize()
    {
        IYwSerializer serializer = new JSONSerializer();

        TestDto actual = serializer.Deserialize(TestData.testDtoJson, TestDto.class);
        TestDto expected = new TestDto("first", "second", "third");

        Assert.assertEquals(expected.one, actual.one);
        Assert.assertEquals(expected.two, actual.two);
        Assert.assertEquals(expected.three, actual.three);
    }

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
