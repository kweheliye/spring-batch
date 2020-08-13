package com.mashreq.wealth.batch.reader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

public class EndOfMonthStatementDirectoryItemReaderTest {

    private DirectoryItemReader reader;

    private final String directory = "file:".concat(System.getProperty("user.dir")).concat("/input/statements/*.pdf");
    private final String emptyDirectory = "file:".concat(System.getProperty("user.dir")).concat("/input/empty-statements/*.pdf");


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_NoFileIsReturned_IfDirectoryIsEmpty() throws IOException {
        reader = new DirectoryItemReader(emptyDirectory);
        File file = reader.read();
        Assert.assertNull(file);
        //We should have zero of files
        Assert.assertEquals(0, reader.getFoundFiles().size());
    }

    //@Test
    public void test_ListOfFilesReturned_WhenDirectoryContainsFiles() throws IOException {
        reader = new DirectoryItemReader(directory);
        //We should have two files
        Assert.assertEquals(2, reader.getFoundFiles().size());


        //After polling or reading a file, the Queue should have only  one file
        File file1 = reader.read();
        Assert.assertEquals(1, reader.getFoundFiles().size());
        Assert.assertTrue(file1.exists());

        //After polling or reading a file, the Queue should have only  one file
        File file2 = reader.read();
        Assert.assertEquals(0, reader.getFoundFiles().size());
        Assert.assertTrue(file2.exists());

    }
}