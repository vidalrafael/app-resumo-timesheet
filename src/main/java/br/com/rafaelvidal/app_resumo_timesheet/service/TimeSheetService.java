package br.com.rafaelvidal.app_resumo_timesheet.service;

import br.com.rafaelvidal.app_resumo_timesheet.dto.ResultadoDto;
import br.com.rafaelvidal.app_resumo_timesheet.dto.TimeSheetDto;
import br.com.rafaelvidal.app_resumo_timesheet.enums.Complexidade;
import br.com.rafaelvidal.app_resumo_timesheet.enums.TiposWorkItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.Files.write;
import static java.nio.file.Paths.get;

@Service
public class TimeSheetService {

    @Autowired
    GraficosService graficosService;

    public void processFile(MultipartFile file) {
        List<TimeSheetDto> listTimeSheetDto = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) { // Skip header
                    firstLine = false;
                    continue;
                }

                TimeSheetDto item = lerLinhaArquivoCsv(line);

                listTimeSheetDto.add(item);
            }

            processarListaTimeSheet(listTimeSheetDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TimeSheetDto lerLinhaArquivoCsv(String line) {
        String[] columns = line.split(",");

        TimeSheetDto linhaArquivoCsv = new TimeSheetDto(
                columns[0].replace("\"", ""),
                columns[1].replace("\"", ""),
                columns[2].replace("\"", ""),
                columns[3].replace("\"", ""),
                columns[4].replace("\"", ""),
                columns[5].replace("\"", ""),
                columns.length > 6 && Objects.nonNull(columns[6]) ? columns[6].replace("\"", "") : "",
                columns.length > 7 && Objects.nonNull(columns[7]) ? columns[7].replace("\"", "") : "",
                columns.length > 8 && Objects.nonNull(columns[8]) ? columns[8].replace("\"", "") : "",
                columns.length > 9 && Objects.nonNull(columns[9]) ? columns[9].replace("\"", "") : "",
                columns.length > 10 && Objects.nonNull(columns[10]) ? columns[10].replace("\"", "") : ""
        );

        return linhaArquivoCsv;
    }

    private void processarListaTimeSheet(List<TimeSheetDto> listTimeSheetDto) {
        Map<String, List<TimeSheetDto>> mapPorUsuario = new HashMap<>();

        for (TimeSheetDto timeSheetDto : listTimeSheetDto) {
            mapPorUsuario.computeIfAbsent(timeSheetDto.getAssignedTo(), k -> new ArrayList<>())
                         .add(timeSheetDto);
        }

        List<ResultadoDto> listResultadoDto = new ArrayList<>();

        mapPorUsuario.forEach((usuario, listTimeSheet) -> {
            ResultadoDto resultadoDto = new ResultadoDto();

            resultadoDto.setUsuario(obterNomeUsuario(usuario));
            resultadoDto.setTotalCards(obterTotalCards(listTimeSheet));
            resultadoDto.setTotalHoras(obterTotalHoras(listTimeSheet));
            resultadoDto.setMediaHorasPorCard(resultadoDto.getTotalHoras() / resultadoDto.getTotalCards());
            resultadoDto.setQuantidadeSimples(obterQuantidadePorComplexidade(listTimeSheet, Complexidade.SIMPLES.getDescricao()));
            resultadoDto.setQuantidadeMuitoSimples(obterQuantidadePorComplexidade(listTimeSheet, Complexidade.MUITO_SIMPLES.getDescricao()));
            resultadoDto.setQuantidadeComplexa(obterQuantidadePorComplexidade(listTimeSheet, Complexidade.COMPLEXA.getDescricao()));
            resultadoDto.setQuantidadeMuitoComplexa(obterQuantidadePorComplexidade(listTimeSheet, Complexidade.MUITO_COMPLEXA.getDescricao()));
            resultadoDto.setQuantidadeAnaliseSustentacao(obterQuantidadePorWorkItem(listTimeSheet, TiposWorkItem.ANALISE_SUSTENTACAO.getDescricao()));
            resultadoDto.setQuantidadeAtendimentoAoCliente(obterQuantidadePorWorkItem(listTimeSheet, TiposWorkItem.ATENDIMENTO_AO_CLIENTE.getDescricao()));
            resultadoDto.setQuantidadeBancoDeDados(obterQuantidadePorWorkItem(listTimeSheet, TiposWorkItem.BANCO_DE_DADOS.getDescricao()));
            resultadoDto.setQuantidadeDesenvolvimento(obterQuantidadePorWorkItem(listTimeSheet, TiposWorkItem.DESENVOLVIMENTO.getDescricao()));

            listResultadoDto.add(resultadoDto);
        });

        gerarRelatorio(listResultadoDto);
    }

    private String obterNomeUsuario(String usuario) {
        return usuario.split(" <")[0];
    }

    private Integer obterQuantidadePorComplexidade(List<TimeSheetDto> listTimeSheet, String descricao) {
        return (int) listTimeSheet.stream()
                .filter(timeSheetDto -> timeSheetDto.getComplexidade().equals(descricao))
                .count();
    }

    private Integer obterQuantidadePorWorkItem(List<TimeSheetDto> listTimeSheet, String descricao) {
        return (int) listTimeSheet.stream()
                .filter(timeSheetDto -> timeSheetDto.getWorkItemType().equals(descricao))
                .count();
    }

    private Integer obterTotalHoras(List<TimeSheetDto> listTimeSheet) {
        return listTimeSheet.stream()
                .filter(timeSheetDto -> timeSheetDto.getWorkItemType().equals(TiposWorkItem.TASK.getDescricao()))
                .mapToInt(timeSheetDto -> !timeSheetDto.getEffortt().isEmpty() ? Integer.parseInt(timeSheetDto.getEffortt()) : 0)
                .sum();
    }

    private Integer obterTotalCards(List<TimeSheetDto> listTimeSheet) {
        return (int) listTimeSheet.stream()
                .filter(timeSheetDto -> !timeSheetDto.getWorkItemType().equals(TiposWorkItem.TASK.getDescricao()))
                .count();
    }

    private void gerarRelatorio(List<ResultadoDto> listResultadoDto) {
        String caminhoPadrao = "D:\\Usuarios\\Vidal\\Desktop\\Gestao Prover\\resultado";

        for (ResultadoDto resultadoDto : listResultadoDto) {
            String caminho = caminhoPadrao + "\\" + resultadoDto.getUsuario();

            criarDiretorio(caminho);

            this.gerarArquivoTxtResultado(resultadoDto, caminho);
            graficosService.gerarGraficoPizzaComplexidade(caminho, resultadoDto);
            graficosService.gerarGraficoPizzaTiposWorkItem(caminho, resultadoDto);
        }
    }

    private void criarDiretorio(String caminho) {
        try {
            Path path = Path.of(caminho);

            if (!Files.exists(path)) {
                Files.createDirectories(path);

            } else {
                FileSystemUtils.deleteRecursively(path.toFile());
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void gerarArquivoTxtResultado(ResultadoDto resultadoDto, String caminho) {
        try {
            String nomeArquivo = caminho + "/Resumo dos resultados.txt";
            String conteudo = "Usuário: " + resultadoDto.getUsuario() + "\n" +
                    "Total de Cards: " + resultadoDto.getTotalCards() + "\n" +
                    "Total de Horas: " + resultadoDto.getTotalHoras() + "\n" +
                    "Média de Horas por Card: " + resultadoDto.getMediaHorasPorCard() + "\n" +
                    "Quantidade de Cards Simples: " + resultadoDto.getQuantidadeSimples() + "\n" +
                    "Quantidade de Cards Muito Simples: " + resultadoDto.getQuantidadeMuitoSimples() + "\n" +
                    "Quantidade de Cards Complexos: " + resultadoDto.getQuantidadeComplexa() + "\n" +
                    "Quantidade de Cards Muito Complexos: " + resultadoDto.getQuantidadeMuitoComplexa() + "\n" +
                    "Quantidade de Cards de Análise e Sustentação: " + resultadoDto.getQuantidadeAnaliseSustentacao() + "\n" +
                    "Quantidade de Cards de Atendimento ao Cliente: " + resultadoDto.getQuantidadeAtendimentoAoCliente() + "\n" +
                    "Quantidade de Cards de Banco de Dados: " + resultadoDto.getQuantidadeBancoDeDados() + "\n" +
                    "Quantidade de Cards de Desenvolvimento: " + resultadoDto.getQuantidadeDesenvolvimento() + "\n";

            write(get(nomeArquivo), conteudo.getBytes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
