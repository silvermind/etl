package io.adopteunops.etl.repository;

import io.adopteunops.etl.domain.Referential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferentialRepository extends JpaRepository<Referential, Long> {


}