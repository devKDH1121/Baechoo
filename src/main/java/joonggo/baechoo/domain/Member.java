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


    private String providerId;
    private String provider;
    private String nickname;

    //아이디

    @Column(unique = true, nullable = false)
    private String userId;

    //비밀번호
    @Column
    private String password;

    //이름
    @Column(nullable = false)
    private String name;

    //주소
    @Column
    private String address = "주소 미입력";

    //이메일
    @Column( unique = true)
    private String email;

    //생년월일
    @Column
    private String birth;

    //전화번호
    @Column(unique = true)
    private String phone;

    //del
    @Column(nullable = false)
    @Builder.Default
    private int del = 0;

    //role
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    // 회원 가입일
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime regDate = LocalDateTime.now();

    public Member update(String nickname){
        this.nickname = nickname;
        return this;
    }
}
