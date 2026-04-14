package com.test.docgen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.docgen.entity.Municipality;

@Repository
public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {

}
