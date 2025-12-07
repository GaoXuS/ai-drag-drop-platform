package com.aidragdrop.repository;

import com.aidragdrop.entity.AIModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AIModuleRepository extends JpaRepository<AIModule, String> {
    Optional<AIModule> findByName(String name);
}

