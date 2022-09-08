package bantads.cliente.exception;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static bantads.cliente.config.RabbitMQConfig.CHAVE_MENSAGEM;
import static bantads.cliente.config.RabbitMQConfig.MENSAGEM_EXCHANGE;

public class ClienteException extends ResponseStatusException {
    public ClienteException(String message, HttpStatus httpStatus, AmqpTemplate rabbitTemplate) {
        super(httpStatus, message);
        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, "error");
    }
}