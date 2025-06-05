package joonggo.baechoo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;

    @Column(length = 1000)
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdDate;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Condition condition;

//    Persist
    @PrePersist
    public void perPersist(){
        this.createdDate = LocalDateTime.now();
    }

//    조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
