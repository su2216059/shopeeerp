package com.example.shopeeerp.adapter.dto.ozon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Ozon商品详情响应DTO
 */
public class OzonProductInfoResponse {
    private List<ProductInfo> result;
    private Integer total;
    private String last_id;

    public List<ProductInfo> getResult() {
        return result;
    }

    public void setResult(List<ProductInfo> result) {
        this.result = result;
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
        private String barcode;
        private Long category_id;
        private String name;
        private String offer_id;
        private Integer height;
        private Integer depth;
        private Integer width;
        private String dimension_unit;
        private Integer weight;
        private String weight_unit;
        private List<Image> images;
        private List<Image> images360;
        private List<String> pdf_list;
        private List<Attribute> attributes;
        private List<ComplexAttribute> complex_attributes;
        private String color_image;
        private String last_id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public Long getCategory_id() {
            return category_id;
        }

        public void setCategory_id(Long category_id) {
            this.category_id = category_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOffer_id() {
            return offer_id;
        }

        public void setOffer_id(String offer_id) {
            this.offer_id = offer_id;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getDepth() {
            return depth;
        }

        public void setDepth(Integer depth) {
            this.depth = depth;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public String getDimension_unit() {
            return dimension_unit;
        }

        public void setDimension_unit(String dimension_unit) {
            this.dimension_unit = dimension_unit;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public String getWeight_unit() {
            return weight_unit;
        }

        public void setWeight_unit(String weight_unit) {
            this.weight_unit = weight_unit;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public List<Image> getImages360() {
            return images360;
        }

        public void setImages360(List<Image> images360) {
            this.images360 = images360;
        }

        public List<String> getPdf_list() {
            return pdf_list;
        }

        public void setPdf_list(List<String> pdf_list) {
            this.pdf_list = pdf_list;
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<Attribute> attributes) {
            this.attributes = attributes;
        }

        public List<ComplexAttribute> getComplex_attributes() {
            return complex_attributes;
        }

        public void setComplex_attributes(List<ComplexAttribute> complex_attributes) {
            this.complex_attributes = complex_attributes;
        }

        public String getColor_image() {
            return color_image;
        }

        public void setColor_image(String color_image) {
            this.color_image = color_image;
        }

        public String getLast_id() {
            return last_id;
        }

        public void setLast_id(String last_id) {
            this.last_id = last_id;
        }
    }

    public static class Image {
        @JsonProperty("file_name")
        private String file_name;
        
        @JsonProperty("default")
        private Boolean default_image;
        
        @JsonProperty("index")
        private Integer index;

        @JsonProperty("file_name")
        public String getFile_name() {
            return file_name;
        }

        @JsonProperty("file_name")
        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        @JsonProperty("default")
        public Boolean getDefault_image() {
            return default_image;
        }

        @JsonProperty("default")
        public void setDefault_image(Boolean default_image) {
            this.default_image = default_image;
        }

        @JsonProperty("index")
        public Integer getIndex() {
            return index;
        }

        @JsonProperty("index")
        public void setIndex(Integer index) {
            this.index = index;
        }
    }

    public static class Attribute {
        private Long attribute_id;
        private Long complex_id;
        private List<AttributeValue> values;

        public Long getAttribute_id() {
            return attribute_id;
        }

        public void setAttribute_id(Long attribute_id) {
            this.attribute_id = attribute_id;
        }

        public Long getComplex_id() {
            return complex_id;
        }

        public void setComplex_id(Long complex_id) {
            this.complex_id = complex_id;
        }

        public List<AttributeValue> getValues() {
            return values;
        }

        public void setValues(List<AttributeValue> values) {
            this.values = values;
        }
    }

    public static class AttributeValue {
        private Long dictionary_value_id;
        private String value;

        public Long getDictionary_value_id() {
            return dictionary_value_id;
        }

        public void setDictionary_value_id(Long dictionary_value_id) {
            this.dictionary_value_id = dictionary_value_id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ComplexAttribute {
        // 复杂属性的结构可以根据实际需要扩展
        // 目前响应中为空数组，暂时保留占位
    }
}
