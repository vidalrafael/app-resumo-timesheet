package br.com.rafaelvidal.app_resumo_timesheet.dto;

import lombok.Data;

@Data
public class ResultadoDto {
    private String usuario;
    private Integer totalCards;
    private Integer totalHoras;
    private Integer mediaHorasPorCard;
    private Integer quantidadeSimples;
    private Integer quantidadeMuitoSimples;
    private Integer quantidadeComplexa;
    private Integer quantidadeMuitoComplexa;
    private Integer quantidadeAnaliseSustentacao;
    private Integer quantidadeAtendimentoAoCliente;
    private Integer quantidadeBancoDeDados;
    private Integer quantidadeDesenvolvimento;
}
