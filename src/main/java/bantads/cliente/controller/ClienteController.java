package bantads.cliente.controller;

import bantads.cliente.model.Cliente;
import bantads.cliente.model.ClienteDTO;
import bantads.cliente.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ClienteController {

    @Autowired
    ClienteService clienteService;


    @GetMapping("/listar")
    public ResponseEntity<List<Cliente>> getAll() {
        return ResponseEntity.ok().body(clienteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(clienteService.getById(id));
    }

    @PostMapping("/")
    public ResponseEntity<Object> insert(@RequestBody ClienteDTO cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.insert(cliente));
    }

    @PutMapping("/")
    public ResponseEntity<Object> updateCliente(@RequestBody ClienteDTO cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.update(cliente));
    }

    @DeleteMapping("/{idCliente}")
    public ResponseEntity<String> deleteById(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.deleteById(id));
    }
}
