/**
 * Anshu Kirana Store - Bilingual Localization Engine (English & తెలుగు)
 */
const I18N = {
  currentLang: localStorage.getItem('aks_lang') || 'en',

  translations: {
    en: {
      appName: "Anshu Kirana Store",
      tagline: "Fresh Groceries at Your Village Doorstep",
      searchPlaceholder: "Search rice, dal, oil, spices, daily essentials...",
      selectVillage: "Deliver to:",
      home: "Home",
      products: "Products",
      categories: "Categories",
      myCart: "My Cart",
      myOrders: "My Orders",
      login: "Login",
      register: "Register",
      logout: "Logout",
      adminDashboard: "Admin Panel",
      heroTitle: "Fresh Groceries Delivered To Your Village!",
      heroSubtitle: "Get top quality Rice, Dals, Oils & Essentials at genuine wholesale prices.",
      orderOnWhatsapp: "Order via WhatsApp",
      exploreProducts: "Shop Now",
      todaysOffers: "Today's Special Offers",
      shopByCategory: "Shop by Category",
      featuredItems: "Popular Village Essentials",
      whyShopWithUs: "Why Shop With Anshu Kirana?",
      why1Title: "Village Doorstep Delivery",
      why1Desc: "Fast delivery directly to your home or farm in Repudi and neighboring villages.",
      why2Title: "Pure & Hygienic Quality",
      why2Desc: "Cleaned grains, unadulterated oils and trusted daily brands.",
      why3Title: "Cash on Delivery (COD)",
      why3Desc: "Pay cash in hand only after receiving and checking your groceries.",
      why4Title: "Best Village Prices",
      why4Desc: "Direct town wholesale pricing with no inflated delivery commissions.",
      addToCart: "Add to Cart",
      outOfStock: "Out of Stock",
      inStock: "In Stock",
      lowStock: "Few packs left",
      freeDeliveryMsg: "Free delivery on orders above ₹299!",
      addMoreForFree: "Add ₹{amount} more for FREE village delivery",
      cartEmpty: "Your grocery cart is empty",
      cartEmptyDesc: "Browse our fresh catalog and add essentials to your basket.",
      startShopping: "Start Shopping",
      subtotal: "Subtotal",
      deliveryFee: "Delivery Charge",
      discount: "Discount",
      grandTotal: "Final Total",
      free: "FREE",
      checkout: "Proceed to Checkout",
      checkoutTitle: "Complete Your Village Delivery Order",
      personalDetails: "Customer Information",
      fullName: "Full Name",
      mobileNumber: "10-digit Mobile Number",
      deliveryAddress: "Village Delivery Address",
      village: "Select Village",
      street: "Street / Colony / House No",
      landmark: "Landmark (e.g. Near Temple, Water Tank, School)",
      instructions: "Delivery Instructions (Optional)",
      paymentMethod: "Payment Method",
      codTitle: "Cash on Delivery (COD)",
      codDesc: "Pay by cash when the store boy delivers items to your door.",
      upiTitle: "UPI / QR Code",
      upiDesc: "Pay with GooglePay, PhonePe, Paytm on delivery or scan QR.",
      placeOrder: "Confirm & Place COD Order",
      orderSuccess: "Order Placed Successfully!",
      orderNumber: "Order Number",
      thankYouMsg: "Thank you for shopping with Anshu Kirana Store. Your order is received and will be packed shortly.",
      trackOrder: "Track My Order",
      continueShopping: "Continue Shopping",
      orderStatus: "Order Status",
      placed: "Order Placed",
      confirmed: "Confirmed",
      packing: "Packing",
      outForDelivery: "Out for Delivery",
      delivered: "Delivered",
      cancelled: "Cancelled",
      cancelOrder: "Cancel Order",
      reorder: "Order Again",
      filters: "Filters",
      sortBy: "Sort By",
      priceLowHigh: "Price: Low to High",
      priceHighLow: "Price: High to Low",
      newest: "Newest First",
      allBrands: "All Brands",
      villageHelpline: "Village Helpline & Orders"
    },
    te: {
      appName: "అంశు కిరాణా స్టోర్",
      tagline: "తాజా సరుకులు మీ ఇంటి వద్దకే",
      searchPlaceholder: "బియ్యం, పప్పులు, నూనె, మసాలాలు వెతకండి...",
      selectVillage: "డెలివరీ ప్రాంతం:",
      home: "హోమ్",
      products: "సరుకులు",
      categories: "కేటగిరీలు",
      myCart: "నా బుట్ట (కార్ట్)",
      myOrders: "నా ఆర్డర్లు",
      login: "లాగిన్",
      register: "ఖాతా తెరవండి",
      logout: "లాగౌట్",
      adminDashboard: "షాప్ అడ్మిన్",
      heroTitle: "మీ గ్రామంలోకే తాజా కిరాణా సరుకులు!",
      heroSubtitle: "నాణ్యమైన బియ్యం, పప్పులు, నూనెలు హోల్‌సేల్ ధరకు పొందండి.",
      orderOnWhatsapp: "వాట్సాప్ ద్వారా ఆర్డర్ చేయండి",
      exploreProducts: "షాపింగ్ చేయండి",
      todaysOffers: "ఈరోజు ప్రత్యేక ఆఫర్లు",
      shopByCategory: "కేటగిరీల వారిగా సరుకులు",
      featuredItems: "గ్రామస్తులు ఎక్కువగా కొనే నిత్యావసరాలు",
      whyShopWithUs: "మా కిరాణా షాపులోనే ఎందుకు కొనాలి?",
      why1Title: "ఇంటి వద్దకే డెలివరీ",
      why1Desc: "రాంపూర్ మరియు చుట్టుపక్కల గ్రామాల్లో మీ ఇంటి ముంగిటకే త్వరగా డెలివరీ.",
      why2Title: "స్వచ్ఛమైన నాణ్యత",
      why2Desc: "శుభ్రమైన ధాన్యాలు, కల్తీ లేని నూనెలు మరియు నమ్మకమైన బ్రాండ్లు.",
      why3Title: "సరుకులు చూసి డబ్బులివ్వండి (COD)",
      why3Desc: "సరుకులు అందిన తర్వాత చూసుకుని మాత్రమే నగదు చెల్లించండి.",
      why4Title: "చౌకైన ధరలు",
      why4Desc: "మండల కేంద్రం ధరలకే మా వద్ద సరుకులు లభిస్తాయి.",
      addToCart: "బుట్టలో వేయి (Add)",
      outOfStock: "స్టాక్ లేదు",
      inStock: "స్టాక్ ఉంది",
      lowStock: "కొన్ని మాత్రమే మిగిలాయి",
      freeDeliveryMsg: "₹299 పైబడి ఆర్డర్లకు ఉచిత డెలివరీ!",
      addMoreForFree: "ఉచిత డెలివరీ కోసం మరో ₹{amount} సరుకులు చేర్చండి",
      cartEmpty: "మీ బుట్ట ఖాళీగా ఉంది",
      cartEmptyDesc: "తాజా సరుకులను చూసి బుట్టలో వేసుకోండి.",
      startShopping: "సరుకులు కొనండి",
      subtotal: "మొత్తం వెల",
      deliveryFee: "డెలివరీ చార్జ్",
      discount: "తగ్గింపు (డిస్కౌంట్)",
      grandTotal: "చెల్లించాల్సిన మొత్తం",
      free: "ఉచితం",
      checkout: "ఆర్డర్ బుక్ చేయండి",
      checkoutTitle: "డెలివరీ వివరాలు నమోదు చేయండి",
      personalDetails: "కస్టమర్ వివరాలు",
      fullName: "మీ పూర్తి పేరు",
      mobileNumber: "10 అంకెల ఫోన్ నెంబర్",
      deliveryAddress: "గ్రామం / ఇంటి చిరునామా",
      village: "గ్రామాన్ని ఎంచుకోండి",
      street: "వీధి / కాలనీ / ఇంటి నెంబరు",
      landmark: "గుర్తు (ఉదా: రామాలయం దగ్గర, వాటర్ ట్యాంక్, పాఠశాల)",
      instructions: "డెలివరీ సూచనలు (ఐచ్ఛికం)",
      paymentMethod: "చెల్లింపు విధానం",
      codTitle: "క్యాష్ ఆన్ డెలివరీ (Cash on Delivery)",
      codDesc: "సరుకులు అందిన తర్వాత డెలివరీ బాయ్‌కి నగదు ఇవ్వండి.",
      upiTitle: "యూపీఐ / క్యూఆర్ కోడ్ (PhonePe / GPay)",
      upiDesc: "డెలివరీ సమయంలో QR కోడ్ స్కాన్ చేసి చెల్లించవచ్చు.",
      placeOrder: "ఆర్డర్ ఖరారు చేయండి (COD)",
      orderSuccess: "మీ ఆర్డర్ విజయవంతంగా నమోదైంది!",
      orderNumber: "ఆర్డర్ నెంబరు",
      thankYouMsg: "అంశు కిరాణా స్టోర్‌లో ఆర్డర్ చేసినందుకు ధన్యవాదాలు. వెంటనే ప్యాక్ చేసి పంపిస్తాము.",
      trackOrder: "ఆర్డర్ స్థితి చూడండి",
      continueShopping: "మరిన్ని సరుకులు కొనండి",
      orderStatus: "ఆర్డర్ స్థితి",
      placed: "ఆర్డర్ అందింది",
      confirmed: "ఖరారైంది",
      packing: "ప్యాకింగ్ అవుతోంది",
      outForDelivery: "డెలివరీకి బయలుదేరింది",
      delivered: "సరుకులు అందాయి",
      cancelled: "రద్దయింది",
      cancelOrder: "ఆర్డర్ రద్దు చేయి",
      reorder: "మళ్ళీ ఆర్డర్ చేయి",
      filters: "ఫిల్టర్లు",
      sortBy: "క్రమబద్ధీకరించు",
      priceLowHigh: "ధర: తక్కువ నుండి ఎక్కువ",
      priceHighLow: "ధర: ఎక్కువ నుండి తక్కువ",
      newest: "కొత్తవి మొదట",
      allBrands: "అన్ని కంపెనీలు (బ్రాండ్లు)",
      villageHelpline: "గ్రామ ఆర్డర్ల హెల్ప్‌లైన్"
    }
  },

  get(key, replacements = {}) {
    const lang = this.currentLang;
    let text = (this.translations[lang] && this.translations[lang][key]) ||
               (this.translations.en && this.translations.en[key]) || key;

    Object.entries(replacements).forEach(([k, v]) => {
      text = text.replace(new RegExp(`\\{${k}\\}`, 'g'), v);
    });

    return text;
  },

  setLanguage(lang) {
    if (lang !== 'en' && lang !== 'te') lang = 'en';
    this.currentLang = lang;
    localStorage.setItem('aks_lang', lang);
    this.applyTranslations();

    // Trigger custom event so page scripts can re-render bilingual product names
    window.dispatchEvent(new CustomEvent('languageChanged', { detail: { lang } }));
  },

  applyTranslations() {
    document.querySelectorAll('[data-i18n]').forEach(el => {
      const key = el.getAttribute('data-i18n');
      if (key) {
        el.textContent = this.get(key);
      }
    });

    document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
      const key = el.getAttribute('data-i18n-placeholder');
      if (key) {
        el.setAttribute('placeholder', this.get(key));
      }
    });

    // Update active state of language toggle buttons
    document.querySelectorAll('.lang-toggle-btn').forEach(btn => {
      if (btn.getAttribute('data-lang') === this.currentLang) {
        btn.classList.add('active');
      } else {
        btn.classList.remove('active');
      }
    });
  }
};

// Auto initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  I18N.applyTranslations();
});
