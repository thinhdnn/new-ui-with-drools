package rule.engine.org.app.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for declaration ingestion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeclarationRequest {

    @NotBlank
    private String uuidReference;

    private String reference;
    private Integer regimeCode;
    private String exporterName;
    private String importerName;
    private String consignorName;
    private String consigneeName;
    private BigDecimal totalGrossWeight;
    private BigDecimal totalNetWeight;
    private String paymentMethod;
    private String regimeName;
    private String officeEntry;
    private String officeDeclaration;
    private String agentName;
    private String consigneeCountry;
    private String consignorCountry;
    private BigDecimal totalInvoiceAmount;
    private String portOfLoading;
    private String portOfFirstDischarge;
    private String transportModeCode;
    private String paymentMethodCode;
    private String procedureCode;
    private String incotermCode;

    @NotNull
    private List<ItemRequest> declarationItems;

    private List<ContainerRequest> containers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ItemRequest {

        private String uuidReference;
        private String hsCode;
        private BigDecimal statisticalQuantity;
        private BigDecimal supplementaryQuantity;
        private String commercialGoodsDesc;
        private BigDecimal valueForeignCurrency;
        private BigDecimal valueLocalCurrency;
        private BigDecimal dutyAssessment;
        private BigDecimal grossWeight;
        private BigDecimal netWeight;
        private String countryOriginCode;
        private BigDecimal declaredCustomsValue;
        private BigDecimal declaredDutyAssessment;
        private BigDecimal declaredTariffUnitPrice;
        private BigDecimal declaredTariffQuantity;
        private Integer numberOfPackages;
        private String grossWeightUOMCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContainerRequest {

        private String containerNo;
        private String reference;
        private String sealNo;
    }
}

