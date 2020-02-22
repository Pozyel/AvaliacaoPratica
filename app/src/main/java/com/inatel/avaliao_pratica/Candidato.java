package com.inatel.avaliao_pratica;

public class Candidato {
    String id;
    String nome;
    String tel;
    String email;
    String princHab;
    String fotoDoCandidato;

    public String getFotoDoCandidato() {
        return fotoDoCandidato;
    }

    public void setFotoDoCandidato(String fotoDoCandidato) {
        this.fotoDoCandidato = fotoDoCandidato;
    }

    public Candidato() {
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPrincHab() {
        return princHab;
    }

    public void setPrincHab(String princHab) {
        this.princHab = princHab;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nome + tel + email + princHab;

    }


}
