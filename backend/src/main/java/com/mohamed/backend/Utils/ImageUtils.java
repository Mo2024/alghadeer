package com.mohamed.backend.Utils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

//    @Value("${file.upload-dir}")
    private static String uploadDir = "/home/mohamed/uploads/";


    public static byte[] resizeAndCompress(MultipartFile image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(image.getInputStream())
                .size(500, 500)          // Resize to 500x500
                .outputFormat("jpg")     // Output as JPEG
                .outputQuality(0.8f)     // Compression quality (0.0 = max compression)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    public static void saveImageToFile(byte[] imageBytes, String fileName) throws IOException {
        String fullPath = uploadDir + fileName + ".jpg";

        File file = new File(fullPath);

        // Create parent directories if they don't exist
        file.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
        }
    }

    // Optionally: Add more methods here later (e.g., validateImageType)
}
