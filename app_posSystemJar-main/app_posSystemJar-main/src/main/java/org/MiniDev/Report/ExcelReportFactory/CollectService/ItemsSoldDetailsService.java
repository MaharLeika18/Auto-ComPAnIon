package org.MiniDev.Report.ExcelReportFactory.CollectService;

import org.MiniDev.Report.ExcelReportFactory.CollectModel.ItemsSoldDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectRepo.ItemsSoldDetailsRepo;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ItemsSoldDetailsService {

    public List<ItemsSoldDetailsModel> getItemsSoldDetailsList(Date fromDate, Date toDate) {
        List<ItemsSoldDetailsModel> itemsSoldLists;

        ItemsSoldDetailsRepo repo = new ItemsSoldDetailsRepo();
        itemsSoldLists = repo.getItemsSoldDetailsLists().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromDate) && !saleDate.after(toDate);
                })
                .collect(Collectors.toList()); // Collect the stream results into a list
        return itemsSoldLists;
    }
}
