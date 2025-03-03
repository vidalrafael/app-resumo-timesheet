package br.com.rafaelvidal.app_resumo_timesheet.service;

import br.com.rafaelvidal.app_resumo_timesheet.dto.ResultadoDto;
import br.com.rafaelvidal.app_resumo_timesheet.enums.Complexidade;
import br.com.rafaelvidal.app_resumo_timesheet.enums.TiposGraficos;
import br.com.rafaelvidal.app_resumo_timesheet.enums.TiposWorkItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class GraficosService {

    public void gerarGraficoPizzaComplexidade(String caminho, ResultadoDto resultadoDto) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(Complexidade.MUITO_SIMPLES.getDescricao(), resultadoDto.getQuantidadeMuitoSimples());
        dataset.setValue(Complexidade.SIMPLES.getDescricao(), resultadoDto.getQuantidadeSimples());
        dataset.setValue(Complexidade.COMPLEXA.getDescricao(), resultadoDto.getQuantidadeComplexa());
        dataset.setValue(Complexidade.MUITO_COMPLEXA.getDescricao(), resultadoDto.getQuantidadeMuitoComplexa());

        String nomeArquivo = "complexidade.png"; 
        String titulo = "Complexidade dos cards";

        this.gerarGraficoPizza(caminho, dataset, titulo, nomeArquivo, TiposGraficos.COMPLEXIDADE);
    }

    public void gerarGraficoPizzaTiposWorkItem(String caminho, ResultadoDto resultadoDto) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(TiposWorkItem.ANALISE_SUSTENTACAO.getDescricao(), resultadoDto.getQuantidadeAnaliseSustentacao());
        dataset.setValue(TiposWorkItem.DESENVOLVIMENTO.getDescricao(), resultadoDto.getQuantidadeDesenvolvimento());
        dataset.setValue(TiposWorkItem.ATENDIMENTO_AO_CLIENTE.getDescricao(), resultadoDto.getQuantidadeAtendimentoAoCliente());
        dataset.setValue(TiposWorkItem.BANCO_DE_DADOS.getDescricao(), resultadoDto.getQuantidadeBancoDeDados());

        String nomeArquivo = "tiposWorkItem.png";
        String titulo = "Tipos de itens de atuação";

        this.gerarGraficoPizza(caminho, dataset, titulo, nomeArquivo, TiposGraficos.TIPOS_WORK_ITEM);
    }

    private void gerarGraficoPizza(String caminho, DefaultPieDataset dataset, String titulo, String nomeArquivo, TiposGraficos tipoGrafico) {
        try {
            JFreeChart grafico = ChartFactory.createPieChart(
                    titulo,
                    dataset,
                    true, // legenda
                    true, // tooltips
                    false // URLs
            );

            configurarEstiloGraficoPizza(grafico, dataset, tipoGrafico);

            File arquivo = new File(caminho + "\\" +  nomeArquivo);
            ChartUtils.saveChartAsPNG(arquivo, grafico, 800, 600);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void configurarEstiloGraficoPizza(JFreeChart grafico, DefaultPieDataset dataset, TiposGraficos tipoGrafico) {
        grafico.setBackgroundPaint(Color.WHITE);
        grafico.setBorderVisible(false);

        LegendTitle legenda = grafico.getLegend();
        legenda.setItemFont(new Font("Arial", Font.BOLD, 16));

        PiePlot plot = (PiePlot) grafico.getPlot();

        List<Comparable> chaves = dataset.getKeys(); // Recupera as keys
        Color[] cores = obterCoresPorTipoGrafico(tipoGrafico);

        for (int i = 0; i < chaves.size(); i++) {
            plot.setSectionPaint(chaves.get(i), cores[i % cores.length]);
        }

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
    }

    private Color[] obterCoresPorTipoGrafico(TiposGraficos tipoGrafico) {
        switch (tipoGrafico) {
            case COMPLEXIDADE:
                return new Color[] {
                        new Color(70, 163, 90),
                        new Color(242, 171, 21),
                        new Color(23, 72, 113),
                        new Color(239, 64, 38)
                };

            case TIPOS_WORK_ITEM:
                return new Color[] {
                        new Color(209, 185, 91),
                        new Color(185, 153, 124),
                        new Color(195, 114, 52),
                        new Color(28, 64, 10)
                };

            default:
                throw new RuntimeException("Tipo de gráfico não encontrado");
        }
    }


}
