package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.PaymentTypeSales;
import org.MiniDev.Report.ReportRepo.PieData2Repo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PieData2Service {

    PieData2Repo repo = new PieData2Repo();

    private List<PaymentTypeSales> getPaymentTypeSalesAll() {
        return repo.getPaymentTypeSales();
    }

    public List<PaymentTypeSales> getPaymentTypesSales(Date fromDate, Date toDate) {
        return getPaymentTypeSalesAll().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromDate) && !saleDate.after(toDate);
                })
                .collect(Collectors.toList());
    }


}
