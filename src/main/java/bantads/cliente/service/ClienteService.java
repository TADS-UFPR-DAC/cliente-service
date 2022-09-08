package bantads.cliente.service;

import java.util.List;
import java.util.Optional;

import bantads.cliente.model.ClienteDTO;
import bantads.cliente.model.Endereco;
import bantads.cliente.utils.Misc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import bantads.cliente.exception.ClienteException;
import bantads.cliente.model.Cliente;
import bantads.cliente.repository.ClienteRepository;
import bantads.cliente.repository.EnderecoRepository;

import static bantads.cliente.config.RabbitMQConfig.CHAVE_MENSAGEM;
import static bantads.cliente.config.RabbitMQConfig.MENSAGEM_EXCHANGE;

@Service
public class ClienteService{

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Cliente> getAll(){
        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, "success");
        return clienteRepository.findAll();
    }

    public ClienteDTO getById(Long id){
        ModelMapper mapper = new ModelMapper();

        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isEmpty())
            throw new ClienteException("Cliente não encontrado(a)!", HttpStatus.NOT_FOUND, rabbitTemplate);

        Optional<Endereco> endereco = enderecoRepository.findById(cliente.get().getIdEndereco());

        ClienteDTO clienteDTO = mapper.map(cliente.get(), ClienteDTO.class);
        endereco.ifPresent(value -> clienteDTO.setEndereco(mapper.map(value, Endereco.class)));

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, "success");
        return clienteDTO;
    }

    public Cliente insert(ClienteDTO clienteDTO) {
        ModelMapper mapper = new ModelMapper();
        Cliente cliente = mapper.map(clienteDTO, Cliente.class);
        Endereco endereco = mapper.map(clienteDTO.getEndereco(), Endereco.class);

        endereco.setId(enderecoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(1L));
        log.info("Inserindo endereço do cliente com nome " + cliente.getNome());
        endereco = enderecoRepository.save(endereco);

        cliente.setIdEndereco(endereco.getId());
        cliente.setId(clienteRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(1L));

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, "success");
        log.info("Inserindo cliente com nome " + cliente.getNome());
        return clienteRepository.save(cliente);
    }

    public Cliente update(ClienteDTO clienteDTO) {
        if(clienteDTO.getId() == null)
            throw new ClienteException("Id do cliente é obrigatório na atualização!", HttpStatus.BAD_REQUEST, rabbitTemplate);

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteDTO.getId());
        if(clienteOpt.isEmpty())
            throw new ClienteException("Cliente com id "+clienteDTO.getId()+" inexistente!", HttpStatus.NOT_FOUND, rabbitTemplate);
        Cliente cliente = clienteOpt.get();

        Misc.copyNonNullProperties(clienteDTO, cliente);

        Optional<Endereco> enderecoOpt = enderecoRepository.findById(cliente.getIdEndereco());
        enderecoOpt.ifPresent(newEndereco -> {
            Misc.copyNonNullProperties(clienteDTO.getEndereco(), newEndereco);

            log.info("Atualizando endereço do cliente com nome " + cliente.getNome());
            enderecoRepository.save(newEndereco);
        });

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, "success");
        log.info("Atualizando cliente com nome " + cliente.getNome());
        return clienteRepository.save(cliente);
    }

    public String deleteById(Long id){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isEmpty())
            throw new ClienteException("Cliente não encontrado para exclusão!", HttpStatus.NOT_FOUND, rabbitTemplate);

        Optional<Endereco> endereco = enderecoRepository.findById(cliente.get().getIdEndereco());
        endereco.ifPresent(value -> enderecoRepository.delete(value));

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, "success");
        clienteRepository.delete(cliente.get());
        return "Cliente deletado(a) com sucesso!";
    }
}
