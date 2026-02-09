package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.adapter.dto.ozon.ProfitQueryDto;
import com.example.shopeeerp.adapter.dto.ozon.ProfitResultResponse;
import com.example.shopeeerp.mapper.*;
import com.example.shopeeerp.pojo.*;
import com.example.shopeeerp.service.OrderService;
import com.example.shopeeerp.service.OzonProductService;
import com.example.shopeeerp.service.ProfitService;
import com.example.shopeeerp.service.cache.PurchasePriceCacheService;
import com.example.shopeeerp.util.BigDecimalUtils;
import com.example.shopeeerp.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ProfitService
 * @packageName: com.example.shopeeerp.service.impl
 * @description:
 * @date: 2026/2/4 16:34
 */
@Service
public class ProfitServiceImpl implements ProfitService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    private static BigDecimal nvl(BigDecimal x) { return x == null ? ZERO : x; }
    @Autowired
    private OzonPostingItemMapper ozonPostingItemMapper;
    @Autowired
    private OzonPostingMapper ozonPostingMapper;
    @Autowired
    private OzonProductMapper ozonProductMapper;
    @Autowired
    private ProfitMapper profitMapper;
    @Autowired
    private OzonProfitOperationMapper ozonProfitOperationMapper;
    @Autowired
    private PurchasePriceCacheService purchasePriceCacheService;
    
    @Transactional(rollbackFor = Exception.class)
    public ProfitResultResponse calcByOrder(String orderId){
        OzonPosting ozonPosting = ozonPostingMapper.selectByPostingNumber(orderId);
        List<OzonPostingItem> ozonPostingItems = ozonPostingItemMapper.selectByPostingNumber(orderId);
        if(ozonPosting==null||ozonPostingItems==null||ozonPostingItems.isEmpty()){
            throw new RuntimeException("订单不存在或无明细: " + orderId);
        }
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;

        Long shopId = ozonPosting.getShopId();
        LocalDate bizDate = LocalDate.from(ozonPosting.getInProcessAt());   // 没有的话先用 LocalDate.now()
        String currency = "RUB";    // 没有就默认为 "RUB"
        // 订单平台费
        totalFee = ozonProfitOperationMapper.getPlatformFee(orderId);
        totalFee=totalFee.abs();
        List<Long> skus  = ozonPostingItems.stream().map(OzonPostingItem::getSku).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if(!skus.isEmpty()){
            Map<String, BigDecimal> skuToMap = purchasePriceCacheService.getPurchasePrices(shopId, skus);
            Map<String, BigDecimal> stringBigDecimalMap = allocateFeeByRevenue(ozonPostingItems, totalFee);
            for (OzonPostingItem item : ozonPostingItems) {
                String sku = String.valueOf(item.getSku());
                BigDecimal qty = new BigDecimal(item.getQuantity()); // quantity 是 int/long
                BigDecimal price = nvl(item.getPrice());             // price 是 BigDecimal 更好

                // revenue = 单价 * 数量
                BigDecimal revenue = price.multiply(qty);

                // 采购单价：从商品表/你的服务拿（你自己实现）
//            BigDecimal purchasePrice = nvl(ozonProductService.getPurchasePrice(shopId, sku));
                BigDecimal purchasePrice = skuToMap.getOrDefault(sku, BigDecimal.ZERO);
                if(purchasePrice==null){
                    purchasePrice=BigDecimal.ZERO;
                }
                BigDecimal cost = purchasePrice.multiply(qty);


                BigDecimal fee = stringBigDecimalMap.getOrDefault(sku, BigDecimal.ZERO);
                BigDecimal profit = revenue.subtract(cost).subtract(fee);

                BigDecimal rate = revenue.signum() == 0
                        ? ZERO
                        : profit.divide(revenue, 6, RM);

                ProfitOrderPo po = new ProfitOrderPo();
                po.setShopId(shopId);
                po.setOrderId(orderId);
                po.setSku(sku);
                po.setRevenue(revenue);
                po.setCost(cost);
                po.setPlatformFee(fee);
                po.setProfit(profit);
                po.setProfitRate(rate);
                po.setCurrency(currency == null ? "RUB" : currency);
                po.setBizDate(bizDate == null ? LocalDate.now() : bizDate);
                profitMapper.upsertProfitOrder(po);
                totalRevenue = totalRevenue.add(revenue);
                totalCost = totalCost.add(cost);
            }
        }

        BigDecimal totalProfit = totalRevenue.subtract(totalCost).subtract(totalFee);
        BigDecimal totalRate = totalRevenue.signum() == 0
                ? ZERO
                : totalProfit.divide(totalRevenue, 6, RM);

        ProfitResultResponse res = new ProfitResultResponse();
        res.setRevenue(totalRevenue);
        res.setCost(totalCost);
        res.setPlatformFee(totalFee);
        res.setProfit(totalProfit);
        res.setProfitRate(totalRate); // 建议改成 BigDecimal 最好
        return res;
    }

    private Map<String, BigDecimal> allocateFeeByRevenue(List<OzonPostingItem> items, BigDecimal totalFee) {
        final BigDecimal ZERO = BigDecimal.ZERO;
        final RoundingMode RM = RoundingMode.HALF_UP;

        if (totalFee == null) totalFee = ZERO;
        totalFee = totalFee.setScale(2, RM);

        // 1) 计算每行 revenue 和总 revenue
        Map<String, BigDecimal> lineRevenue = new LinkedHashMap<>();
        BigDecimal totalRevenue = ZERO;

        for (OzonPostingItem it : items) {
            BigDecimal qty = new BigDecimal(it.getQuantity());
            BigDecimal revenue = it.getPrice().multiply(qty).setScale(2, RM);
            lineRevenue.put(String.valueOf(it.getSku()), revenue);
            totalRevenue = totalRevenue.add(revenue);
        }

        // 2) 分摊
        Map<String, BigDecimal> feeMap = new LinkedHashMap<>();
        BigDecimal allocated = ZERO;

        List<String> skus = new ArrayList<>(lineRevenue.keySet());
        for (int i = 0; i < skus.size(); i++) {
            String sku = skus.get(i);
            BigDecimal revenue = lineRevenue.getOrDefault(sku, ZERO);

            BigDecimal fee;
            if (totalRevenue.signum() == 0) {
                fee = ZERO;
            } else if (i == skus.size() - 1) {
                // 尾差修正：最后一行吃掉剩余
                fee = totalFee.subtract(allocated);
            } else {
                BigDecimal ratio = revenue.divide(totalRevenue, 12, RM);
                fee = totalFee.multiply(ratio).setScale(2, RM);
                allocated = allocated.add(fee);
            }
            feeMap.put(sku, fee);
        }
        return feeMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProfitResultResponse summary(ProfitQueryDto query){
        validate(query);
        ProfitResultResponse summary = profitMapper.summary(query);
        return summary;
    }

    public void validate(ProfitQueryDto query){
        if(query==null||query.getShopId()==null){
            throw new RuntimeException("shopId必填");
        }
        if (query.getStartDate() == null || query.getEndDate() == null) throw new RuntimeException("日期必填");
    }
}
