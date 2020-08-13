package com.mashreq.wealth.batch.processor;

import com.mashreq.wealth.entity.Statement;
import com.mashreq.wealth.enums.FileNameStatus;
import com.mashreq.wealth.enums.ProcessStatus;
import com.mashreq.wealth.service.StatementService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class EndOfMonthStatementItemProcessorTest {

    @TempDir
    public Path tempDir;

    private StatementItemProcessor processor;
    private StatementService endOfMonthStatementServiceMock;
    private Path validFile = null;
    private Path badFile = null;
    private String validFilename = "201912311200_12333_44331_1.pdf";
    private String badFilename = "201912311200_12333_q.pdf";


    @BeforeEach
    public void run() throws IOException {
        validFile = tempDir.resolve(validFilename);
        badFile = tempDir.resolve(badFilename);
        endOfMonthStatementServiceMock = Mockito.mock(StatementService.class);
        processor = new StatementItemProcessor(endOfMonthStatementServiceMock);
    }

    @Test
    public void isFilenameValid_whenBadFilenameIsGiven_thenReturnFalse() throws IOException {
        Statement endOfMonthStatement = processor.parseFileName(badFilename);
        assertNotNull(endOfMonthStatement);
        assertEquals(badFilename, endOfMonthStatement.getFilename());
        assertEquals(FileNameStatus.INVALID, endOfMonthStatement.getStatementStatus().getFileNameStatus());
        assertEquals(ProcessStatus.NOT_STARTED, endOfMonthStatement.getStatementStatus().getProcessStatus());
    }

    @Test
    public void isFilenameValid_whenValidFilenameIsGiven_thenReturnTrue() {
        Statement endOfMonthStatement = processor.parseFileName(validFilename);
        assertNotNull(endOfMonthStatement);
        assertEquals(validFilename, endOfMonthStatement.getFilename());
        assertEquals(FileNameStatus.VALID, endOfMonthStatement.getStatementStatus().getFileNameStatus());
        assertEquals(ProcessStatus.STARTED, endOfMonthStatement.getStatementStatus().getProcessStatus());
    }

    @Test
    public void process_whenFilenameExist_thenReturnNull() throws Exception {
        when(endOfMonthStatementServiceMock.findByFilename(badFile.toFile().getName())).thenReturn(new Statement());
        Statement endOfMonthStatement = processor.process(badFile.toFile());
        Assert.assertNull(endOfMonthStatement);
    }

    @Test
    public void process_whenFilenameNotExist_thenReturnProcessStatusStarted() throws Exception {
        //When file name does not exist in DB, this object will be processed further
        when(endOfMonthStatementServiceMock.findByFilename(validFile.toFile().getName())).thenReturn(null);
        Statement endOfMonthStatement = processor.process(validFile.toFile());
        assertNotNull(endOfMonthStatement);
        assertEquals(ProcessStatus.STARTED, endOfMonthStatement.getStatementStatus().getProcessStatus());
    }

    @Test
    public void process_whenFilenameNotExistAndFilenameIsValid_thenReturnFilenameStatusValid() throws Exception {
        //When file name does not exist in DB, this object will be processed further
        when(endOfMonthStatementServiceMock.findByFilename(validFile.toFile().getName())).thenReturn(null);
        Statement endOfMonthStatement = processor.process(validFile.toFile());
        assertNotNull(endOfMonthStatement);
        assertEquals(FileNameStatus.VALID, endOfMonthStatement.getStatementStatus().getFileNameStatus());
    }


    @Test
    public void process_whenFilenameNotExistAndFilenameIsBad_thenReturnProcessStatusNotStarted() throws Exception {
        //When file name does not exist in DB, this object will be processed further
        when(endOfMonthStatementServiceMock.findByFilename(badFile.toFile().getName())).thenReturn(null);
        Statement endOfMonthStatement = processor.process(badFile.toFile());
        assertNotNull(endOfMonthStatement);
        assertEquals(ProcessStatus.NOT_STARTED, endOfMonthStatement.getStatementStatus().getProcessStatus());
    }


}