# Brewkery Android App

> **Technical Assessment**: Clickretina Android Developer  
> **Candidate / Developer**: Sachin Narigara  
> **Official Prototype Reference**: [https://vivekshah138.github.io/Brewkery/](https://vivekshah138.github.io/Brewkery/)  
> **API Source Repository**: [VivekShah138/Brewkery](https://github.com/VivekShah138/Brewkery)  

---

## ☕ App Overview

**Brewkery** is an artisanal coffee & fresh oven bakery mobile ordering application developed natively for Android using modern **Jetpack Compose**, **Material 3**, **Kotlin Coroutines & Flow**, **Retrofit**, **Kotlinx Serialization**, and **Coil**.

The application reflects the warm, cream-and-terracotta visual identity of an artisanal coffee house, featuring full menu browsing, dynamic item customization with real-time pricing calculation, single-source-of-truth cart management, and order dispatch tracking.

---

## ✨ Features

1. **Artisanal Menu (Screen 1)**:
   - Header with custom circular `BK` monogram badge, subtitle, and dynamic cart badge.
   - Dynamic Store Delivery Card: Displays estimated delivery (`20 - 30 mins`) and `$2.50 flat fee`, automatically switching to live `ACTIVE ORDER` tracking once an order is placed.
   - Live Search: Instant filtering across titles, descriptions, and ingredients.
   - Horizontal Category Chips: Filter by *All Items*, *☕ Hot Coffee*, *🧊 Cold Brews*, and *🥐 Artisan Bakery*.
   - Product Cards: 62x62dp image, badge pills (`BESTSELLER`, `STAFF PICK`, `FRESHLY BAKED`, `SEASONAL`, etc.), ratings with review counts, price in terracotta orange, and `+ Customize` action.
   - Floating Bottom Cart Bar: Slide-in/out reactive bar displaying item count, subtotal, and checkout arrow link.

2. **Item Customizer (Screen 2)**:
   - Top toolbar with circular back button, uppercase letter-spaced title `ITEM CUSTOMIZER`, and toggleable heart favorite button.
   - Large 230dp rounded hero image with badge overlays.
   - Title, orange price, full description, and scrollable key ingredient chips.
   - **Size Selection**: `Tall (8 oz)`, `Grande (12 oz)`, `Venti (16 oz)` (or bakery pack options) with highlighted orange border and `#FFF2EE` background.
   - **Milk Options / Spreads**: Radio selection for `Oat Milk (Barista Blend)`, `Roasted Almond Milk`, `Organic Whole Milk`, and artisan bakery spreads with additional price tags.
   - **Sweetness & Serving**: `0% Unsweetened`, `50% Mild`, `100% Standard Sweet` (or serving temperature).
   - Quantity Stepper `[- qty +]` with minimum constraint.
   - Sticky Bottom Button: Reactive `Add to Cart • $X.XX` with dynamic unit price and quantity formula.
   - In-app feedback: SnackBar with direct `View Cart` action.

3. **Order Cart & Checkout (Screen 3)**:
   - Top bar with back navigation, centered title `YOUR CART`, and `Clear Cart` action with confirmation dialog.
   - Cart Items: Thumbnail, title, customizations breakdown (`Grande (12 oz) • Oat Milk`, `50% Mild`), item total price, and quantity adjuster.
   - Empty Cart View: Stylized bag illustration, friendly messaging, and `"Explore Menu"` button.
   - Bottom Checkout Section:
     - Real-time **Subtotal**
     - **Delivery Fee** ($2.50 flat fee, $0.00 when empty)
     - **Estimated Tax** (8% as documented in prototype)
     - **Total Payable** in bold orange
     - Primary `Place Order Now • $X.XX` button with lock icon.

4. **Order Confirmation & Tracking (Screen 4)**:
   - Circular coffee graphic `☕` with orange 3dp accent border.
   - `ORDER DISPATCHED` pill and *"Brewing in Progress!"* heading.
   - **Order Ticket Card**:
     - Order reference code (e.g. `#BK-74921`)
     - Green `PREPARING` badge with pulsating status indicator dot
     - Estimated wait time (`20–30 minutes`)
     - Items ordered count and ordered item summary
     - Status note: `Status: Barista accepted your order!`
   - `"Back to Menu"` button in dark brown `#140B07` returning to storefront with live order status active.

5. **Premium Splash Screen & Artisanal App Icon**:
   - Native **Android SplashScreen API** (`androidx.core:core-splashscreen`) configured in `themes.xml` and `AndroidManifest.xml` with zero cold-start delay.
   - Jetpack Compose entrance splash with animated circular BK monogram badge, coffee steam waves, and smooth crossfade into storefront.
   - Bespoke **Adaptive Launcher Icon** featuring a deep espresso backdrop, terracotta accent ring, golden amber steam waves, and bold BK monogram across both adaptive vector XML and high-res mipmaps.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Language** | Kotlin 2.1.0 |
| **UI Framework** | Android Jetpack Compose, Material 3, Lifecycle ViewModel Compose |
| **Navigation** | Navigation Compose (single NavHost, typed arguments) |
| **Networking** | Retrofit 2.11.0, OkHttp 4.12.0, Kotlinx Serialization Json 1.7.3 |
| **Image Loading** | Coil Compose 2.7.0 (with OkHttp caching, memory cache & crossfade) |
| **Architecture** | MVVM (Model-View-ViewModel) + Single-Source-of-Truth Repository |
| **Reactive Streams** | Kotlin Coroutines, StateFlow, collectAsStateWithLifecycle |
| **Testing** | JUnit 4, Kotlinx Coroutines Test, MockWebServer |
| **Build System** | Gradle 9.3.1 (Kotlin DSL), Android Gradle Plugin 8.9.1, Compile/Target SDK 35, Min SDK 24 |

---

## 📐 Architecture

```
com.clickretina.brewkery/
├── BrewkeryApplication.kt             # Application class, image loader, single repository owner
├── MainActivity.kt                    # Edge-to-edge Activity entry point
├── data/
│   ├── remote/
│   │   ├── BrewkeryApiService.kt      # Retrofit interface for data.json & api/items/{id}.json
│   │   ├── RetrofitClient.kt          # Client setup with Kotlinx Serialization
│   │   └── dto/
│   │       └── ProductDtos.kt         # API DTO schemas matching raw JSON
│   ├── mapper/
│   │   └── ProductMapper.kt           # DTO to Domain model mappers
│   └── repository/
│       ├── BrewkeryRepository.kt      # Domain contract for catalog, cart & active orders
│       └── BrewkeryRepositoryImpl.kt  # Thread-safe in-memory cache & network repository
├── domain/
│   ├── model/
│   │   ├── StoreModels.kt             # StoreMeta, Category, ProductItem, ActiveOrder
│   │   ├── CartItem.kt                # CartItem with dynamic unitPrice and totalPrice
│   │   └── Resource.kt                # Loading, Success, Error sealed class
│   └── util/
│       └── PriceCalculator.kt         # Pure calculator for Subtotal, Delivery Fee, Tax, Total
└── ui/
    ├── BrewkeryApp.kt                 # Compose theme wrapper & NavController setup
    ├── navigation/
    │   ├── Screen.kt                  # Sealed routes: Menu, Customizer, Cart, OrderConfirmation
    │   └── NavGraph.kt                # Single NavHost wiring all 6 navigation paths
    ├── screens/
    │   ├── home/                      # Menu Screen, HomeViewModel, HomeUiState
    │   ├── detail/                    # Customizer Screen, DetailViewModel, DetailUiState
    │   ├── cart/                      # Cart Screen, CartViewModel, CartUiState
    │   └── order/                     # Order Status Screen, OrderStatusViewModel, OrderStatusUiState
    ├── components/                    # Reusable components (Header, Search, DeliveryCard, etc.)
    └── theme/                         # Color, Theme, Type
```

---

## 🌐 API Integration

The app connects to the official endpoints hosted on GitHub Raw:
- **Base URL**: `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/`
- **Store & Menu Endpoint**: `GET data.json`
- **Single Item Detail Endpoint**: `GET api/items/{id}.json` (IDs `1` through `6`)

Network responses are mapped to domain models. If the device is offline or the network drops, the repository automatically falls back to cached items so the app remains fully usable.

---

## 🚀 Build Instructions

### Prerequisites
- JDK 21 (bundled in Android Studio JBR or standalone JDK 21)
- Android SDK Platform 35 (`compileSdk = 35`, `minSdk = 24`)

### Build Debug APK via Command Line
```powershell
# Set JAVA_HOME to JDK 21 / Android Studio JBR
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# Build debug APK
.\gradlew.bat assembleDebug --no-daemon

# Run unit & integration test suites
.\gradlew.bat testDebugUnitTest --no-daemon
```

---

## 📱 APK Installation Instructions

The generated debug APK is located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Install via ADB:
```powershell
adb install -r "app/build/outputs/apk/debug/app-debug.apk"
```
Or transfer `app-debug.apk` directly to an Android phone/emulator and install with *"Install unknown apps"* permission enabled.

---

## 🤖 AI Development Section

This project was built collaboratively with AI coding assistants adhering to professional software engineering standards.

### 1. Antigravity Prompts Used
- *"Inspect the current project directory. Create a complete Android Studio compatible project with Kotlin DSL build.gradle.kts, package com.clickretina.brewkery, minSdk 24, compileSdk 35, Jetpack Compose, Material 3, and MVVM."*
- *"Open and inspect the official Brewkery assignment prototype at https://vivekshah138.github.io/Brewkery/. Document JSON schema, endpoints, image URLs, options, and error handling in API_IMPLEMENTATION.md without guessing fields."*
- *"Implement the Brewkery REST API integration using Kotlin, Retrofit, Kotlinx Serialization, and Coroutines using single source of truth."*
- *"Build the Brewkery Menu Screen using Jetpack Compose with warm cream background, delivery info card, search bar, category chips, LazyColumn products, and reactive floating bottom cart bar."*
- *"Implement the Brewkery Item Customizer Screen in Jetpack Compose with customizer top bar, hero image with badge, size selection cards, radio milk options, sweetness options, quantity stepper, and dynamic Add to Cart price."*
- *"Implement the Brewkery Cart Screen with PriceCalculator, reactive breakdown (Subtotal, $2.50 Delivery Fee, 8% Tax, Total Payable), clear cart confirmation, and place order action."*
- *"Implement the Brewkery Order Confirmation Screen with circular coffee graphic, ORDER DISPATCHED label, PREPARING ticket card with pulsating dot, estimated wait, and Back to Menu action."*
- *"Integrate the entire Brewkery Android application using Navigation Compose with menu, customizer/{productId}, cart, and orderConfirmation destinations and verify all paths."*

### 2. How AI Helped with Implementation
- **Accelerated Boilerplate**: Quickly configured Gradle version catalogs (`libs.versions.toml`), Kotlin 2.1 Compose compiler plugins, and Material 3 theme setups.
- **Reverse Engineering**: Accurately extracted the real JSON schema from the GitHub repository (`data.json` and `api/items/{id}.json`) without guessing fields or fabricating endpoints.
- **Single Source of Truth**: Architected a central `BrewkeryRepository` with `StateFlow` so the menu cart badge, customizer additions, and cart checkout stay in sync without redundant network calls.
- **Unit Testing**: Generated 31 comprehensive unit tests covering API deserialization, domain mappers, repository state transitions, `PriceCalculator` arithmetic, and navigation paths.

### 3. Actual Bugs Caught & Fixed (Honest Documentation)

#### Bug 1: Kotlin Object Initialization Circularity (`NullPointerException`)
- **Symptoms**: `BrewkeryNavigationTest.verify backward compatibility route aliases` threw a `java.lang.NullPointerException` when calling `Screen.Home.route`.
- **Root Cause**: In `Screen.kt`, `Screen` declared sealed subclasses (`data object Menu : Screen("menu")`) while its `companion object` declared `val Home = Menu`. In Kotlin bytecode, the companion object was initialized during static class loading of `Screen` before `Menu` had completed instantiation, causing `Menu` to be evaluated as `null`.
- **Solution**: Replaced eager values with lazy getter properties:
  ```kotlin
  companion object {
      val Home get() = Menu
      val Detail get() = Customizer
      val OrderStatus get() = OrderConfirmation
  }
  ```

#### Bug 2: Double Status Bar Inset Margin
- **Symptoms**: On devices with `enableEdgeToEdge()`, the top section (header, customizer toolbar, cart title) had an unnaturally large top gap.
- **Root Cause**: `Scaffold` already calculated the status bar inset in `innerPadding.calculateTopPadding()`. The inner `Column` applied `.padding(innerPadding)` AND `.statusBarsPadding()`, effectively doubling the top margin.
- **Solution**: Removed redundant `.statusBarsPadding()` on the inner Column and let `Scaffold`'s `innerPadding` handle edge-to-edge window insets cleanly.

#### Bug 3: Delivery Fee on Empty Cart Assertion Discrepancy
- **Symptoms**: `CartViewModelTest.updateQuantity with zero removes item` failed with `AssertionError: expected:<0.0> but was:<2.5>`.
- **Root Cause**: `CartUiState` held a static `val storeDeliveryFee: Double = 2.50` property representing the store flat fee rate, whereas `PriceCalculator` correctly calculated `$0.00` when the cart was empty.
- **Solution**: Delegated `CartUiState.deliveryFee` dynamically to `bill.deliveryFee`, ensuring the UI reactively shows `$0.00` delivery fee when empty, and `$2.50` when items are present.

---

## 💡 What I Learned

1. **State Ownership in Navigation Compose**: Passing a single-source-of-truth repository and scoping `CartViewModel` to the navigation graph lifecycle ensures cart items, quantities, and badges stay synchronized across all screens without UI flicker or state desynchronization.
2. **ViewModel Keys for Dynamic Screens**: Supplying `key = "customizer_$productId"` to `viewModel()` preserves customizer state per product without requiring manual parcelable bundle passing.
3. **Currency & Tax Precision**: Floating-point arithmetic (`Double`) can produce binary rounding discrepancies (e.g., `12.45 * 0.08 = 0.996`). Using a dedicated `PriceCalculator` with `BigDecimal` half-up rounding guarantees exact 2-decimal financial calculations matching the assignment specification.

---

## 👨‍💻 Developer Information

- **Developer**: Sachin Narigara
- **Application**: Brewkery Android App
- **Assignment**: Clickretina Android Developer Technical Assessment
- **Package Name**: `com.clickretina.brewkery`

