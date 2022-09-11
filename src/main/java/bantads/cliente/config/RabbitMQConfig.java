package bantads.cliente.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String MENSAGEM_EXCHANGE = "mensagem";
    public static final String CLIENTE_EXCHANGE = "cliente";
    public static final String FILA_MENSAGEM = "MensagemQueue";
    public static final String FILA_DELETAR_CLIENTE = "DeletarClienteQueue";
    public static final String CHAVE_MENSAGEM = "mensagem";
    public static final String CHAVE_DELETAR_CLIENTE = "deletarCliente";

    @Bean
    DirectExchange mensagemExchange() {
        return new DirectExchange(MENSAGEM_EXCHANGE);
    }

    @Bean
    DirectExchange clienteExchange() {
        return new DirectExchange(CLIENTE_EXCHANGE);
    }

    @Bean
    Queue mensagemQueue() {
        return QueueBuilder.durable(FILA_MENSAGEM).build();
    }

    @Bean
    Queue deletarClienteQueue() {
        return QueueBuilder.durable(FILA_DELETAR_CLIENTE).build();
    }

    @Bean
    Binding mensagemBinding() {
        return BindingBuilder.bind(mensagemQueue()).to(mensagemExchange()).with(CHAVE_MENSAGEM);
    }

    @Bean
    Binding deletarClienteBinding() {
        return BindingBuilder.bind(deletarClienteQueue()).to(clienteExchange()).with(CHAVE_DELETAR_CLIENTE);
    }

}