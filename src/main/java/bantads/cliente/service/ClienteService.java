package bantads.cliente.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import bantads.cliente.exception.ClienteException;
import bantads.cliente.model.Cliente;
import bantads.cliente.repository.ClienteRepository;
import bantads.cliente.repository.EnderecoRepository;

@Service
public class ClienteService{

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Cliente> getClienteAll(){
        List<Cliente> clientes = new ArrayList<>();
        return clientes;
    }

    public Optional<Cliente> getClienteById(String cpfCliente){
        Optional<Cliente> cliente = ClienteRepository.findClienteById(cpfCliente);
        if(cliente.isEmpty()) throw new ClienteException("Cliente não encontrado(a)!", HttpStatus.NOT_FOUND);
        else return cliente;
    }

    public Optional<Cliente> insertCliente(Cliente cliente) {
        Optional<Cliente> exists = ClienteRepository.findClienteById(cliente.getCpf());
        if(exists.isPresent()) throw new ClienteException("Este cliente já existe!", HttpStatus.BAD_REQUEST);
        else{        
            cliente = new Cliente();
            cliente.setSalario(3000L);

        log.info("Salvando novo cliente...");
        }
        return ClienteRepository.saveCliente(cliente);
    }

    public Optional<Cliente> updateCliente(Cliente cliente){
        Optional<Cliente> exists = ClienteRepository.findClienteById(cliente.getCpf());
        if(!exists.isPresent()) throw new ClienteException("Este cliente não encontrado!", HttpStatus.BAD_REQUEST);
        else{        
            Cliente c = new Cliente();

        log.info("Atualizando cliente...");
        }
        return ClienteRepository.saveCliente(cliente);
    }

    public String deleteClienteById(String cpfCliente){
        Optional<Cliente> exists = clienteRepository.deleteClienteById(cpfCliente);
        if(exists.isEmpty()) throw new ClienteException("Cliente não encontrado para exclusão da conta", HttpStatus.NOT_FOUND);
        else return "Cliente deletado(a) com sucesso!";
    }
}
