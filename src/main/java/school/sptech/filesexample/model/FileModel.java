package school.sptech.filesexample.model;

import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
public class FileModel {
    private String name;
    private String contentType;
    private Long size;
    private ByteArrayResource content;
}
