package az.monitoringSoftware.monitoringSoftware.webApi.controllers;

import az.monitoringSoftware.monitoringSoftware.business.concretes.SaleManager;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.CreateSaleRequest;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.EndSaleRequest;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.GetSaleByDeskIdRequest;
import az.monitoringSoftware.monitoringSoftware.business.requests.sale.UpdateSaleRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/sale")
public class SaleController {
    private final SaleManager saleManager;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody @Valid CreateSaleRequest createSaleRequest) {
        saleManager.add(createSaleRequest);
    }

    @GetMapping("/getSaleByDeskId/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetSaleByDeskIdRequest> getSaleByDeskIdRequest(@PathVariable String id){
       return ResponseEntity.ok( saleManager.getSaleByDeskIdRequest(UUID.fromString(id)));
    }

    @PostMapping("/endSale")
    @ResponseStatus(HttpStatus.OK)
    public void endSale(@RequestBody @Valid EndSaleRequest endSaleRequest) {
        saleManager.endSaleRequest(endSaleRequest);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody @Valid UpdateSaleRequest updateSaleRequest){
        saleManager.update(updateSaleRequest);
    }
}
