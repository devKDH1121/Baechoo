package joonggo.baechoo.dto;

import joonggo.baechoo.domain.Condition;
import joonggo.baechoo.domain.Status;
import lombok.Data;

@Data
public class ItemCreateDTO {
    private String name;
    private int price;
    private String description;
    private String category;
    private Status status;
    private String imageUrl;
    private Condition condition;
}
