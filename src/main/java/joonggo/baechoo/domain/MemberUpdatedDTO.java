package joonggo.baechoo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberUpdatedDTO {

    private Long id;
    private String userId;
//    private String password;
    private String name;
    private String address;
    private String email;
    private String phone;
}
