package bantads.cliente.model;

import javax.persistence.Column;
import javax.persistence.Id;

public class ClienteDTO {

    private Long id;
    private String cpf;
    private String nome;
    private String email;
    private Endereco endereco;

    public ClienteDTO() {
    }

    public ClienteDTO(String cpf, String nome, String email, Endereco endereco) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
