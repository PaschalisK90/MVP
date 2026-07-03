package org.example.mvp.repository;

import org.example.mvp.model.Certificate;
import org.hibernate.query.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.UUID;

@Repository
public interface CertificateRepository  extends JpaRepository<Certificate, UUID>, JpaSpecificationExecutor<Certificate>  {
}
