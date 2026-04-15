package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.TopSalesItems;
import org.MiniDev.Report.ReportRepo.PieData1Repo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PieData1Service {

    PieData1Repo repo = new PieData1Repo();

    private List<TopSalesItems> getTopSalesAllItemsLists() {
        return repo.getTopSalesItemsLists();
    }

    public List<TopSalesItems> getTopSalesItemsLists(Date fromDate, Date toDate) {
        return getTopSalesAllItemsLists().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromDate) && !saleDate.after(toDate);
                })
                .collect(Collectors.toList());
    }


}
