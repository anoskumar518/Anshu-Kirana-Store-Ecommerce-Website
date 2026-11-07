-- ====================================================================
-- Anshu Kirana Store - Database Migration V2: Seed Data
-- ====================================================================

-- 1. Insert Initial Roles
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_ADMIN', 'Store Administrator with full access to inventory, orders, and reports'),
(2, 'ROLE_CUSTOMER', 'Village customer with shopping, cart, and order tracking access');

-- 2. Insert Default Categories (Bilingual English & Telugu)
INSERT INTO categories (id, name_en, name_te, slug, description, image_url, display_order, is_active) VALUES
(1, 'Rice & Grains', 'బియ్యం మరియు ధాన్యాలు', 'rice-grains', 'Premium Sona Masoori, Basmati, Wheat Flour (Atta) and Millets', 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80', 1, TRUE),
(2, 'Dals & Pulses', 'పప్పులు', 'dals-pulses', 'High-protein Toor Dal, Moong Dal, Chana Dal, Urad Dal', 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500&auto=format&fit=crop&q=80', 2, TRUE),
(3, 'Cooking Oil & Ghee', 'నూనె మరియు నెయ్యి', 'oil-ghee', 'Sunflower Oil, Groundnut Oil, Mustard Oil, Pure Cow Desi Ghee', 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=80', 3, TRUE),
(4, 'Spices & Masalas', 'మసాలాలు', 'spices-masalas', 'Turmeric, Red Chilli, Coriander, Garam Masala, Whole Spices', 'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500&auto=format&fit=crop&q=80', 4, TRUE),
(5, 'Sugar & Salt', 'చక్కెర మరియు ఉప్పు', 'sugar-salt', 'Refined Sugar, Jaggery (Bellam), Iodized Tata Salt, Crystal Salt', 'https://images.unsplash.com/photo-1622484212850-eb596d769edc?w=500&auto=format&fit=crop&q=80', 5, TRUE),
(6, 'Biscuits & Snacks', 'బిస్కెట్లు మరియు స్నాక్స్', 'biscuits-snacks', 'Parle-G, Good Day, Rusk, Namkeen, Murukulu, Mixtures', 'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&auto=format&fit=crop&q=80', 6, TRUE),
(7, 'Tea & Coffee', 'టీ మరియు కాఫీ', 'tea-coffee', 'Tata Tea Gold, Red Label, Taj Mahal, Bru Instant, Sunrise', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&auto=format&fit=crop&q=80', 7, TRUE),
(8, 'Dairy & Daily Essentials', 'పాల ఉత్పత్తులు మరియు నిత్యావసరాలు', 'dairy-essentials', 'Fresh Milk, Curd, Butter, Paneer, Farm Fresh Eggs', 'https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&auto=format&fit=crop&q=80', 8, TRUE),
(9, 'Personal Care', 'వ్యక్తిగత సంరక్షణ', 'personal-care', 'Soaps, Shampoos, Toothpaste, Hair Oils, Talcum Powder', 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500&auto=format&fit=crop&q=80', 9, TRUE),
(10, 'Household Cleaning', 'ఇంటి శుభ్రత', 'household-cleaning', 'Detergent Powders, Dishwash Bars, Floor Cleaners, Mosquito Coils', 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=500&auto=format&fit=crop&q=80', 10, TRUE);

-- 3. Insert Sample Popular Grocery Products
INSERT INTO products (id, category_id, name_en, name_te, brand, description_en, description_te, image_url, is_featured, is_active) VALUES
(1, 1, 'Kurnool Sona Masoori Rice (Old Crop)', 'కర్నూల్ సోనా మసూరి బియ్యం (పాత బియ్యం)', 'Anshu Select', 'Premium aged 1-year old Kurnool Sona Masoori raw rice, lightweight and aromatic.', 'పాత కర్నూల్ సోనా మసూరి బియ్యం, అన్నం పొడిపొడిగా మరియు రుచికరంగా వస్తుంది.', 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(2, 1, 'Aashirvaad Shudh Chakki Atta', 'ఆశీర్వాద్ షుద్ధ్ చక్కీ గోధుమ పిండి', 'Aashirvaad', '100% whole wheat flour made with heavy grains, ensures soft rotis.', '100% స్వచ్ఛమైన గోధుమ పిండి. మృదువైన రోటీలకు ప్రసిద్ధి.', 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(3, 2, 'Unpolished Desi Toor Dal', 'నాటు కందిపప్పు (పాలిష్ లేనిది)', 'Farm Fresh', 'High-protein, naturally sun-dried unpolished Toor Dal with rich yellow color.', 'సహజసిద్ధమైన పాలిష్ లేని నాటు కందిపప్పు, సాంబారు మరియు పప్పుకు శ్రేష్ఠమైనది.', 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(4, 2, 'Yellow Moong Dal (Split)', 'పసుపు పెసరపప్పు', 'Anshu Daily', 'Easily digestible, high-quality split yellow moong dal for khichdi and dal.', 'సులభంగా జీర్ణమయ్యే శుభ్రమైన పెసరపప్పు.', 'https://images.unsplash.com/photo-1585994192701-f1a505c8574a?w=500&auto=format&fit=crop&q=80', FALSE, TRUE),
(5, 3, 'Freedom Refined Sunflower Oil', 'ఫ్రీడమ్ రిఫైన్డ్ సన్‌ఫ్లవర్ ఆయిల్', 'Freedom', 'Rich in Vitamin E and Omega 6, light and non-sticky cooking oil.', 'విటమిన్ E మరియు ఒమేగా 6 తో తేలికైన వంట నూనె.', 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(6, 3, 'Heritage Pure Cow Ghee', 'హెరిటేజ్ స్వచ్ఛమైన ఆవు నెయ్యి', 'Heritage', 'Traditional granular golden cow ghee with rich aroma and traditional taste.', 'సాంప్రదాయ సువాసనతో కూడిన స్వచ్ఛమైన ఆవు నెయ్యి.', 'https://images.unsplash.com/photo-1631451095765-2c91616fc9e6?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(7, 4, 'Everest Tikhalal Red Chilli Powder', 'ఎవరెస్ట్ తీఖాలాల్ కారం పొడి', 'Everest', 'Fiery hot and vibrant red colour chilli powder ground from selected chillies.', 'ఎంచుకున్న ఎండుమిర్చి నుండి తయారైన ఘాటైన ఎర్రటి కారం పొడి.', 'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500&auto=format&fit=crop&q=80', FALSE, TRUE),
(8, 4, 'Tata Sampann Turmeric Powder (Haldi)', 'టాటా సంపన్ పసుపు పొడి', 'Tata Sampann', 'With minimum 3% natural curcumin guarantee, pure and golden yellow.', 'కనీసం 3% సహజ కర్క్యుమిన్ కలిగిన స్వచ్ఛమైన పసుపు పొడి.', 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=500&auto=format&fit=crop&q=80', FALSE, TRUE),
(9, 5, 'Tata Salt Vacuum Evaporated Iodised', 'టాటా అయోడైజ్డ్ ఉప్పు', 'Tata', 'Desh Ka Namak, vacuum evaporated pure salt rich in Iodine.', 'దేశ్ కా నమక్ - స్వచ్ఛమైన అయోడైజ్డ్ ఉప్పు.', 'https://images.unsplash.com/photo-1622484212850-eb596d769edc?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(10, 5, 'Madhur Pure & Hygienic Sugar', 'మధుర్ స్వచ్ఛమైన చక్కెర', 'Madhur', 'Sulphur-free sparkling white crystal sugar untouched by hands.', 'సల్ఫర్ రహిత స్పటిక స్వచ్ఛమైన చక్కెర.', 'https://images.unsplash.com/photo-1587735243615-c03f25aaff15?w=500&auto=format&fit=crop&q=80', FALSE, TRUE),
(11, 6, 'Parle-G Original Gluco Biscuits', 'పార్లే-జి గ్లూకో బిస్కెట్లు', 'Parle', 'Indias favorite tea-time glucose biscuits, energy packed.', 'భారతదేశపు అభిమాన టీ-టైమ్ గ్లూకోజ్ బిస్కెట్లు.', 'https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(12, 7, 'Tata Tea Gold Leaf Tea', 'టాటా టీ గోల్డ్ టీ పొడి', 'Tata Tea', 'Gentle rolled tea leaves with crushed leaves for the perfect aroma and strength.', 'అద్భుతమైన రుచి మరియు ఘుమఘుమలాడే సువాసనతో కూడిన టీ ఆకులు.', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(13, 8, 'Farm Fresh White Eggs', 'తాజా కోడి గుడ్లు', 'Village Poultry', 'Freshly collected village poultry eggs rich in natural protein.', 'గ్రామీణ పౌల్ట్రీ నుండి తాజా కోడి గుడ్లు.', 'https://images.unsplash.com/photo-1516467508483-a7212febe31a?w=500&auto=format&fit=crop&q=80', TRUE, TRUE),
(14, 10, 'Surf Excel Easy Wash Detergent Powder', 'సర్ఫ్ ఎక్సెల్ ఈజీ వాష్ డిటర్జెంట్ పౌడర్', 'Surf Excel', 'Effortless stain removal in the bucket wash, leaves clothes smelling fresh.', 'సులభంగా మురికిని వదిలించే నాణ్యమైన సర్ఫ్ పౌడర్.', 'https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=500&auto=format&fit=crop&q=80', FALSE, TRUE);

-- 4. Insert Product Variants with Village Pack Sizes & Pricing
INSERT INTO product_variants (id, product_id, unit_name, sku, mrp, selling_price, discount_percent, is_default, is_active) VALUES
-- Sona Masoori Rice variants
(1, 1, '5 kg Bag', 'RICE-SM-5KG', 350.00, 315.00, 10, FALSE, TRUE),
(2, 1, '10 kg Bag', 'RICE-SM-10KG', 680.00, 599.00, 12, FALSE, TRUE),
(3, 1, '25 kg Bag', 'RICE-SM-25KG', 1650.00, 1450.00, 12, TRUE, TRUE),

-- Aashirvaad Atta variants
(4, 2, '1 kg Pouch', 'ATTA-AASH-1KG', 65.00, 58.00, 11, FALSE, TRUE),
(5, 2, '5 kg Bag', 'ATTA-AASH-5KG', 310.00, 275.00, 11, TRUE, TRUE),
(6, 2, '10 kg Bag', 'ATTA-AASH-10KG', 590.00, 520.00, 12, FALSE, TRUE),

-- Toor Dal variants
(7, 3, '500 g', 'DAL-TOOR-500G', 95.00, 85.00, 11, FALSE, TRUE),
(8, 3, '1 kg', 'DAL-TOOR-1KG', 185.00, 165.00, 11, TRUE, TRUE),
(9, 3, '2 kg', 'DAL-TOOR-2KG', 370.00, 320.00, 14, FALSE, TRUE),

-- Yellow Moong Dal
(10, 4, '500 g', 'DAL-MOONG-500G', 75.00, 68.00, 9, FALSE, TRUE),
(11, 4, '1 kg', 'DAL-MOONG-1KG', 145.00, 130.00, 10, TRUE, TRUE),

-- Sunflower Oil variants
(12, 5, '1 Litre Pouch', 'OIL-SUN-1L', 145.00, 128.00, 12, TRUE, TRUE),
(13, 5, '5 Litre Can', 'OIL-SUN-5L', 720.00, 625.00, 13, FALSE, TRUE),

-- Cow Ghee variants
(14, 6, '200 ml Jar', 'GHEE-COW-200ML', 165.00, 150.00, 9, FALSE, TRUE),
(15, 6, '500 ml Jar', 'GHEE-COW-500ML', 390.00, 360.00, 8, TRUE, TRUE),
(16, 6, '1 Litre Tin', 'GHEE-COW-1L', 760.00, 699.00, 8, FALSE, TRUE),

-- Red Chilli Powder
(17, 7, '250 g', 'SPICE-CHILLI-250G', 90.00, 80.00, 11, FALSE, TRUE),
(18, 7, '500 g', 'SPICE-CHILLI-500G', 175.00, 155.00, 11, TRUE, TRUE),
(19, 7, '1 kg', 'SPICE-CHILLI-1KG', 340.00, 299.00, 12, FALSE, TRUE),

-- Turmeric Powder
(20, 8, '200 g', 'SPICE-TURM-200G', 55.00, 48.00, 13, FALSE, TRUE),
(21, 8, '500 g', 'SPICE-TURM-500G', 130.00, 115.00, 12, TRUE, TRUE),

-- Tata Salt
(22, 9, '1 kg Packet', 'SALT-TATA-1KG', 28.00, 25.00, 11, TRUE, TRUE),

-- Sugar
(23, 10, '1 kg', 'SUGAR-MADHUR-1KG', 52.00, 46.00, 12, TRUE, TRUE),
(24, 10, '5 kg', 'SUGAR-MADHUR-5KG', 255.00, 225.00, 12, FALSE, TRUE),

-- Parle-G
(25, 11, 'Pack of 5 (120g each)', 'SNACK-PARLEG-P5', 50.00, 45.00, 10, TRUE, TRUE),

-- Tata Tea Gold
(26, 12, '250 g Pouch', 'TEA-TTGOLD-250G', 165.00, 145.00, 12, FALSE, TRUE),
(27, 12, '500 g Pouch', 'TEA-TTGOLD-500G', 320.00, 285.00, 11, TRUE, TRUE),

-- Eggs
(28, 13, 'Tray of 6 Eggs', 'EGG-TRAY-6', 48.00, 42.00, 13, FALSE, TRUE),
(29, 13, 'Crate of 30 Eggs', 'EGG-CRATE-30', 220.00, 195.00, 11, TRUE, TRUE),

-- Surf Excel
(30, 14, '1 kg Packet', 'CLEAN-SURF-1KG', 150.00, 135.00, 10, TRUE, TRUE),
(31, 14, '3 kg Bag', 'CLEAN-SURF-3KG', 440.00, 395.00, 10, FALSE, TRUE);

-- 5. Insert Initial Inventory Stock & Balances
INSERT INTO inventory (id, variant_id, stock_quantity, low_stock_threshold, last_restocked_at) VALUES
(1, 1, 25, 5, CURRENT_TIMESTAMP),
(2, 2, 20, 5, CURRENT_TIMESTAMP),
(3, 3, 35, 5, CURRENT_TIMESTAMP),
(4, 4, 40, 8, CURRENT_TIMESTAMP),
(5, 5, 30, 5, CURRENT_TIMESTAMP),
(6, 6, 15, 3, CURRENT_TIMESTAMP),
(7, 7, 30, 6, CURRENT_TIMESTAMP),
(8, 8, 45, 8, CURRENT_TIMESTAMP),
(9, 9, 20, 4, CURRENT_TIMESTAMP),
(10, 10, 25, 5, CURRENT_TIMESTAMP),
(11, 11, 30, 5, CURRENT_TIMESTAMP),
(12, 12, 50, 10, CURRENT_TIMESTAMP),
(13, 13, 20, 4, CURRENT_TIMESTAMP),
(14, 14, 25, 5, CURRENT_TIMESTAMP),
(15, 15, 30, 5, CURRENT_TIMESTAMP),
(16, 16, 15, 3, CURRENT_TIMESTAMP),
(17, 17, 30, 5, CURRENT_TIMESTAMP),
(18, 18, 25, 5, CURRENT_TIMESTAMP),
(19, 19, 15, 3, CURRENT_TIMESTAMP),
(20, 20, 35, 5, CURRENT_TIMESTAMP),
(21, 21, 20, 4, CURRENT_TIMESTAMP),
(22, 22, 100, 15, CURRENT_TIMESTAMP),
(23, 23, 60, 10, CURRENT_TIMESTAMP),
(24, 24, 20, 4, CURRENT_TIMESTAMP),
(25, 25, 50, 10, CURRENT_TIMESTAMP),
(26, 26, 30, 5, CURRENT_TIMESTAMP),
(27, 27, 25, 5, CURRENT_TIMESTAMP),
(28, 28, 40, 8, CURRENT_TIMESTAMP),
(29, 29, 20, 4, CURRENT_TIMESTAMP),
(30, 30, 30, 6, CURRENT_TIMESTAMP),
(31, 31, 15, 3, CURRENT_TIMESTAMP);

-- 6. Insert Initial Promotional Offers
INSERT INTO offers (id, title_en, title_te, coupon_code, discount_percent, min_order_amount, is_active) VALUES
(1, 'Village Welcome Offer: Flat 5% Off', 'గ్రామ స్వాగత ఆఫర్: 5% తగ్గింపు', 'VILLAGE5', 5, 200.00, TRUE),
(2, 'Monthly Ration Saver: ₹50 Off on orders above ₹1000', 'నెలాఖరు రేషన్ ఆఫర్: ₹1000 పైబడి ఆర్డర్లకు ₹50 తగ్గింపు', 'RATION50', 0, 1000.00, TRUE);
