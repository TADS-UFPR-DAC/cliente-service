package bantads.cliente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bantads.cliente.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findFirstByOrderByIdDesc();

	Optional<Cliente> findByCpf(String cpf);

	List<Cliente> findByStatus(String string);
}
