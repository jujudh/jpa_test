package com._depth.jpa;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class test {


    @Test
    public void testLoading() throws IOException {
        String abPath = "C:/upload";
        String path = "20251013/../../.txt";
        Path filePath = Paths.get(abPath).resolve(path).normalize();
        System.out.println("filepath = " + filePath);
        if (!filePath.startsWith(Paths.get(abPath).toAbsolutePath())) {
            System.out.println("error");
            return;
        }

        //File file = filePath.toFile();
    }

}
