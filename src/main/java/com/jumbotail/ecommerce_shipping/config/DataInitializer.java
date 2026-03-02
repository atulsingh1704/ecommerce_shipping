package com.jumbotail.ecommerce_shipping.config;


import com.jumbotail.ecommerce_shipping.entity.*;
import com.jumbotail.ecommerce_shipping.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds the H2 database with sample data on application startup.
 *
 * Initialises:
 * - 2 Warehouses (BLR_Warehouse, MUMB_Warehouse)
 * - 3 Sellers    (Nestle, Rice Seller, Sugar Seller)
 * - 3 Products   (Maggie 500g, Rice Bag 10Kg, Sugar Bag 25kg)
 * - 2 Customers  (Shree Kirana Store, Andheri Mini Mart)
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(WarehouseRepository warehouseRepo,
                                   SellerRepository sellerRepo,
                                   ProductRepository productRepo,
                                   CustomerRepository customerRepo) {
        return args -> {
            log.info("========== Seeding database with sample data ==========");

            // ── Warehouses ────────────────────────────────────────────────
            Warehouse blr = warehouseRepo.save(Warehouse.builder()
                    .warehouseCode("BLR_Warehouse")
                    .warehouseName("Bangalore Fulfillment Center")
                    .address("Whitefield Industrial Area")
                    .city("Bangalore")
                    .state("Karnataka")
                    .pinCode("560066")
                    .location(new Location(12.99999, 37.923273))
                    .capacityCubicMetres(50000)
                    .contactPhone("08041234567")
                    .operational(true)
                    .build());

            Warehouse mumb = warehouseRepo.save(Warehouse.builder()
                    .warehouseCode("MUMB_Warehouse")
                    .warehouseName("Mumbai Fulfillment Center")
                    .address("Bhiwandi Logistics Park")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .pinCode("421302")
                    .location(new Location(11.99999, 27.923273))
                    .capacityCubicMetres(75000)
                    .contactPhone("02241234567")
                    .operational(true)
                    .build());

            log.info("Seeded warehouses: {}, {}", blr.getWarehouseCode(), mumb.getWarehouseCode());

            // ── Sellers ───────────────────────────────────────────────────
            Seller nestle = sellerRepo.save(Seller.builder()
                    .sellerName("Nestle India Ltd")
                    .contactPerson("Rajesh Kumar")
                    .phoneNumber("9876543210")
                    .email("rajesh@nestle.in")
                    .address("Nestle House, Cunningham Road")
                    .city("Bangalore")
                    .state("Karnataka")
                    .pinCode("560052")
                    .location(new Location(12.9716, 77.5946))   // Bangalore CBD
                    .gstNumber("29AABCN1234A1Z5")
                    .active(true)
                    .rating(4.8)
                    .build());

            Seller riceSeller = sellerRepo.save(Seller.builder()
                    .sellerName("Rice Seller Co.")
                    .contactPerson("Amit Singh")
                    .phoneNumber("9123456789")
                    .email("amit@riceseller.in")
                    .address("Grain Market, Sector 14")
                    .city("Chandigarh")
                    .state("Punjab")
                    .pinCode("160014")
                    .location(new Location(30.7333, 76.7794))   // Chandigarh
                    .gstNumber("03AABCR5678B1Z3")
                    .active(true)
                    .rating(4.5)
                    .build());

            Seller sugarSeller = sellerRepo.save(Seller.builder()
                    .sellerName("Sugar Seller Pvt Ltd")
                    .contactPerson("Priya Patel")
                    .phoneNumber("9988776655")
                    .email("priya@sugarseller.in")
                    .address("Sweet Market Complex")
                    .city("Ahmedabad")
                    .state("Gujarat")
                    .pinCode("380001")
                    .location(new Location(23.0225, 72.5714))   // Ahmedabad
                    .gstNumber("24AABCS9012C1Z1")
                    .active(true)
                    .rating(4.6)
                    .build());

            log.info("Seeded sellers: {}, {}, {}",
                    nestle.getSellerName(), riceSeller.getSellerName(), sugarSeller.getSellerName());

            // ── Products ──────────────────────────────────────────────────
            productRepo.save(Product.builder()
                    .productName("Maggie Noodles 500g Packet")
                    .sku("NESTLE-MAGGIE-500G")
                    .description("Maggie 2-minute noodles, 500g family pack")
                    .category("FMCG")
                    .brand("Maggie")
                    .sellingPrice(10.0)
                    .mrp(12.0)
                    .weightKg(0.5)
                    .lengthCm(10.0)
                    .widthCm(10.0)
                    .heightCm(10.0)
                    .unitOfMeasure("packet")
                    .minimumOrderQuantity(12)
                    .stockQuantity(10000)
                    .active(true)
                    .seller(nestle)
                    .build());

            productRepo.save(Product.builder()
                    .productName("Premium Basmati Rice Bag 10Kg")
                    .sku("RICE-BASMATI-10KG")
                    .description("Premium quality Basmati rice, 10 kg bag")
                    .category("Staples")
                    .brand("RiceSeller Brand")
                    .sellingPrice(500.0)
                    .mrp(550.0)
                    .weightKg(10.0)
                    .lengthCm(100.0)
                    .widthCm(80.0)
                    .heightCm(50.0)
                    .unitOfMeasure("bag")
                    .minimumOrderQuantity(1)
                    .stockQuantity(500)
                    .active(true)
                    .seller(riceSeller)
                    .build());

            productRepo.save(Product.builder()
                    .productName("Refined Sugar Bag 25Kg")
                    .sku("SUGAR-REFINED-25KG")
                    .description("Pure refined white sugar, 25 kg sack")
                    .category("Staples")
                    .brand("SugarSeller Brand")
                    .sellingPrice(700.0)
                    .mrp(750.0)
                    .weightKg(25.0)
                    .lengthCm(100.0)
                    .widthCm(90.0)
                    .heightCm(60.0)
                    .unitOfMeasure("sack")
                    .minimumOrderQuantity(1)
                    .stockQuantity(200)
                    .active(true)
                    .seller(sugarSeller)
                    .build());

            log.info("Seeded products: Maggie 500g, Rice 10Kg, Sugar 25Kg");

            // ── Customers ─────────────────────────────────────────────────
            customerRepo.save(Customer.builder()
                    .customerCode("Cust-123")
                    .storeName("Shree Kirana Store")
                    .ownerName("Suresh Sharma")
                    .phoneNumber("9847123456")
                    .email("suresh@shreekirana.in")
                    .address("12, Gandhi Bazaar Main Road")
                    .city("Mysore")
                    .state("Karnataka")
                    .pinCode("570001")
                    .location(new Location(11.232, 23.445495))
                    .gstNumber("29AABCS1234D1Z2")
                    .active(true)
                    .creditLimit(50000.0)
                    .build());

            customerRepo.save(Customer.builder()
                    .customerCode("Cust-124")
                    .storeName("Andheri Mini Mart")
                    .ownerName("Deepak Verma")
                    .phoneNumber("9101234567")
                    .email("deepak@andherimart.in")
                    .address("Shop 5, Andheri West Market")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .pinCode("400053")
                    .location(new Location(17.232, 33.445495))
                    .gstNumber("27AABCA5678E1Z1")
                    .active(true)
                    .creditLimit(75000.0)
                    .build());

            log.info("Seeded customers: Shree Kirana Store (Cust-123), Andheri Mini Mart (Cust-124)");
            log.info("========== Database seeding complete ==========");
        };
    }
}