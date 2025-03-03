package br.com.rafaelvidal.app_resumo_timesheet.enums;

public enum TiposWorkItem {
    ANALISE_SUSTENTACAO("Analise Sustentacao"),
    TASK("Task"),
    ATENDIMENTO_AO_CLIENTE("Atendimento ao cliente"),
    BANCO_DE_DADOS("Banco de dados"),
    DESENVOLVIMENTO("Desenvolvimento");

    private final String descricao;

    TiposWorkItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}