package bantads.cliente.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import bantads.cliente.model.ClienteDTO;
import bantads.cliente.model.Endereco;
import bantads.cliente.model.Status;
import bantads.cliente.utils.Misc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import bantads.cliente.exception.ClienteException;
import bantads.cliente.model.Cliente;
import bantads.cliente.repository.ClienteRepository;
import bantads.cliente.repository.EnderecoRepository;

import static bantads.cliente.config.RabbitMQConfig.*;

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
        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("acharTodosClientes"));
        return clienteRepository.findAll();
    }
    
    public List<Cliente> findByStatus() {
        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("acharTodosClientes"));
        return clienteRepository.findByStatus("PENDENTE");
	}

    public ClienteDTO getById(Long id){
        ModelMapper mapper = new ModelMapper();

        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isEmpty()){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("acharCliente"));
            throw new ClienteException("Cliente não encontrado(a)!", HttpStatus.NOT_FOUND);
        }

        Optional<Endereco> endereco = enderecoRepository.findById(cliente.get().getIdEndereco());

        ClienteDTO clienteDTO = mapper.map(cliente.get(), ClienteDTO.class);
        endereco.ifPresent(value -> clienteDTO.setEndereco(mapper.map(value, Endereco.class)));

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("acharCliente"));
        return clienteDTO;
    }
    
    public ClienteDTO getByCpf(String cpf){
        ModelMapper mapper = new ModelMapper();

        Optional<Cliente> cliente = clienteRepository.findByCpf(cpf);
        if(cliente.isEmpty()){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("acharClientePorCpf"));
            throw new ClienteException("Cliente não encontrado(a)!", HttpStatus.NOT_FOUND);
        }

        Optional<Endereco> endereco = enderecoRepository.findById(cliente.get().getIdEndereco());

        ClienteDTO clienteDTO = mapper.map(cliente.get(), ClienteDTO.class);
        endereco.ifPresent(value -> clienteDTO.setEndereco(mapper.map(value, Endereco.class)));

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("acharClientePorCpf"));
        return clienteDTO;
    }

    public Cliente insert(ClienteDTO clienteDTO) {
        if(clienteDTO.getCpf() == null || clienteDTO.getCpf().equals("") ||
                clienteDTO.getNome() == null || clienteDTO.getNome().equals("") ||
                clienteDTO.getEmail() == null || clienteDTO.getEmail().equals("")){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("inserirCliente"));
            throw new ClienteException("Campos faltando!", HttpStatus.BAD_REQUEST);
        }
        ModelMapper mapper = new ModelMapper();
        Cliente cliente = mapper.map(clienteDTO, Cliente.class);
        Endereco endereco = mapper.map(clienteDTO.getEndereco(), Endereco.class);

        endereco.setId(enderecoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(1L));
        log.info("Inserindo endereço do cliente com nome " + cliente.getNome());
        endereco = enderecoRepository.save(endereco);

        cliente.setIdEndereco(endereco.getId());
        cliente.setId(clienteRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(1L));
        cliente.setStatus(Status.PENDENTE.name());

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("inserirCliente"));
        log.info("Inserindo cliente com nome " + cliente.getNome());
        return clienteRepository.save(cliente);
    }

    public Cliente update(ClienteDTO clienteDTO) {
        if(clienteDTO.getId() == null){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("atualizarCliente"));
            throw new ClienteException("Id do cliente é obrigatório na atualização!", HttpStatus.BAD_REQUEST);
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteDTO.getId());
        if(clienteOpt.isEmpty()){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("atualizarCliente"));
            throw new ClienteException("Cliente com id "+clienteDTO.getId()+" inexistente!", HttpStatus.NOT_FOUND);
        }
        Cliente cliente = clienteOpt.get();

        Misc.copyNonNullProperties(clienteDTO, cliente);

        Optional<Endereco> enderecoOpt = enderecoRepository.findById(cliente.getIdEndereco());
        enderecoOpt.ifPresent(newEndereco -> {
            Misc.copyNonNullProperties(clienteDTO.getEndereco(), newEndereco);

            log.info("Atualizando endereço do cliente com nome " + cliente.getNome());
            enderecoRepository.save(newEndereco);
        });

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("atualizarCliente"));
        log.info("Atualizando cliente com nome " + cliente.getNome());
        return clienteRepository.save(cliente);
    }

    public String deleteById(Long id){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isEmpty()){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("deletarCliente"));
            throw new ClienteException("Cliente não encontrado para exclusão!", HttpStatus.NOT_FOUND);
        }

        Optional<Endereco> endereco = enderecoRepository.findById(cliente.get().getIdEndereco());
        endereco.ifPresent(value -> enderecoRepository.delete(value));

        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("deletarCliente"));
        clienteRepository.delete(cliente.get());
        return "Cliente deletado(a) com sucesso!";
    }

    public void aprovar(Long id){
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if(clienteOptional.isEmpty()){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("aprovarCliente"));
            throw new ClienteException("Cliente não encontrado!", HttpStatus.NOT_FOUND);
        }

        Cliente cliente = clienteOptional.get();
        if(!Objects.equals(cliente.getStatus(), Status.REPROVADO.name())) {
            cliente.setStatus(Status.APROVADO.name());

            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("aprovarCliente"));
            clienteRepository.save(cliente);
        }
    }

    public void reprovar(Long id){
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if(clienteOptional.isEmpty()){
            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, errorFormat("reprovarCliente"));
            throw new ClienteException("Cliente não encontrado!", HttpStatus.NOT_FOUND);
        }

        Cliente cliente = clienteOptional.get();

        if(!Objects.equals(cliente.getStatus(), Status.APROVADO.name())){
            cliente.setStatus(Status.REPROVADO.name());

            rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, successFormat("reprovarCliente"));
            clienteRepository.save(cliente);
        }
    }

    private String successFormat(String endpoint){
        return "{" +
                "\"path\":\""+endpoint+"\"," +
                "\"result\":\"success\"" +
                "}";
    }

    private String errorFormat(String endpoint){
        return "{" +
                "\"path\":\""+endpoint+"\"," +
                "\"result\":\"error\"" +
                "}";
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(FILA_DELETAR_CLIENTE),
            exchange = @Exchange(name = CLIENTE_EXCHANGE),
            key = CHAVE_DELETAR_CLIENTE))
    public void deletarConta(final Message message, final Cliente cliente) {
        log.info("Deletando cliente com id "+cliente.getId());
        clienteRepository.delete(cliente);
    }

}
