package bantads.cliente.repository;

import bantads.cliente.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByIdCliente(Long idCliente);
    Optional<Endereco> deleteByIdCliente(Long idCliente);
}