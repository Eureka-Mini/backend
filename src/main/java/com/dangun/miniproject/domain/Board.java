package com.dangun.miniproject.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Integer price;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments;
}
