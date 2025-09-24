package com.VTM.application.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AccountWritePlatformService {
//    Map<String, Object> getRates();

    List<Map<String, Object>> totalStock();

    List<Map<String, Object>> totalCash(Date startDate, Date endDate);

    List<Map<String, Object>> totalSalesWeight(Date startDate, Date endDate);

    List<Map<String, Object>> totalOldGoldPurchaseWeight(Date startDate, Date endDate);

    List<Map<String, Object>> totalCreditCardBill(Date startDate, Date endDate);

    List<Map<String, Object>> getBillCancelledIssues(Date startDate, Date endDate);

    List<Map<String, Object>> getAllCostCentres();

    List<Map<String, Object>> getCompanyNames();

    Map<String, Object> getTotalChequeAndUPI(Date startDate, Date endDate);

    Map<String, Object> getEstimateSummary(Date startDate, Date endDate);


    List<Map<String, Object>> getPaymentSummary(Date startDate, Date endDate);

    List<Map<String, Object>> getStoneSummary(Date startDate, Date endDate);

    Map<String, Object> getRateOFGoldAndSliver();

    Map<String, Object> getSchemeCollection(Date startDate, Date endDate);

    Map<String, Object> getAdjustmentWeightAmount(Date startDate, Date endDate);

    Map<String, Object> getSchemeAdjustment(Date startDate, Date endDate, String costId);
}
