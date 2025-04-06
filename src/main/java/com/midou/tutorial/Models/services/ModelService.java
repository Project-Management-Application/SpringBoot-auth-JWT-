package com.midou.tutorial.Models.services;


import com.midou.tutorial.Models.DTO.ModelDTO;
import com.midou.tutorial.Storage.SupabaseStorage;
import com.midou.tutorial.Models.DTO.ModelRequest;
import com.midou.tutorial.Models.entities.Model;
import com.midou.tutorial.Models.entities.ModelCard; // Changed to ModelCard
import com.midou.tutorial.Models.repositories.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelService {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private SupabaseStorage supabaseStorage;

    @Transactional
    public Model createModel(ModelRequest request) throws IOException {
        String fileName = "Image-" + request.getBackgroundImage().getOriginalFilename();
        String imageUrl = supabaseStorage.uploadImage(request.getBackgroundImage(), fileName);

        List<ModelCard> cards = request.getCardNames().stream()
                .map(cardName -> ModelCard.builder()
                        .name(cardName)
                        .build())
                .collect(Collectors.toList());

        Model model = Model.builder()
                .name(request.getName())
                .description(request.getDescription())
                .backgroundImage(imageUrl)
                .cards(cards)
                .build();

        cards.forEach(card -> card.setModel(model));
        System.out.println("Saving model: " + model.getName() + " with " + cards.size() + " cards");

        Model savedModel = modelRepository.save(model);
        System.out.println("Model saved with ID: " + savedModel.getId());
        return savedModel;
    }

    @Transactional(readOnly = true)
    public List<ModelDTO> getAllModels() {
        List<Model> models = modelRepository.findAll();
        return models.stream()
                .map(model -> new ModelDTO(
                        model.getId(),
                        model.getName(),
                        model.getDescription(),
                        model.getBackgroundImage()
                ))
                .collect(Collectors.toList());
    }
}