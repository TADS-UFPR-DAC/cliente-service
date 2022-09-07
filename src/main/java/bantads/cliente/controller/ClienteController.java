package bantads.cliente.controller;

import bantads.cliente.model.Cliente;
import bantads.cliente.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @GetMapping("/Cliente/{idCliente}")
    public ResponseEntity<Optional<Cliente>> getClienteById(@PathVariable String cpfCliente) {
        return ResponseEntity.ok().body(clienteService.getClienteById(cpfCliente));
    }

    @GetMapping("/Listar")
    public ResponseEntity<List<Cliente>> getClienteAll() {
        return ResponseEntity.ok().body(clienteService.getClienteAll());
    }

    @PostMapping("/Inserir/")
    public ResponseEntity<Optional<Cliente>> saveCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.insereCliente(cliente));
    }

    @PutMapping("/Atualizar/")
    public ResponseEntity<Optional<Cliente>> updateCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.atualizaCliente(cliente));
    }

    @DeleteMapping("/Deletar/{idCliente}")
    public ResponseEntity<String> deleteClienteById(@PathVariable String cpfCliente){
        return ResponseEntity.ok(clienteService.deleteByIdCliente(cpfCliente));
    }
}
