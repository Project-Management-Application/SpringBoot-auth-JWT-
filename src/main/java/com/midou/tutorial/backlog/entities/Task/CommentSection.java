package com.midou.tutorial.backlog.entities.Task;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentSection {
    @Id
    @SequenceGenerator(
            name = "commentSection_sequence",
            sequenceName = "commentSection_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "commentSection_sequence"
    )
    private long commentSectionId;

    @OneToMany(mappedBy = "commentSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToOne(mappedBy = "commentSection")
    private Task task;
}
