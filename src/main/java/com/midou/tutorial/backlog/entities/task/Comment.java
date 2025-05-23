package com.midou.tutorial.backlog.entities.task;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sequence"
    )
    private long commentId;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "commentSection_id", nullable = false)
    private CommentSection commentSection;
}
