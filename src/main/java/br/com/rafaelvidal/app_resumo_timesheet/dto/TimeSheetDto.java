package br.com.rafaelvidal.app_resumo_timesheet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetDto {
    private String id;
    private String title;
    private String state;
    private String assignedTo;
    private String workItemType;
    private String estimates;
    private String complexidade;
    private String effortt;
    private String tese;
    private String dateWork;
    private String commitss;
}
