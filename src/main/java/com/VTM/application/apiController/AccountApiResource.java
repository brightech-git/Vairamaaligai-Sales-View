package com.VTM.application.apiController;

import com.VTM.application.service.AccountWritePlatformService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin(origins = {
        "https://nkvairamaaligai.com/",
        "http://localhost:3000",
})
@RequestMapping("/api/v1")
public class AccountApiResource {

    private final AccountWritePlatformService accountService;

    @Autowired
    public AccountApiResource(AccountWritePlatformService accountService) {
        this.accountService = accountService;
    }


    @GetMapping("/rates")
    public Map<String, Object> getRateOFGoldAndSliver() {
        return accountService.getRateOFGoldAndSliver();
    }

    @GetMapping("/totalStock")
    public List<Map<String, Object>> totalStock() {
        try {
            return accountService.totalStock();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/totalCash")
    public List<Map<String, Object>> totalCash(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        try {
            // Pass both startDate and endDate to the service method
            return accountService.totalCash(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching total cash", e);
        }
    }

    @GetMapping("/totalSalesWeight")
    public List<Map<String, Object>> totalSalesWeight(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            return accountService.totalSalesWeight(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching total Sales Weight", e);
        }
    }

    @GetMapping("/totalOldGoldPurchaseWeight")
    public List<Map<String, Object>> totalOldGoldPurchaseWeight(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        try {
            // Pass both startDate and endDate to the service method
            return accountService.totalOldGoldPurchaseWeight(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching total Sales Weight", e);
        }
    }

    @GetMapping("/totalCreditCardBill")
    public List<Map<String, Object>> totalCreditCardBill(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        try {
            // Pass both startDate and endDate to the service method
            return accountService.totalCreditCardBill(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching total Sales Weight", e);
        }
    }
    @GetMapping("/billCancel")
    public List<Map<String, Object>> getBillCancelledIssues(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return accountService.getBillCancelledIssues(startDate, endDate);
    }

    @GetMapping("/costCentres")
    public List<Map<String, Object>> getAllCostCentres() {
        return accountService.getAllCostCentres();
    }

    @GetMapping("/companyNames")
    public List<Map<String, Object>> getCompanyNames() {
        return accountService.getCompanyNames();
    }

    @GetMapping("/totalChequeAndUPI")
    public Map<String, Object> getTotalChequeAndUPI(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return accountService.getTotalChequeAndUPI(startDate, endDate);
    }

    @GetMapping("/summary")
    public Map<String, Object> getEstimateSummary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return accountService.getEstimateSummary(startDate, endDate);
    }


    @GetMapping("/paymentSummary")
    public List<Map<String, Object>> getPaymentSummary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        return accountService.getPaymentSummary(startDate, endDate);
    }

    @GetMapping("/stoneSummary")
    public List<Map<String, Object>> getStoneSummary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return accountService.getStoneSummary(startDate, endDate);
    }

    @GetMapping("/collection")
    public ResponseEntity<Map<String, Object>> getSchemeCollection(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        Map<String, Object> result = accountService.getSchemeCollection(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/AdjustmentWeightAmount") // 🔁 Removed "FJ"
    public ResponseEntity<Map<String, Object>> getAdjustmentWeightAmount(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        Map<String, Object> result = accountService.getAdjustmentWeightAmount(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/SchemeAdjustment")
    public ResponseEntity<Map<String, Object>> getSchemeAdjustment(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String costId) {

        Map<String, Object> result = accountService.getSchemeAdjustment(startDate, endDate, costId);
        return ResponseEntity.ok(result);
    }
}
