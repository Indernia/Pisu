package dk.dtu.compute.se.pisd.roborally.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author s235455
 * 
 * 
 */
public class ResourceFileLister {
    public List<String> getFiles() {
        List<String> fileList = new ArrayList<>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:boards/**/*.json");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && filename.endsWith(".json")) {
                    fileList.add(filename.substring(0, filename.length() - 5)); // Remove ".json" from the end
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
}