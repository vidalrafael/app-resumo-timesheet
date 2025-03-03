package br.com.rafaelvidal.app_resumo_timesheet.enums;

public enum Complexidade {
    SIMPLES("Simples"),
    MUITO_SIMPLES("Muito simples"),
    COMPLEXA("Complexa"),
    MUITO_COMPLEXA("Muito complexa");

    private final String descricao;

    Complexidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
