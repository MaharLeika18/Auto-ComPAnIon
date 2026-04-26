package org.MiniDev.Report.ExcelReportFactory.CollectService;

import org.MiniDev.Report.ExcelReportFactory.CollectModel.NetSalesDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectRepo.NetSalesDetailsRepo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class NetSalesDetailsService {

    public List<NetSalesDetailsModel> getNetSalesLists(Date fromDate, Date toDate) {
        List<NetSalesDetailsModel> netResultLists;

        NetSalesDetailsRepo repo = new NetSalesDetailsRepo();
        netResultLists = repo.getNetSalesDetailsSet().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromDate) && !saleDate.after(toDate);
                })
                .collect(Collectors.toList()); // Collect the stream results into a list

        return netResultLists;
    }

}
