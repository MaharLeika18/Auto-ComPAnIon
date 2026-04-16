package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.MostProfitItems;
import org.MiniDev.Report.ReportRepo.PieData3Repo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PieData3Service {

    PieData3Repo repo = new PieData3Repo();

    private List<MostProfitItems> getMostProfitAllItemsLists() {
        return repo.getMostProfitItemsLists();
    }

    public List<MostProfitItems> getMostProfitItemsLists(Date fromDate, Date toDate) {
        return repo.getMostProfitItemsLists().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromDate) && !saleDate.after(toDate);
                })
                .collect(Collectors.toList());
    }


}
