package com.kosa.backend.user.service;

import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.MakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MakerService {
    private final MakerRepository makerRepository;

    public Maker findById(int userId) {
        return makerRepository.findById(userId).get();
    }

    public void updateMaker(User user ,String userContent) {
        Optional<Maker> maker = makerRepository.findById(user.getId());
        if(maker.isPresent()) {
            maker.get().updateUserContent(userContent);
            makerRepository.save(maker.get());
        }
    }
}
