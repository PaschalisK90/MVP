package org.example.mvp.repository;
import org.example.mvp.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface CertificateRepository  extends JpaRepository<Certificate, Long>, JpaSpecificationExecutor<Certificate>  {
}
