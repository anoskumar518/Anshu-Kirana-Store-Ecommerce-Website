package com.kirana.config;

import com.kirana.entity.*;
import com.kirana.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final OfferRepository offerRepository;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           ProductVariantRepository variantRepository,
                           InventoryRepository inventoryRepository,
                           StockMovementRepository stockMovementRepository,
                           OfferRepository offerRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.offerRepository = offerRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initRolesAndUsers();
        initCategoriesAndProducts();
        initOffers();
    }

    private void initRolesAndUsers() {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN, "Store Administrator")));

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_CUSTOMER, "Village Customer")));

        // Ensure Default Admin User: admin@anshukirana.com / admin123
        if (!userRepository.existsByPhone("7993542873")) {
            User admin = new User();
            admin.setFullName("Anshu Kirana Owner");
            admin.setEmail("admin@anshukirana.com");
            admin.setPhone("7993542873");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setPreferredLanguage("en");
            admin.setIsActive(true);

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(customerRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        } else {
            userRepository.findByPhone("7993542873").ifPresent(user -> {
                user.setPasswordHash(passwordEncoder.encode("admin123"));
                userRepository.save(user);
            });
        }

        // Ensure Default Customer User: customer@village.com / customer123
        if (!userRepository.existsByPhone("9876543211")) {
            User customer = new User();
            customer.setFullName("Venkat Rao");
            customer.setEmail("customer@village.com");
            customer.setPhone("9876543211");
            customer.setPasswordHash(passwordEncoder.encode("customer123"));
            customer.setPreferredLanguage("te");
            customer.setIsActive(true);
            customer.setRoles(Collections.singleton(customerRole));

            userRepository.save(customer);
        } else {
            userRepository.findByPhone("9876543211").ifPresent(user -> {
                user.setPasswordHash(passwordEncoder.encode("customer123"));
                userRepository.save(user);
            });
        }
    }

    private void initCategoriesAndProducts() {
        Map<String, Category> catMap = new HashMap<>();

        // 1. Seed Categories if none exist
        if (categoryRepository.count() == 0) {
            List<Category> categoriesToSave = List.of(
                createCategory("Rice & Grains", "బియ్యం మరియు ధాన్యాలు", "rice-grains", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500", 1),
                createCategory("Dals & Pulses", "పప్పులు", "dals-pulses", "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500", 2),
                createCategory("Cooking Oil & Ghee", "నూనె మరియు నెయ్యి", "oil-ghee", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500", 3),
                createCategory("Spices & Masalas", "మసాలాలు", "spices-masalas", "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500", 4),
                createCategory("Sugar & Salt", "చక్కెర మరియు ఉప్పు", "sugar-salt", "https://images.unsplash.com/photo-1622484212850-eb596d769edc?w=500", 5),
                createCategory("Biscuits & Snacks", "బిస్కెట్లు మరియు స్నాక్స్", "biscuits-snacks", "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500", 6),
                createCategory("Tea & Coffee", "టీ మరియు కాఫీ", "tea-coffee", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500", 7),
                createCategory("Dairy & Daily Essentials", "పాల ఉత్పత్తులు మరియు నిత్యావసరాలు", "dairy-essentials", "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500", 8),
                createCategory("Personal Care", "వ్యక్తిగత సంరక్షణ", "personal-care", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500", 9),
                createCategory("Household Cleaning", "ఇంటి శుభ్రత", "household-cleaning", "https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=500", 10)
            );

            for (Category c : categoriesToSave) {
                Category saved = categoryRepository.save(c);
                catMap.put(saved.getSlug(), saved);
            }
        } else {
            categoryRepository.findAll().forEach(c -> catMap.put(c.getSlug(), c));
        }

        // 2. Seed Products if none exist
        if (productRepository.count() == 0) {
            Category riceCat = catMap.get("rice-grains");
            Category dalCat = catMap.get("dals-pulses");
            Category oilCat = catMap.get("oil-ghee");
            Category spiceCat = catMap.get("spices-masalas");
            Category sugarCat = catMap.get("sugar-salt");
            Category snackCat = catMap.get("biscuits-snacks");
            Category teaCat = catMap.get("tea-coffee");
            Category dairyCat = catMap.get("dairy-essentials");
            Category cleanCat = catMap.get("household-cleaning");

            // Product 1: Sona Masoori Rice
            seedProductWithVariants(
                riceCat, "Kurnool Sona Masoori Rice (Old Crop)", "కర్నూల్ సోనా మసూరి బియ్యం (పాత బియ్యం)", "Anshu Select",
                "Premium aged 1-year old Kurnool Sona Masoori raw rice, lightweight and aromatic.",
                "పాత కర్నూల్ సోనా మసూరి బియ్యం, అన్నం పొడిపొడిగా మరియు రుచికరంగా వస్తుంది.",
                "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500", true,
                List.of(
                    new VariantSeed("5 kg Bag", "RICE-SM-5KG", 350.00, 315.00, 10, false, 25),
                    new VariantSeed("10 kg Bag", "RICE-SM-10KG", 680.00, 599.00, 12, false, 20),
                    new VariantSeed("25 kg Bag", "RICE-SM-25KG", 1650.00, 1450.00, 12, true, 35)
                )
            );

            // Product 2: Aashirvaad Atta
            seedProductWithVariants(
                riceCat, "Aashirvaad Shudh Chakki Atta", "ఆశీర్వాద్ షుద్ధ్ చక్కీ గోధుమ పిండి", "Aashirvaad",
                "100% whole wheat flour made with heavy grains, ensures soft rotis.",
                "100% స్వచ్ఛమైన గోధుమ పిండి. మృదువైన రోటీలకు ప్రసిద్ధి.",
                "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500", true,
                List.of(
                    new VariantSeed("1 kg Pouch", "ATTA-AASH-1KG", 65.00, 58.00, 11, false, 40),
                    new VariantSeed("5 kg Bag", "ATTA-AASH-5KG", 310.00, 275.00, 11, true, 30),
                    new VariantSeed("10 kg Bag", "ATTA-AASH-10KG", 590.00, 520.00, 12, false, 15)
                )
            );

            // Product 3: Toor Dal
            seedProductWithVariants(
                dalCat, "Unpolished Desi Toor Dal", "నాటు కందిపప్పు (పాలిష్ లేనిది)", "Farm Fresh",
                "High-protein, naturally sun-dried unpolished Toor Dal with rich yellow color.",
                "సహజసిద్ధమైన పాలిష్ లేని నాటు కందిపప్పు, సాంబారు మరియు పప్పుకు శ్రేష్ఠమైనది.",
                "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500", true,
                List.of(
                    new VariantSeed("500 g", "DAL-TOOR-500G", 95.00, 85.00, 11, false, 30),
                    new VariantSeed("1 kg", "DAL-TOOR-1KG", 185.00, 165.00, 11, true, 45),
                    new VariantSeed("2 kg", "DAL-TOOR-2KG", 370.00, 320.00, 14, false, 20)
                )
            );

            // Product 4: Moong Dal
            seedProductWithVariants(
                dalCat, "Yellow Moong Dal (Split)", "పసుపు పెసరపప్పు", "Anshu Daily",
                "Easily digestible, high-quality split yellow moong dal for khichdi and dal.",
                "సులభంగా జీర్ణమయ్యే శుభ్రమైన పెసరపప్పు.",
                "https://images.unsplash.com/photo-1585994192701-f1a505c8574a?w=500", false,
                List.of(
                    new VariantSeed("500 g", "DAL-MOONG-500G", 75.00, 68.00, 9, false, 25),
                    new VariantSeed("1 kg", "DAL-MOONG-1KG", 145.00, 130.00, 10, true, 30)
                )
            );

            // Product 5: Sunflower Oil
            seedProductWithVariants(
                oilCat, "Freedom Refined Sunflower Oil", "ఫ్రీడమ్ రిఫైన్డ్ సన్‌ఫ్లవర్ ఆయిల్", "Freedom",
                "Rich in Vitamin E and Omega 6, light and non-sticky cooking oil.",
                "విటమిన్ E మరియు ఒమేగా 6 తో తేలికైన వంట నూనె.",
                "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500", true,
                List.of(
                    new VariantSeed("1 Litre Pouch", "OIL-SUN-1L", 145.00, 128.00, 12, true, 50),
                    new VariantSeed("5 Litre Can", "OIL-SUN-5L", 720.00, 625.00, 13, false, 20)
                )
            );

            // Product 6: Cow Ghee
            seedProductWithVariants(
                oilCat, "Heritage Pure Cow Ghee", "హెరిటేజ్ స్వచ్ఛమైన ఆవు నెయ్యి", "Heritage",
                "Traditional granular golden cow ghee with rich aroma and traditional taste.",
                "సాంప్రదాయ సువాసనతో కూడిన స్వచ్ఛమైన ఆవు నెయ్యి.",
                "https://images.unsplash.com/photo-1631451095765-2c91616fc9e6?w=500", true,
                List.of(
                    new VariantSeed("200 ml Jar", "GHEE-COW-200ML", 165.00, 150.00, 9, false, 25),
                    new VariantSeed("500 ml Jar", "GHEE-COW-500ML", 390.00, 360.00, 8, true, 30),
                    new VariantSeed("1 Litre Tin", "GHEE-COW-1L", 760.00, 699.00, 8, false, 15)
                )
            );

            // Product 7: Red Chilli Powder
            seedProductWithVariants(
                spiceCat, "Everest Tikhalal Red Chilli Powder", "ఎవరెస్ట్ తీఖాలాల్ కారం పొడి", "Everest",
                "Fiery hot and vibrant red colour chilli powder ground from selected chillies.",
                "ఎంచుకున్న ఎండుమిర్చి నుండి తయారైన ఘాటైన ఎర్రటి కారం పొడి.",
                "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500", false,
                List.of(
                    new VariantSeed("250 g", "SPICE-CHILLI-250G", 90.00, 80.00, 11, false, 30),
                    new VariantSeed("500 g", "SPICE-CHILLI-500G", 175.00, 155.00, 11, true, 25),
                    new VariantSeed("1 kg", "SPICE-CHILLI-1KG", 340.00, 299.00, 12, false, 15)
                )
            );

            // Product 8: Turmeric Powder
            seedProductWithVariants(
                spiceCat, "Tata Sampann Turmeric Powder (Haldi)", "టాటా సంపన్ పసుపు పొడి", "Tata Sampann",
                "With minimum 3% natural curcumin guarantee, pure and golden yellow.",
                "కనీసం 3% సహజ కర్క్యుమిన్ కలిగిన స్వచ్ఛమైన పసుపు పొడి.",
                "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=500", false,
                List.of(
                    new VariantSeed("200 g", "SPICE-TURM-200G", 55.00, 48.00, 13, false, 35),
                    new VariantSeed("500 g", "SPICE-TURM-500G", 130.00, 115.00, 12, true, 20)
                )
            );

            // Product 9: Tata Salt
            seedProductWithVariants(
                sugarCat, "Tata Salt Vacuum Evaporated Iodised", "టాటా అయోడైజ్డ్ ఉప్పు", "Tata",
                "Desh Ka Namak, vacuum evaporated pure salt rich in Iodine.",
                "దేశ్ కా నమక్ - స్వచ్ఛమైన అయోడైజ్డ్ ఉప్పు.",
                "https://images.unsplash.com/photo-1622484212850-eb596d769edc?w=500", true,
                List.of(
                    new VariantSeed("1 kg Packet", "SALT-TATA-1KG", 28.00, 25.00, 11, true, 100)
                )
            );

            // Product 10: Sugar
            seedProductWithVariants(
                sugarCat, "Madhur Pure & Hygienic Sugar", "మధుర్ స్వచ్ఛమైన చక్కెర", "Madhur",
                "Sulphur-free sparkling white crystal sugar untouched by hands.",
                "సల్ఫర్ రహిత స్పటిక స్వచ్ఛమైన చక్కెర.",
                "https://images.unsplash.com/photo-1587735243615-c03f25aaff15?w=500", false,
                List.of(
                    new VariantSeed("1 kg", "SUGAR-MADHUR-1KG", 52.00, 46.00, 12, true, 60),
                    new VariantSeed("5 kg", "SUGAR-MADHUR-5KG", 255.00, 225.00, 12, false, 20)
                )
            );

            // Product 11: Parle-G
            seedProductWithVariants(
                snackCat, "Parle-G Original Gluco Biscuits", "పార్లే-జి గ్లూకో బిస్కెట్లు", "Parle",
                "Indias favorite tea-time glucose biscuits, energy packed.",
                "భారతదేశపు అభిమాన టీ-టైమ్ గ్లూకోజ్ బిస్కెట్లు.",
                "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500", true,
                List.of(
                    new VariantSeed("Pack of 5 (120g each)", "SNACK-PARLEG-P5", 50.00, 45.00, 10, true, 50)
                )
            );

            // Product 12: Tata Tea Gold
            seedProductWithVariants(
                teaCat, "Tata Tea Gold Leaf Tea", "టాటా టీ గోల్డ్ టీ పొడి", "Tata Tea",
                "Gentle rolled tea leaves with crushed leaves for the perfect aroma and strength.",
                "అద్భుతమైన రుచి మరియు ఘుమఘుమలాడే సువాసనతో కూడిన టీ ఆకులు.",
                "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500", true,
                List.of(
                    new VariantSeed("250 g Pouch", "TEA-TTGOLD-250G", 165.00, 145.00, 12, false, 30),
                    new VariantSeed("500 g Pouch", "TEA-TTGOLD-500G", 320.00, 285.00, 11, true, 25)
                )
            );

            // Product 13: Farm Fresh Eggs
            seedProductWithVariants(
                dairyCat, "Farm Fresh White Eggs", "తాజా కోడి గుడ్లు", "Village Poultry",
                "Freshly collected village poultry eggs rich in natural protein.",
                "గ్రామీణ పౌల్ట్రీ నుండి తాజా కోడి గుడ్లు.",
                "https://images.unsplash.com/photo-1516467508483-a7212febe31a?w=500", true,
                List.of(
                    new VariantSeed("Tray of 6 Eggs", "EGG-TRAY-6", 48.00, 42.00, 13, false, 40),
                    new VariantSeed("Crate of 30 Eggs", "EGG-CRATE-30", 220.00, 195.00, 11, true, 20)
                )
            );

            // Product 14: Surf Excel
            seedProductWithVariants(
                cleanCat, "Surf Excel Easy Wash Detergent Powder", "సర్ఫ్ ఎక్సెల్ ఈజీ వాష్ డిటర్జెంట్ పౌడర్", "Surf Excel",
                "Effortless stain removal in bucket wash, leaves clothes smelling fresh.",
                "సులభంగా మురికిని వదిలించే నాణ్యమైన సర్ఫ్ పౌడర్.",
                "https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=500", false,
                List.of(
                    new VariantSeed("1 kg Packet", "CLEAN-SURF-1KG", 150.00, 135.00, 10, true, 30),
                    new VariantSeed("3 kg Bag", "CLEAN-SURF-3KG", 440.00, 395.00, 10, false, 15)
                )
            );
        }
    }

    private void seedProductWithVariants(Category cat, String nameEn, String nameTe, String brand,
                                         String descEn, String descTe, String imgUrl, boolean isFeatured,
                                         List<VariantSeed> variants) {
        if (cat == null) return;

        Product p = new Product();
        p.setCategory(cat);
        p.setNameEn(nameEn);
        p.setNameTe(nameTe);
        p.setBrand(brand);
        p.setDescriptionEn(descEn);
        p.setDescriptionTe(descTe);
        p.setImageUrl(imgUrl);
        p.setIsFeatured(isFeatured);
        p.setIsActive(true);

        Product savedProduct = productRepository.save(p);

        for (VariantSeed vs : variants) {
            ProductVariant v = new ProductVariant();
            v.setProduct(savedProduct);
            v.setUnitName(vs.unitName);
            v.setSku(vs.sku);
            v.setMrp(BigDecimal.valueOf(vs.mrp));
            v.setSellingPrice(BigDecimal.valueOf(vs.sellingPrice));
            v.setDiscountPercent(vs.discountPercent);
            v.setIsDefault(vs.isDefault);
            v.setIsActive(true);

            ProductVariant savedVariant = variantRepository.save(v);

            Inventory inv = new Inventory();
            inv.setVariant(savedVariant);
            inv.setStockQuantity(vs.stock);
            inv.setLowStockThreshold(5);
            inv.setLastRestockedAt(LocalDateTime.now());
            inventoryRepository.save(inv);

            StockMovement sm = new StockMovement(
                savedVariant,
                StockMovementType.INITIAL_STOCK,
                vs.stock,
                vs.stock,
                "INIT_SEED",
                "Initial catalog stock setup"
            );
            stockMovementRepository.save(sm);
        }
    }

    private Category createCategory(String nameEn, String nameTe, String slug, String img, int order) {
        Category c = new Category();
        c.setNameEn(nameEn);
        c.setNameTe(nameTe);
        c.setSlug(slug);
        c.setImageUrl(img);
        c.setDisplayOrder(order);
        c.setIsActive(true);
        return c;
    }

    private void initOffers() {
        if (offerRepository.count() == 0) {
            Offer o1 = new Offer();
            o1.setTitleEn("Village Welcome Offer: Flat 5% Off");
            o1.setTitleTe("గ్రామ స్వాగత ఆఫర్: 5% తగ్గింపు");
            o1.setCouponCode("VILLAGE5");
            o1.setDiscountPercent(5);
            o1.setMinOrderAmount(BigDecimal.valueOf(200.00));
            o1.setIsActive(true);
            offerRepository.save(o1);

            Offer o2 = new Offer();
            o2.setTitleEn("Monthly Ration Saver: ₹50 Off");
            o2.setTitleTe("నెలాఖరు రేషన్ ఆఫర్: ₹50 తగ్గింపు");
            o2.setCouponCode("RATION50");
            o2.setDiscountAmount(BigDecimal.valueOf(50.00));
            o2.setMinOrderAmount(BigDecimal.valueOf(1000.00));
            o2.setIsActive(true);
            offerRepository.save(o2);
        }
    }

    private static class VariantSeed {
        String unitName;
        String sku;
        double mrp;
        double sellingPrice;
        int discountPercent;
        boolean isDefault;
        int stock;

        VariantSeed(String unitName, String sku, double mrp, double sellingPrice, int discountPercent, boolean isDefault, int stock) {
            this.unitName = unitName;
            this.sku = sku;
            this.mrp = mrp;
            this.sellingPrice = sellingPrice;
            this.discountPercent = discountPercent;
            this.isDefault = isDefault;
            this.stock = stock;
        }
    }
}
