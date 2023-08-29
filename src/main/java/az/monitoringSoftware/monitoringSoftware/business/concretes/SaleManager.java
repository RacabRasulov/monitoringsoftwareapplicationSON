package az.monitoringSoftware.monitoringSoftware.business.concretes;

import az.monitoringSoftware.monitoringSoftware.business.abstracts.SaleService;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.CreateSaleRequest;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.EndSaleRequest;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.GetSaleByDeskIdRequest;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.UpdateSaleRequest;
import az.monitoringSoftware.monitoringSoftware.business.requests.saleProduct.CreatSaleProductRequest;
import az.monitoringSoftware.monitoringSoftware.core.utilities.mappers.ModelMapperManager;
import az.monitoringSoftware.monitoringSoftware.dataAccess.abstracts.DeskRepository;
import az.monitoringSoftware.monitoringSoftware.dataAccess.abstracts.ProductRepository;
import az.monitoringSoftware.monitoringSoftware.dataAccess.abstracts.SaleRepository;
import az.monitoringSoftware.monitoringSoftware.domain.entities.Desk;
import az.monitoringSoftware.monitoringSoftware.domain.entities.Product;
import az.monitoringSoftware.monitoringSoftware.domain.entities.Sale;
import az.monitoringSoftware.monitoringSoftware.domain.entities.SaleProduct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class SaleManager implements SaleService {
    private final SaleRepository saleRepository;
    private final ModelMapperManager modelMapperManager;
    private final DeskRepository deskRepository;
    private final ProductRepository productRepository;


    @Override
    public void add(CreateSaleRequest createSaleRequest) {
        Optional<Desk> desk = deskRepository.findById(UUID.fromString(String
                .valueOf(createSaleRequest.getDeskId())));

        Sale sale = new Sale();

        for (CreatSaleProductRequest createSaleProduct : createSaleRequest.getSaleProducts()) {
            SaleProduct saleProduct = new SaleProduct();
            saleProduct.setProductId(createSaleProduct.getProductId());
            saleProduct.setName(createSaleProduct.getName());
            saleProduct.setPrice(createSaleProduct.getPrice());
            saleProduct.setOrderCount(createSaleProduct.getOrderCount());
            saleProduct.setCost(createSaleProduct.getCost());
            saleProduct.setNameOfSeller(createSaleProduct.getNameOfSeller());
            sale.getSaleProducts().add(saleProduct);
        }

        sale.setStartDate(createSaleRequest.getStartDate().toLocalDateTime());
        sale.setHour(createSaleRequest.getHour());
        sale.setMinutes(createSaleRequest.getMinutes());
        sale.setIsDefaultTimeChecked(createSaleRequest.getIsDefaultTimeChecked());
        sale.setDeskId(createSaleRequest.getDeskId());
        sale.setDeskName(desk.get().getName());
        sale.setDeviceId(desk.get().getDevice().getId());
        sale.setDeviceName(desk.get().getDevice().getName());
        sale.setDevicePrice(desk.get().getDevice().getPrice());
        for (SaleProduct product : sale.getSaleProducts()) {
            product.setSale(sale);
        }
        saleRepository.save(sale);
    }

    @Override
    public GetSaleByDeskIdRequest getSaleByDeskIdRequest(UUID id) {

        var saleDetails = saleRepository.findSalesByDeskIdAndIsSaleNotEnded(id);

        if (saleDetails == null)
            return null;
        else {
            return modelMapperManager.forResponse()
                    .map(saleDetails, GetSaleByDeskIdRequest.class);
        }
    }

    @Override
    public void endSaleRequest(EndSaleRequest endSaleRequest) {

        Optional<Sale> sale = saleRepository.findSalesByDeskIdAndIsSaleNotEnded(endSaleRequest.getDeskId());

        for(SaleProduct saleProduct : sale.get().getSaleProducts()){
            //update product stock count
            Optional<Product> product = productRepository.findById(saleProduct.getProductId());
            product.get().setStockCount(product.get().getCount() - saleProduct.getOrderCount());
            productRepository.save(product.get());
        }


        sale.get().setTotalAmount(endSaleRequest.getTotalAmount());
        sale.get().setEndDate(endSaleRequest.getEndDate().toLocalDateTime());
        sale.get().setTotalGameAmount(endSaleRequest.getTotalGameAmount());
        sale.get().setTotalProductAmount(endSaleRequest.getTotalProductAmount());
        sale.get().setIsSaleEnded(endSaleRequest.getIsSaleEnded());

        Duration duration = Duration.between(sale.get().getStartDate(), sale.get().getEndDate());
        long minutesDifference = duration.toMinutes();
        sale.get().setTotalMinutes(minutesDifference);

        saleRepository.save(sale.get());

    }

    @Override
    public void update(UpdateSaleRequest updateSaleRequest) {
        saleRepository.deleteByDeskId(updateSaleRequest.getDeskId());

        Optional<Desk> desk = deskRepository.findById(updateSaleRequest.getDeskId());

        Sale sale = new Sale();

        for (CreatSaleProductRequest createSaleProduct : updateSaleRequest.getSaleProducts()) {
            SaleProduct saleProduct = new SaleProduct();
            saleProduct.setProductId(createSaleProduct.getProductId());
            saleProduct.setName(createSaleProduct.getName());
            saleProduct.setPrice(createSaleProduct.getPrice());
            saleProduct.setOrderCount(createSaleProduct.getOrderCount());
            saleProduct.setCost(createSaleProduct.getCost());
            saleProduct.setNameOfSeller(createSaleProduct.getNameOfSeller());
            sale.getSaleProducts().add(saleProduct);
        }

        sale.setUpdatedAt(updateSaleRequest.getStartDate().toLocalDateTime());
        sale.setHour(updateSaleRequest.getHour());
        sale.setMinutes(updateSaleRequest.getMinutes());
        sale.setIsDefaultTimeChecked(updateSaleRequest.getIsDefaultTimeChecked());
        sale.setDeskId(updateSaleRequest.getDeskId());
        sale.setDeskName(desk.get().getName());
        sale.setDeviceId(desk.get().getDevice().getId());
        sale.setDeviceName(desk.get().getDevice().getName());
        sale.setDevicePrice(desk.get().getDevice().getPrice());
        for (SaleProduct product : sale.getSaleProducts()) {
            product.setSale(sale);
        }
        saleRepository.save(sale);
    }
}




