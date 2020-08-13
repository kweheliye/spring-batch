package com.mashreq.wealth.batch.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public class DirectoryItemReader implements ItemReader<File> {
    private final String directoryPath;
    private final Queue<File> foundFiles = new ConcurrentLinkedDeque();


    private ResourcePatternResolver patternResolver;

    public DirectoryItemReader(final String directoryPath) throws IOException {
        this(new PathMatchingResourcePatternResolver(), directoryPath);

    }

    public DirectoryItemReader(ResourcePatternResolver patternResolver, final String directoryPath) throws IOException {
        this.patternResolver = patternResolver;
        this.directoryPath = directoryPath;
        for (final Resource file : getFiles()) {
            this.foundFiles.offer(file.getFile());
        }
    }

    @Override
    public File read() {
        return foundFiles.poll();
    }

    //For testing purpose
    public Queue<File> getFoundFiles() {
        return foundFiles;
    }

    /**
     * Read Files from a folder
     * @return   Resource of Files
     * @throws IOException
     */
    private Resource[] getFiles() throws IOException {
        log.info("directoryPath:{}", directoryPath);
        patternResolver = new PathMatchingResourcePatternResolver();
        return patternResolver.getResources(directoryPath);
    }


}
