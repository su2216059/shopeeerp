package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.adapter.dto.ozon.OzonCashflowResponse;
import com.example.shopeeerp.adapter.impl.OzonAdapter;
import com.example.shopeeerp.pojo.*;
import com.example.shopeeerp.service.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

@Service
public class OzonCashflowSyncServiceImpl implements OzonCashflowSyncService {

    @Autowired
    private OzonAdapter ozonAdapter;
    @Value("${ozon.shop-id:1}")
    private Long defaultShopId;
    @Autowired
    private OzonCashflowPeriodService periodService;
    @Autowired
    private OzonCashflowSummaryService summaryService;
    @Autowired
    private OzonCashflowDetailService detailService;
    @Autowired
    private OzonCashflowPaymentService paymentService;
    @Autowired
    private OzonCashflowDeliveryItemService deliveryItemService;
    @Autowired
    private OzonCashflowReturnItemService returnItemService;
    @Autowired
    private OzonCashflowServiceItemService serviceItemService;
    @Autowired
    private OzonCashflowOtherItemService otherItemService;

    @Override
    @Transactional
    public void sync(LocalDateTime from, LocalDateTime to) {
        String startIso = toIso(from != null ? from : LocalDateTime.now().minusDays(1));
        String endIso = toIso(to != null ? to : LocalDateTime.now());

        List<OzonCashflowResponse> responses = ozonAdapter.fetchCashflows(startIso, endIso, true, 50);
        for (OzonCashflowResponse resp : responses) {
            if (resp == null || resp.getResult() == null) {
                continue;
            }
            OzonCashflowResponse.Result result = resp.getResult();

            // 汇总 cash_flows
            List<OzonCashflowSummary> summaries = new ArrayList<>();
            if (result.getCashFlows() != null) {
                for (OzonCashflowResponse.CashFlow cf : result.getCashFlows()) {
                    if (cf == null || cf.getPeriod() == null) {
                        continue;
                    }
                    Long periodId = buildStablePeriodId(cf.getPeriod());
                    if (periodId == null) {
                        continue;
                    }
                    OzonCashflowPeriod period = buildPeriod(cf.getPeriod(), resp.getPageCount(), periodId);
                    upsertPeriod(period);

                    OzonCashflowSummary summary = new OzonCashflowSummary();
                    summary.setPeriodId(periodId);
                    summary.setCommissionAmount(cf.getCommissionAmount());
                    summary.setCurrencyCode(cf.getCurrencyCode());
                    summary.setItemDeliveryReturnAmount(cf.getItemDeliveryAndReturnAmount());
                    summary.setOrdersAmount(cf.getOrdersAmount());
                    summary.setReturnsAmount(cf.getReturnsAmount());
                    summary.setServicesAmount(cf.getServicesAmount());
                    summaries.add(summary);
                }
                // 按 period 分批删除再写入，避免跨 period 数据互相覆盖
                summaries.stream()
                        .map(OzonCashflowSummary::getPeriodId)
                        .distinct()
                        .forEach(pid -> summaryService.deleteByPeriodId(pid));
                summaryService.saveBatch(summaries);
            }

            // 详情 details
            if (result.getDetails() != null && !result.getDetails().isEmpty()) {
                for (OzonCashflowResponse.Details detailResp : result.getDetails()) {
                    if (detailResp == null || detailResp.getPeriod() == null) {
                        continue;
                    }
                    Long periodId = buildStablePeriodId(detailResp.getPeriod());
                    if (periodId == null) {
                        continue;
                    }
                    OzonCashflowPeriod period = buildPeriod(detailResp.getPeriod(), resp.getPageCount(), periodId);
                    upsertPeriod(period);

                    // detail
                    OzonCashflowDetail detail = buildDetail(periodId, detailResp);
                    detailService.deleteByPeriodId(periodId);
                    detailService.save(detail);

                    // payments
                    if (detailResp.getPayments() != null) {
                        List<OzonCashflowPayment> payments = new ArrayList<>();
                        detailResp.getPayments().forEach(p -> {
                            OzonCashflowPayment pay = new OzonCashflowPayment();
                            pay.setPeriodId(periodId);
                            pay.setPayment(p.getPayment());
                            pay.setCurrencyCode(p.getCurrencyCode());
                            payments.add(pay);
                        });
                        paymentService.deleteByPeriodId(periodId);
                        paymentService.saveBatch(payments);
                    }

                    // delivery services
                    if (detailResp.getDelivery() != null && detailResp.getDelivery().getDeliveryServices() != null
                            && detailResp.getDelivery().getDeliveryServices().getItems() != null) {
                        List<OzonCashflowDeliveryItem> list = new ArrayList<>();
                        detailResp.getDelivery().getDeliveryServices().getItems().forEach(it -> {
                            OzonCashflowDeliveryItem item = new OzonCashflowDeliveryItem();
                            item.setPeriodId(periodId);
                            item.setName(it.getName());
                            item.setPrice(it.getPrice());
                            list.add(item);
                        });
                        deliveryItemService.deleteByPeriodId(periodId);
                        deliveryItemService.saveBatch(list);
                    }

                    // return services
                    if (detailResp.getReturnBlock() != null && detailResp.getReturnBlock().getReturnServices() != null
                            && detailResp.getReturnBlock().getReturnServices().getItems() != null) {
                        List<OzonCashflowReturnItem> list = new ArrayList<>();
                        detailResp.getReturnBlock().getReturnServices().getItems().forEach(it -> {
                            OzonCashflowReturnItem item = new OzonCashflowReturnItem();
                            item.setPeriodId(periodId);
                            item.setName(it.getName());
                            item.setPrice(it.getPrice());
                            list.add(item);
                        });
                        returnItemService.deleteByPeriodId(periodId);
                        returnItemService.saveBatch(list);
                    }

                    // services items
                if (detailResp.getServices() != null && detailResp.getServices().getItems() != null) {
                    List<OzonCashflowServiceItem> list = new ArrayList<>();
                    detailResp.getServices().getItems().forEach(it -> {
                        OzonCashflowServiceItem item = new OzonCashflowServiceItem();
                        item.setPeriodId(periodId);
                        item.setName(it.getName());
                        item.setPrice(it.getPrice());
                        list.add(item);
                        });
                        serviceItemService.deleteByPeriodId(periodId);
                        serviceItemService.saveBatch(list);
                    }

                    // others items
                if (detailResp.getOthers() != null && detailResp.getOthers().getItems() != null) {
                    List<OzonCashflowOtherItem> list = new ArrayList<>();
                    detailResp.getOthers().getItems().forEach(it -> {
                        OzonCashflowOtherItem item = new OzonCashflowOtherItem();
                        item.setPeriodId(periodId);
                        item.setName(it.getName());
                        item.setPrice(it.getPrice());
                        list.add(item);
                        });
                        otherItemService.deleteByPeriodId(periodId);
                        otherItemService.saveBatch(list);
                    }
                }
            }
        }
    }

