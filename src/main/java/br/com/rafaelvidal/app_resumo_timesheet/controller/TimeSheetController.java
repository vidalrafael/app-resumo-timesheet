package br.com.rafaelvidal.app_resumo_timesheet.controller;

import br.com.rafaelvidal.app_resumo_timesheet.service.TimeSheetService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Controller
public class TimeSheetController {

    @Autowired
    private TimeSheetService timeSheetService;

    @GetMapping("/")
    public String index() {
        return "uploadTimeSheetCsv";
    }

    @PostMapping("/uploadTimeSheetCsv")
    public String uploadTimeSheetCsv(@RequestParam("file") MultipartFile file, Model model) {
        timeSheetService.processFile(file);
        model.addAttribute("message", "File uploaded successfully!");
        return "uploadTimeSheetCsv";
    }

    @PostMapping("/teste")
    public String teste() throws IOException {
        return "uploadTimeSheetCsv";
    }
}
