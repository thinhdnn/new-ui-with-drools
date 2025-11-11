package rule.engine.org.app.api.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import rule.engine.org.app.api.request.DeclarationRequest;
import rule.engine.org.app.domain.entity.execution.declaration.Declaration;
import rule.engine.org.app.domain.entity.execution.declaration.GovernmentAgencyGoodsItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeclarationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "functionCode", ignore = true)
    @Mapping(target = "typeCode", ignore = true)
    @Mapping(target = "officeId", ignore = true)
    @Mapping(target = "declarationId", source = "uuidReference")
    @Mapping(target = "submissionDateTime", ignore = true)
    @Mapping(target = "ucr", source = "reference")
    @Mapping(target = "declarantId", ignore = true)
    @Mapping(target = "declarantName", source = "agentName")
    @Mapping(target = "declarantCountryId", ignore = true)
    @Mapping(target = "consignorId", ignore = true)
    @Mapping(target = "consignorName", source = "consignorName")
    @Mapping(target = "consignorCountryId", source = "consignorCountry")
    @Mapping(target = "consigneeId", ignore = true)
    @Mapping(target = "consigneeName", source = "consigneeName")
    @Mapping(target = "consigneeCountryId", source = "consigneeCountry")
    @Mapping(target = "importerId", ignore = true)
    @Mapping(target = "importerName", source = "importerName")
    @Mapping(target = "importerCountryId", ignore = true)
    @Mapping(target = "countryOfExportId", ignore = true)
    @Mapping(target = "countryOfImportId", ignore = true)
    @Mapping(target = "countryOfDestinationId", ignore = true)
    @Mapping(target = "incotermCode", source = "incotermCode")
    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "invoiceIssueDateTime", ignore = true)
    @Mapping(target = "invoiceCurrencyCode", ignore = true)
    @Mapping(target = "invoiceAmount", source = "totalInvoiceAmount")
    @Mapping(target = "transportMeansModeCode", source = "transportModeCode")
    @Mapping(target = "transportMeansId", ignore = true)
    @Mapping(target = "transportMeansJourneyId", ignore = true)
    @Mapping(target = "loadingLocationId", source = "portOfLoading")
    @Mapping(target = "unloadingLocationId", source = "portOfFirstDischarge")
    @Mapping(target = "locationOfGoodsId", ignore = true)
    @Mapping(target = "warehouseId", ignore = true)
    @Mapping(target = "packageQuantity", ignore = true)
    @Mapping(target = "marksNumbersId", ignore = true)
    @Mapping(target = "totalGrossMassMeasure", source = "totalGrossWeight")
    @Mapping(target = "totalNetMassMeasure", source = "totalNetWeight")
    @Mapping(target = "totalFreightAmount", ignore = true)
    @Mapping(target = "totalInsuranceAmount", ignore = true)
    @Mapping(target = "otherChargesAmount", ignore = true)
    @Mapping(target = "previousDocumentIds", ignore = true)
    @Mapping(target = "governmentAgencyGoodsItems", source = "declarationItems")
    Declaration toEntity(DeclarationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "declaration", ignore = true)
    @Mapping(target = "sequenceNumeric", ignore = true)
    @Mapping(target = "hsId", source = "hsCode")
    @Mapping(target = "description", source = "commercialGoodsDesc")
    @Mapping(target = "originCountryId", source = "countryOriginCode")
    @Mapping(target = "netWeightMeasure", source = "netWeight")
    @Mapping(target = "grossWeightMeasure", source = "grossWeight")
    @Mapping(target = "quantityQuantity", source = "statisticalQuantity")
    @Mapping(target = "quantityUnitCode", source = "grossWeightUOMCode")
    @Mapping(target = "invoiceLineNumberId", ignore = true)
    @Mapping(target = "unitPriceAmount", source = "declaredTariffUnitPrice")
    @Mapping(target = "statisticalValueAmount", source = "valueLocalCurrency")
    @Mapping(target = "customsValueAmount", source = "declaredCustomsValue")
    @Mapping(target = "previousProcedureCode", ignore = true)
    @Mapping(target = "procedureCode", ignore = true)
    @Mapping(target = "preferenceCode", ignore = true)
    @Mapping(target = "valuationMethodCode", ignore = true)
    @Mapping(target = "dutyRate", ignore = true)
    @Mapping(target = "dutyAmount", source = "declaredDutyAssessment")
    GovernmentAgencyGoodsItem toEntity(DeclarationRequest.ItemRequest itemRequest);

    default List<GovernmentAgencyGoodsItem> mapItems(List<DeclarationRequest.ItemRequest> items) {
        return items == null ? java.util.Collections.emptyList()
            : items.stream().map(this::toEntity).toList();
    }

    @AfterMapping
    default void setBackReferences(@MappingTarget Declaration declaration) {
        List<GovernmentAgencyGoodsItem> items = declaration.getGovernmentAgencyGoodsItems();
        if (items == null || items.isEmpty()) {
            return;
        }
        for (int index = 0; index < items.size(); index++) {
            GovernmentAgencyGoodsItem item = items.get(index);
            item.setDeclaration(declaration);
            if (item.getSequenceNumeric() == null) {
                item.setSequenceNumeric(index + 1);
            }
        }
    }
}


