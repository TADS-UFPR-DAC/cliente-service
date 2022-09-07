package bantads.cliente.model;

public class Cliente {
    private String cpf;
    private String nome;
    private String email;
    private Endereco endereco;
    private float salario;
    
    public Cliente() {
    }

    public Cliente(String cpf, String nome, String email, Endereco endereco, float salario) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
        this.salario = salario;
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

    public float getSalario() {
        return salario;
    }

    public void setSalario(float salario) {
        this.salario = salario;
    }     
}
