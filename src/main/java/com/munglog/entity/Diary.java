package com.munglog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    private LocalDateTime createdAt;

    public Diary(String content, LocalDateTime createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
