package com.example.shopeeerp.adapter.dto.ozon;

import java.util.List;

/**
 * Ozon商品详情响应DTO
 */
public class OzonProductInfoResponse {
    private List<ProductInfo> items;
    private Integer total;
    private String last_id;

    public List<ProductInfo> getItems() {
        return items;
    }

    public void setItems(List<ProductInfo> items) {
        this.items = items;
    }

    // 为了兼容，保留result的getter/setter，内部使用items
    public List<ProductInfo> getResult() {
        return items;
    }

    public void setResult(List<ProductInfo> result) {
        this.items = result;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    public static class ProductInfo {
        private Long id;
        private Long sku;
        private String offer_id;
        private String name;
        private String price;
        private String old_price;
        private String min_price;
        private String currency_code;
        private Long description_category_id;
        private Long type_id;
        private String vat;
        private Integer volume_weight;
        private Boolean is_archived;
        private Boolean is_autoarchived;
        private Boolean is_discounted;
        private Boolean is_kgt;
        private Boolean is_prepayment_allowed;
        private Boolean is_super;
        private Boolean has_discounted_fbo_item;
        private Integer discounted_fbo_stocks;
        private List<String> barcodes;
        private List<String> images;
        private List<String> images360;
        private List<String> primary_image;
        private List<String> color_image;
        private List<Availability> availabilities;
        private List<Commission> commissions;
        private List<Error> errors;
        private List<Promotion> promotions;
        private List<Source> sources;
        private ModelInfo model_info;
        private PriceIndexes price_indexes;
        private Statuses statuses;
        private Stocks stocks;
        private VisibilityDetails visibility_details;
        private String created_at;
        private String updated_at;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getSku() {
            return sku;
        }

        public void setSku(Long sku) {
            this.sku = sku;
        }

        public String getOffer_id() {
            return offer_id;
        }

        public void setOffer_id(String offer_id) {
            this.offer_id = offer_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getOld_price() {
            return old_price;
        }

        public void setOld_price(String old_price) {
            this.old_price = old_price;
        }

        public String getMin_price() {
            return min_price;
        }

        public void setMin_price(String min_price) {
            this.min_price = min_price;
        }

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
        }

        public Long getDescription_category_id() {
            return description_category_id;
        }

        public void setDescription_category_id(Long description_category_id) {
            this.description_category_id = description_category_id;
        }

        public Long getType_id() {
            return type_id;
        }

        public void setType_id(Long type_id) {
            this.type_id = type_id;
        }

        public String getVat() {
            return vat;
        }

        public void setVat(String vat) {
            this.vat = vat;
        }

        public Integer getVolume_weight() {
            return volume_weight;
        }

        public void setVolume_weight(Integer volume_weight) {
            this.volume_weight = volume_weight;
        }

        public Boolean getIs_archived() {
            return is_archived;
        }

        public void setIs_archived(Boolean is_archived) {
            this.is_archived = is_archived;
        }

        public Boolean getIs_autoarchived() {
            return is_autoarchived;
        }

        public void setIs_autoarchived(Boolean is_autoarchived) {
            this.is_autoarchived = is_autoarchived;
        }

        public Boolean getIs_discounted() {
            return is_discounted;
        }

        public void setIs_discounted(Boolean is_discounted) {
            this.is_discounted = is_discounted;
        }

        public Boolean getIs_kgt() {
            return is_kgt;
        }

        public void setIs_kgt(Boolean is_kgt) {
            this.is_kgt = is_kgt;
        }

        public Boolean getIs_prepayment_allowed() {
            return is_prepayment_allowed;
        }

        public void setIs_prepayment_allowed(Boolean is_prepayment_allowed) {
            this.is_prepayment_allowed = is_prepayment_allowed;
        }

        public Boolean getIs_super() {
            return is_super;
        }

        public void setIs_super(Boolean is_super) {
            this.is_super = is_super;
        }

        public Boolean getHas_discounted_fbo_item() {
            return has_discounted_fbo_item;
        }

        public void setHas_discounted_fbo_item(Boolean has_discounted_fbo_item) {
            this.has_discounted_fbo_item = has_discounted_fbo_item;
        }

        public Integer getDiscounted_fbo_stocks() {
            return discounted_fbo_stocks;
        }

        public void setDiscounted_fbo_stocks(Integer discounted_fbo_stocks) {
            this.discounted_fbo_stocks = discounted_fbo_stocks;
        }

        public List<String> getBarcodes() {
            return barcodes;
        }

        public void setBarcodes(List<String> barcodes) {
            this.barcodes = barcodes;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<String> getImages360() {
            return images360;
        }

        public void setImages360(List<String> images360) {
            this.images360 = images360;
        }

        public List<String> getPrimary_image() {
            return primary_image;
        }

        public void setPrimary_image(List<String> primary_image) {
            this.primary_image = primary_image;
        }

        public List<String> getColor_image() {
            return color_image;
        }

        public void setColor_image(List<String> color_image) {
            this.color_image = color_image;
        }

        public List<Availability> getAvailabilities() {
            return availabilities;
        }

        public void setAvailabilities(List<Availability> availabilities) {
            this.availabilities = availabilities;
        }

        public List<Commission> getCommissions() {
            return commissions;
        }

        public void setCommissions(List<Commission> commissions) {
            this.commissions = commissions;
        }

        public List<Error> getErrors() {
            return errors;
        }

        public void setErrors(List<Error> errors) {
            this.errors = errors;
        }

        public List<Promotion> getPromotions() {
            return promotions;
        }

        public void setPromotions(List<Promotion> promotions) {
            this.promotions = promotions;
        }

        public List<Source> getSources() {
            return sources;
        }

        public void setSources(List<Source> sources) {
            this.sources = sources;
        }

        public ModelInfo getModel_info() {
            return model_info;
        }

        public void setModel_info(ModelInfo model_info) {
            this.model_info = model_info;
        }

        public PriceIndexes getPrice_indexes() {
            return price_indexes;
        }

        public void setPrice_indexes(PriceIndexes price_indexes) {
            this.price_indexes = price_indexes;
        }

        public Statuses getStatuses() {
            return statuses;
        }

        public void setStatuses(Statuses statuses) {
            this.statuses = statuses;
        }

        public Stocks getStocks() {
            return stocks;
        }

        public void setStocks(Stocks stocks) {
            this.stocks = stocks;
        }

        public VisibilityDetails getVisibility_details() {
            return visibility_details;
        }

        public void setVisibility_details(VisibilityDetails visibility_details) {
            this.visibility_details = visibility_details;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }

    public static class Availability {
        private String availability;
        private List<Reason> reasons;
        private Long sku;
        private String source;

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }

        public List<Reason> getReasons() {
            return reasons;
        }

        public void setReasons(List<Reason> reasons) {
            this.reasons = reasons;
        }

        public Long getSku() {
            return sku;
        }

        public void setSku(Long sku) {
            this.sku = sku;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public static class Reason {
        private HumanText human_text;
        private Integer id;

        public HumanText getHuman_text() {
            return human_text;
        }

        public void setHuman_text(HumanText human_text) {
            this.human_text = human_text;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class HumanText {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Commission {
        private Integer delivery_amount;
        private Integer percent;
        private Integer return_amount;
        private String sale_schema;
        private Integer value;

        public Integer getDelivery_amount() {
            return delivery_amount;
        }

        public void setDelivery_amount(Integer delivery_amount) {
            this.delivery_amount = delivery_amount;
        }

        public Integer getPercent() {
            return percent;
        }

        public void setPercent(Integer percent) {
            this.percent = percent;
        }

        public Integer getReturn_amount() {
            return return_amount;
        }

        public void setReturn_amount(Integer return_amount) {
            this.return_amount = return_amount;
        }

        public String getSale_schema() {
            return sale_schema;
        }

        public void setSale_schema(String sale_schema) {
            this.sale_schema = sale_schema;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    public static class Error {
        private Long attribute_id;
        private String code;
        private String field;
        private String level;
        private String state;
        private ErrorTexts texts;

        public Long getAttribute_id() {
            return attribute_id;
        }

        public void setAttribute_id(Long attribute_id) {
            this.attribute_id = attribute_id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public ErrorTexts getTexts() {
            return texts;
        }

        public void setTexts(ErrorTexts texts) {
            this.texts = texts;
        }
    }

    public static class ErrorTexts {
        private String attribute_name;
        private String description;
        private String hint_code;
        private String message;
        private List<Param> params;
        private String short_description;

        public String getAttribute_name() {
            return attribute_name;
        }

        public void setAttribute_name(String attribute_name) {
            this.attribute_name = attribute_name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getHint_code() {
            return hint_code;
        }

        public void setHint_code(String hint_code) {
            this.hint_code = hint_code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Param> getParams() {
            return params;
        }

        public void setParams(List<Param> params) {
            this.params = params;
        }

        public String getShort_description() {
            return short_description;
        }

        public void setShort_description(String short_description) {
            this.short_description = short_description;
        }
    }

    public static class Param {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ModelInfo {
        private Integer count;
        private Long model_id;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Long getModel_id() {
            return model_id;
        }

        public void setModel_id(Long model_id) {
            this.model_id = model_id;
        }
    }

    public static class PriceIndexes {
        private String color_index;
        private IndexData external_index_data;
        private IndexData ozon_index_data;
        private IndexData self_marketplaces_index_data;

        public String getColor_index() {
            return color_index;
        }

        public void setColor_index(String color_index) {
            this.color_index = color_index;
        }

        public IndexData getExternal_index_data() {
            return external_index_data;
        }

        public void setExternal_index_data(IndexData external_index_data) {
            this.external_index_data = external_index_data;
        }

        public IndexData getOzon_index_data() {
            return ozon_index_data;
        }

        public void setOzon_index_data(IndexData ozon_index_data) {
            this.ozon_index_data = ozon_index_data;
        }

        public IndexData getSelf_marketplaces_index_data() {
            return self_marketplaces_index_data;
        }

        public void setSelf_marketplaces_index_data(IndexData self_marketplaces_index_data) {
            this.self_marketplaces_index_data = self_marketplaces_index_data;
        }
    }

    public static class IndexData {
        private String minimal_price;
        private String minimal_price_currency;
        private Integer price_index_value;

        public String getMinimal_price() {
            return minimal_price;
        }

        public void setMinimal_price(String minimal_price) {
            this.minimal_price = minimal_price;
        }

        public String getMinimal_price_currency() {
            return minimal_price_currency;
        }

        public void setMinimal_price_currency(String minimal_price_currency) {
            this.minimal_price_currency = minimal_price_currency;
        }

        public Integer getPrice_index_value() {
            return price_index_value;
        }

        public void setPrice_index_value(Integer price_index_value) {
            this.price_index_value = price_index_value;
        }
    }

    public static class Promotion {
        private Boolean is_enabled;
        private String type;

        public Boolean getIs_enabled() {
            return is_enabled;
        }

        public void setIs_enabled(Boolean is_enabled) {
            this.is_enabled = is_enabled;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Source {
        private String created_at;
        private String quant_code;
        private String shipment_type;
        private Long sku;
        private String source;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getQuant_code() {
            return quant_code;
        }

        public void setQuant_code(String quant_code) {
            this.quant_code = quant_code;
        }

        public String getShipment_type() {
            return shipment_type;
        }

        public void setShipment_type(String shipment_type) {
            this.shipment_type = shipment_type;
        }

        public Long getSku() {
            return sku;
        }

        public void setSku(Long sku) {
            this.sku = sku;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public static class Statuses {
        private Boolean is_created;
        private String moderate_status;
        private String status;
        private String status_description;
        private String status_failed;
        private String status_name;
        private String status_tooltip;
        private String status_updated_at;
        private String validation_status;

        public Boolean getIs_created() {
            return is_created;
        }

        public void setIs_created(Boolean is_created) {
            this.is_created = is_created;
        }

        public String getModerate_status() {
            return moderate_status;
        }

        public void setModerate_status(String moderate_status) {
            this.moderate_status = moderate_status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus_description() {
            return status_description;
        }

        public void setStatus_description(String status_description) {
            this.status_description = status_description;
        }

        public String getStatus_failed() {
            return status_failed;
        }

        public void setStatus_failed(String status_failed) {
            this.status_failed = status_failed;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }

        public String getStatus_tooltip() {
            return status_tooltip;
        }

        public void setStatus_tooltip(String status_tooltip) {
            this.status_tooltip = status_tooltip;
        }

        public String getStatus_updated_at() {
            return status_updated_at;
        }

        public void setStatus_updated_at(String status_updated_at) {
            this.status_updated_at = status_updated_at;
        }

        public String getValidation_status() {
            return validation_status;
        }

        public void setValidation_status(String validation_status) {
            this.validation_status = validation_status;
        }
    }

    public static class Stocks {
        private Boolean has_stock;
        private List<Stock> stocks;

        public Boolean getHas_stock() {
            return has_stock;
        }

        public void setHas_stock(Boolean has_stock) {
            this.has_stock = has_stock;
        }

        public List<Stock> getStocks() {
            return stocks;
        }

        public void setStocks(List<Stock> stocks) {
            this.stocks = stocks;
        }
    }

    public static class Stock {
        private Integer present;
        private Integer reserved;
        private Long sku;
        private String source;

        public Integer getPresent() {
            return present;
        }

        public void setPresent(Integer present) {
            this.present = present;
        }

        public Integer getReserved() {
            return reserved;
        }

        public void setReserved(Integer reserved) {
            this.reserved = reserved;
        }

        public Long getSku() {
            return sku;
        }

        public void setSku(Long sku) {
            this.sku = sku;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public static class VisibilityDetails {
        private Boolean has_price;
        private Boolean has_stock;

        public Boolean getHas_price() {
            return has_price;
        }

        public void setHas_price(Boolean has_price) {
            this.has_price = has_price;
        }

        public Boolean getHas_stock() {
            return has_stock;
        }

        public void setHas_stock(Boolean has_stock) {
            this.has_stock = has_stock;
        }
    }
}
