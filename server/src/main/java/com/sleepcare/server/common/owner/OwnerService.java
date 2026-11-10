package com.sleepcare.server.common.owner;

import java.time.Instant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    // 처음 보는 사용자면 owner 행을 만든다 회원가입이 없으니
    public void ensureExists(String ownerId) {
        if (ownerRepository.existsById(ownerId)) {
            return;
        }
        try {
            ownerRepository.save(new Owner(ownerId, Instant.now()));
        } catch (DataIntegrityViolationException e) {
            // 같은 사용자의 첫 요청 두 개가 동시에 온 경우 -> 이미 만들어졌으니 무시한다
        }
    }
}
