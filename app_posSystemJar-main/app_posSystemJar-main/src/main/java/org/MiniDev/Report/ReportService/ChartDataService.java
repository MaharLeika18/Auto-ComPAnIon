package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.ProfitChartDataModel;
import org.MiniDev.Report.ReportRepo.ChartDataRepo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ChartDataService {

    protected ChartDataRepo repo = new ChartDataRepo();

    private List<ProfitChartDataModel> getProfitChartDataModelAllLists() {
        return repo.getProfitChartDataModelLists();
    }

    public List<ProfitChartDataModel> getProfitChartDataModelLists(Date fromDate, Date toDate) {
        return repo.getProfitChartDataModelLists().stream()
                .filter(p -> {
                    Date reportDate = p.getReportDate();
                    return reportDate != null && !reportDate.before(fromDate) && !reportDate.after(toDate);
                })
                .collect(Collectors.toList());
    }
}