    private void upsertPeriod(OzonCashflowPeriod period) {
        if (period == null || period.getId() == null) {
            return;
        }
        OzonCashflowPeriod exists = periodService.getById(period.getId());
        if (exists == null) {
            periodService.save(period);
        } else {
            period.setCreatedAt(exists.getCreatedAt());
            periodService.update(period);
        }
    }

    private OzonCashflowPeriod buildPeriod(OzonCashflowResponse.Period p, Integer pageCount, Long periodId) {
        if (p == null || periodId == null) {
            return null;
        }
        OzonCashflowPeriod period = new OzonCashflowPeriod();
        period.setId(periodId);
        period.setShopId(defaultShopId);
        period.setBeginTime(parseDateTime(p.getBegin()));
        period.setEndTime(parseDateTime(p.getEnd()));
        period.setPageCount(pageCount);
        period.setCreatedAt(LocalDateTime.now());
        period.setUpdatedAt(LocalDateTime.now());
        return period;
    }

    private Long buildStablePeriodId(OzonCashflowResponse.Period p) {
        if (p == null) {
            return null;
        }
        String begin = Strings.isBlank(p.getBegin()) ? "" : p.getBegin().trim();
        String end = Strings.isBlank(p.getEnd()) ? "" : p.getEnd().trim();
        if (Strings.isBlank(begin) && Strings.isBlank(end)) {
            return p.getId();
        }
        String raw = begin + "|" + end + "|shop:" + defaultShopId;
        return Math.abs(UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits());
    }

    private OzonCashflowDetail buildDetail(Long periodId, OzonCashflowResponse.Details d) {
        OzonCashflowDetail detail = new OzonCashflowDetail();
        detail.setPeriodId(periodId);
        detail.setBeginBalanceAmount(d.getBeginBalanceAmount());
        if (d.getDelivery() != null) {
            detail.setDeliveryTotal(d.getDelivery().getTotal());
            detail.setDeliveryAmount(d.getDelivery().getAmount());
        }
        if (d.getReturnBlock() != null) {
            detail.setReturnTotal(d.getReturnBlock().getTotal());
            detail.setReturnAmount(d.getReturnBlock().getAmount());
        }
        detail.setLoan(d.getLoan());
        detail.setInvoiceTransfer(d.getInvoiceTransfer());
        if (d.getRfbs() != null) {
            detail.setRfbsTotal(d.getRfbs().getTotal());
            detail.setRfbsTransferDelivery(d.getRfbs().getTransferDelivery());
            detail.setRfbsTransferDeliveryReturn(d.getRfbs().getTransferDeliveryReturn());
            detail.setRfbsCompensationDeliveryReturn(d.getRfbs().getCompensationDeliveryReturn());
            detail.setRfbsPartialCompensation(d.getRfbs().getPartialCompensation());
            detail.setRfbsPartialCompensationReturn(d.getRfbs().getPartialCompensationReturn());
        }
        if (d.getServices() != null) {
            detail.setServicesTotal(d.getServices().getTotal());
        }
        if (d.getOthers() != null) {
            detail.setOthersTotal(d.getOthers().getTotal());
        }
        detail.setEndBalanceAmount(d.getEndBalanceAmount());
        detail.setCreatedAt(LocalDateTime.now());
        return detail;
    }

    private LocalDateTime parseDateTime(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(value.trim(), DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private String toIso(LocalDateTime time) {
        return time.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
