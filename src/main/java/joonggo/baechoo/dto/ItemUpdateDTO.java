package joonggo.baechoo.dto;

import joonggo.baechoo.domain.Condition;
import joonggo.baechoo.domain.Status;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ItemUpdateDTO {
    private String name;
    private int price;
    private String description;
    private String category;
    private Status status;
    private String imageUrl;
    private Condition condition;

    private MultipartFile imageFile;
}
