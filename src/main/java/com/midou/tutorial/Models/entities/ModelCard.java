package com.midou.tutorial.Models.entities;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "model_cards")
public class ModelCard {
    @Id
    @SequenceGenerator(
            name = "model_card_sequence",
            sequenceName = "model_card_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "model_card_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;
}