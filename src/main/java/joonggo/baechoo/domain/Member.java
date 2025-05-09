package joonggo.baechoo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //아이디

    @Column(unique = true, nullable = false)
    private String userId;

    //비밀번호
    @Column(nullable = false)
    private String password;

    //이름
    @Column(nullable = false)
    private String name;

    //주소
    @Column(nullable = false)
    private String address;

    //이메일
    @Column(nullable = false, unique = true)
    private String email;

    //생년월일
    @Column(nullable = false)
    private String birth;

    //전화번호
    @Column(nullable = false, unique = true)
    private String phone;

    //del
    @Column(nullable = false)
    private int del = 0;

    //role
    @Column(nullable = false)
    private Role role = Role.USER;

    // 회원 가입일
    @Column(nullable = false)
    private LocalDateTime regDate = LocalDateTime.now();
}
