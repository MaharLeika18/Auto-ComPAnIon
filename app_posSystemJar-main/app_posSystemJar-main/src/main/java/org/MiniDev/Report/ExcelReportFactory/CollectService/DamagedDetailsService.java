package org.MiniDev.Report.ExcelReportFactory.CollectService;

import org.MiniDev.Report.ExcelReportFactory.CollectModel.DamagedDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectRepo.DamagedDetailsRepo;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DamagedDetailsService {
    public List<DamagedDetailsModel> getDamagedDetailsModelLists(Date fromDate, Date toDate) {
        List<DamagedDetailsModel> damagedDetailsList;

        DamagedDetailsRepo repo = new DamagedDetailsRepo();
        damagedDetailsList = repo.getDamagedAmountLists().stream()
                .filter(p -> {
                    Date reportDate = p.getReportDate();
                    return reportDate != null && !reportDate.before(fromDate) && !reportDate.after(toDate);
                })
                .collect(Collectors.toList()); // Collect the stream results into a list
        return damagedDetailsList;
    }
}
