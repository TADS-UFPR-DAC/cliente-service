package bantads.cliente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bantads.cliente.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    static Optional<Cliente> findClienteById(String cpfCliente) {
        
        return null;
    }

    static Optional<Cliente> saveCliente(Cliente cliente) {
        // TODO Auto-generated method stub
        return null;
    }

    Optional<Cliente> deleteClienteById(String cpfCliente);
}
