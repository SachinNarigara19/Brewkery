# Brewkery API & UI Specification

> **Official Prototype Reference**: [https://vivekshah138.github.io/Brewkery/](https://vivekshah138.github.io/Brewkery/)  
> **Source Repository**: [VivekShah138/Brewkery](https://github.com/VivekShah138/Brewkery)  
> **Target Application**: Clickretina Android Developer Technical Assessment ("Brewkery")

---

## 1. API Architecture & Endpoints

The Brewkery application is backed by static, CORS-enabled REST endpoints hosted directly on GitHub Raw content.

### Base URL
```
https://raw.githubusercontent.com/VivekShah138/Brewkery/main/
```

### Endpoints
| Purpose | HTTP Method | Path / Full URL | Response Status |
| :--- | :---: | :--- | :---: |
| **Full Menu & Store Metadata** | `GET` | `data.json`<br>`https://raw.githubusercontent.com/VivekShah138/Brewkery/main/data.json` | `200 OK` |
| **Single Item Detail** | `GET` | `api/items/{id}.json`<br>`https://raw.githubusercontent.com/VivekShah138/Brewkery/main/api/items/{id}.json`<br>*(Valid IDs: `1` to `6`)* | `200 OK` |

---

## 2. JSON Schema Definitions

### 2.1. `data.json` (Full Store & Catalog)

```json
{
  "meta": {
    "app": "Brewkery",
    "version": "1.0.0",
    "tagline": "Artisanal Coffee & Fresh Oven Bakes",
    "currency": "USD",
    "currency_symbol": "$",
    "delivery_fee": 2.50,
    "tax_rate_percent": 8.0,
    "estimated_delivery_time": "20 - 30 mins"
  },
  "categories": [
    {
      "id": "cat_hot_coffee",
      "name": "Hot Coffee",
      "icon": "☕",
      "item_count": 2
    },
    {
      "id": "cat_cold_brews",
      "name": "Cold Brews",
      "icon": "🧊",
      "item_count": 2
    },
    {
      "id": "cat_bakery",
      "name": "Artisan Bakery",
      "icon": "🥐",
      "item_count": 2
    }
  ],
  "items": [ /* Array of Product Items */ ]
}
```

### 2.2. Product Item Schema (`api/items/{id}.json` and `items[]`)

```json
{
  "id": 1,
  "category_id": "cat_hot_coffee",
  "name": "Toasted Caramel Macchiato",
  "tagline": "Double shot espresso with velvety oat milk & salted caramel",
  "description": "Rich espresso roast layered with velvety steamed milk, infused with house-made vanilla bean syrup and marked with a buttery toasted caramel drizzle.",
  "base_price": 4.85,
  "rating": 4.9,
  "review_count": 312,
  "prep_time": "5-7 mins",
  "calories": 240,
  "image_url": "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=800&auto=format&fit=crop&q=80",
  "badge": "BESTSELLER",
  "ingredients": [
    "Signature Espresso",
    "Steamed Oat Milk",
    "Salted Caramel",
    "Bourbon Vanilla"
  ],
  "customizations": {
    "sizes": [
      { "id": "sz_small", "label": "Tall (8 oz)", "extra_price": 0.00 },
      { "id": "sz_medium", "label": "Grande (12 oz)", "extra_price": 0.65 },
      { "id": "sz_large", "label": "Venti (16 oz)", "extra_price": 1.25 }
    ],
    "sugar_levels": [
      "0% Unsweetened",
      "50% Mild",
      "100% Standard Sweet"
    ],
    "milk_options": [
      { "id": "m_oat", "name": "Oat Milk (Barista Blend)", "extra_price": 0.00 },
      { "id": "m_almond", "name": "Roasted Almond Milk", "extra_price": 0.50 },
      { "id": "m_whole", "name": "Organic Whole Milk", "extra_price": 0.00 }
    ]
  }
}
```

---

## 3. Complete Product Catalog Reference

| ID | Name | Category ID | Base Price | Rating (Reviews) | Badge | Prep Time | Calories | Key Ingredients |
| :---: | :--- | :--- | :---: | :---: | :--- | :---: | :---: | :--- |
| **1** | **Toasted Caramel Macchiato** | `cat_hot_coffee` | $4.85 | 4.9 (312) | `BESTSELLER` | 5-7 mins | 240 | Signature Espresso, Steamed Oat Milk, Salted Caramel, Bourbon Vanilla |
| **2** | **Velvet Vanilla Cold Foam** | `cat_cold_brews` | $5.40 | 4.8 (248) | `STAFF PICK` | 3-5 mins | 180 | 18hr Steeped Coffee, Vanilla Sweet Cream, Crushed Ice, Raw Cane Sugar |
| **3** | **Golden Normandy Croissant** | `cat_bakery` | $3.90 | 4.95 (420) | `FRESHLY BAKED` | Warm in 2 mins | 290 | Normandy Butter (84%), Organic Flour, Natural Yeast, Sea Salt |
| **4** | **Crushed Pistachio Danish** | `cat_bakery` | $4.60 | 4.75 (184) | `SEASONAL` | Ready to serve | 330 | Bronte Pistachio Paste, Vanilla Custard, Puff Pastry, Icing Sugar |
| **5** | **Havana Spanish Latte** | `cat_hot_coffee` | $5.10 | 4.88 (195) | `POPULAR` | 4-6 mins | 260 | Dark Roast Espresso, Condensed Milk, Steamed Whole Milk, Ground Ceylon Cinnamon |
| **6** | **Nitro Cherry Cascara** | `cat_cold_brews` | $5.60 | 4.7 (89) | `NITRO ON TAP` | 1 min pour | 45 | Cascara Coffee Cherries, Liquid Nitrogen Infusion, Organic Meyer Lemon Peel |

---

## 4. Kotlin Data Models (`kotlinx.serialization`)

```kotlin
package com.clickretina.brewkery.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuResponse(
    @SerialName("meta") val meta: StoreMeta,
    @SerialName("categories") val categories: List<CategoryDto>,
    @SerialName("items") val items: List<ProductItemDto>
)

@Serializable
data class StoreMeta(
    @SerialName("app") val app: String = "Brewkery",
    @SerialName("version") val version: String = "1.0.0",
    @SerialName("tagline") val tagline: String = "",
    @SerialName("currency") val currency: String = "USD",
    @SerialName("currency_symbol") val currencySymbol: String = "$",
    @SerialName("delivery_fee") val deliveryFee: Double = 2.50,
    @SerialName("tax_rate_percent") val taxRatePercent: Double = 8.0,
    @SerialName("estimated_delivery_time") val estimatedDeliveryTime: String = "20 - 30 mins"
)

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("icon") val icon: String,
    @SerialName("item_count") val itemCount: Int
)

@Serializable
data class ProductItemDto(
    @SerialName("id") val id: Int,
    @SerialName("category_id") val categoryId: String,
    @SerialName("name") val name: String,
    @SerialName("tagline") val tagline: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("base_price") val basePrice: Double,
    @SerialName("rating") val rating: Double,
    @SerialName("review_count") val reviewCount: Int = 0,
    @SerialName("prep_time") val prepTime: String = "",
    @SerialName("calories") val calories: Int = 0,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("badge") val badge: String = "",
    @SerialName("ingredients") val ingredients: List<String> = emptyList(),
    @SerialName("customizations") val customizations: CustomizationsDto = CustomizationsDto()
)

@Serializable
data class CustomizationsDto(
    @SerialName("sizes") val sizes: List<SizeOptionDto> = emptyList(),
    @SerialName("sugar_levels") val sugarLevels: List<String> = emptyList(),
    @SerialName("milk_options") val milkOptions: List<MilkOptionDto> = emptyList()
)

@Serializable
data class SizeOptionDto(
    @SerialName("id") val id: String,
    @SerialName("label") val label: String,
    @SerialName("extra_price") val extraPrice: Double = 0.0
)

@Serializable
data class MilkOptionDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("extra_price") val extraPrice: Double = 0.0
)
```

---

## 5. Screen Specifications & UI Behavior

### Screen 1: Product Menu (Home)
- **Top App Bar**:
  - Left: "BK" dark circle monogram, "Fresh Roast & Bakes", and "Brewkery Artisans" title.
  - Right: Cart icon button displaying a real-time orange badge with total cart quantity.
- **Dynamic Store Info / Active Order Card**:
  - *Default State*: Shows 🛵 icon, "STORE INFO", "Delivery in 20 - 30 mins", "$2.50 flat fee", and "Open" pill.
  - *Active Order State*: Once an order is submitted, switches to ☕ icon, "ACTIVE ORDER" (green), "Active Order #BK-XXXXX", "Preparing (Arriving in 25 mins)", with an interactive "Track" button directing to Screen 4.
- **Search Bar**:
  - "Search roast, cold brew, pastry..." filtering the items list in real time against item title and description.
- **Category Filter Chips Row**:
  - Horizontal pill list: `All Items`, `☕ Hot Coffee`, `🧊 Cold Brews`, `🥐 Artisan Bakery`.
  - Selected chip is styled with dark contrast (`#140b07`), unselected with clean crema border.
- **Menu Product List**:
  - Card with:
    - 56x56 dp rounded image loaded with Coil.
    - Badge tag (e.g. `BESTSELLER`, `STAFF PICK`, `FRESHLY BAKED`).
    - Item name (bold, truncated).
    - Star rating with review count: `⭐ 4.9 (312)`.
    - Base price formatted: `$4.85`.
    - "+ Customize" button navigating to Screen 2.
- **Floating Cart Bar**:
  - Visible whenever `cart.isNotEmpty()`.
  - Shows item count badge, subtotal, and "Proceed to Checkout →" button opening Screen 3.

### Screen 2: Item Customizer (Detail)
- **Header**:
  - Circular Back button to return to Menu.
  - "ITEM CUSTOMIZER" centered label.
  - Heart icon button to toggle favorites.
- **Hero Image & Badge**:
  - High-res cropped product photo.
  - Translucent badge overlay (e.g., `BESTSELLER`).
- **Product Overview**:
  - Product name in bold display typography.
  - Base price in terracotta accent (`$4.85`).
  - Full product description.
- **Key Ingredients**:
  - Horizontal chip tags displaying ingredients from `item.ingredients`.
- **Dynamic Customizer Sections**:
  1. **Size Selection**: Grid of sizes showing label and extra price (`+$0.65`). Default: index 0.
  2. **Milk Options / Spreads**: Option cards with extra price (`+$0.50`). Default: index 0.
  3. **Sugar Levels / Serving**: Chip options (e.g. `0% Unsweetened`, `50% Mild`, `100% Standard Sweet`). Default: index 0.
- **Price Calculation Formula**:
  $$\text{Unit Price} = \text{base\_price} + \text{size.extra\_price} + \text{milk.extra\_price}$$
  $$\text{Total Price} = \text{Unit Price} \times \text{quantity}$$
- **Quantity Stepper & Add to Cart**:
  - Stepper `[- qty +]`.
  - Primary button: "Add to Cart • ${total}". Adds configured item to cart, triggers feedback toast, and transitions to Screen 3 (Cart).

### Screen 3: Cart & Checkout
- **Header**:
  - Back button navigating to Menu.
  - "YOUR CART" title.
  - "Clear Cart" button (red text) with empty state reset.
- **Cart Items List**:
  - Item name.
  - Customization summary (`Grande (12 oz) • Oat Milk (Barista Blend)`).
  - Calculated item total price (`unitPrice * quantity`).
  - Quantity adjuster `[- qty +]` (stepper decrements quantity; if qty becomes 0, item is removed from cart).
- **Bill Breakdown Card**:
  - **Subtotal**: $\sum (\text{unitPrice} \times \text{quantity})$
  - **Delivery Fee**: `$2.50` (or `$0.00` if cart is empty)
  - **Est. Tax (8.0%)**: $\text{Subtotal} \times 0.08$
  - **Total Payable**: $\text{Subtotal} + \text{Delivery Fee} + \text{Est. Tax}$
- **Place Order Action**:
  - Terracotta gradient button: "Place Order Now • ${total}".
  - Triggers checkout: clears in-memory cart, assigns order reference code (e.g. `#BK-74921`), sets active order state, and transitions to Screen 4.

### Screen 4: Order Status
- **Success Graphic**:
  - Circular badge with coffee cup icon.
  - "Order Dispatched" badge in terracotta.
  - "Brewing in Progress!" headline.
  - "Your ticket was dispatched to our barista."
- **Order Ticket Card**:
  - Order reference ID (`#BK-XXXXX`).
  - Status chip: `PREPARING` (pulsing indicator).
  - Estimated wait: `20 - 30 minutes`.
  - Items ordered count: `X Items`.
  - Live status note: `Barista accepted your order!`.
- **Navigation Action**:
  - "Back to Menu" button returning to Home with the active order banner active.

---

## 6. Error Handling & Loading States

1. **Network Loading State**:
   - Screen 1 displays a centered circular progress indicator while fetching `data.json`.
   - Screen 2 shows a skeleton/progress state while resolving `api/items/{id}.json`.
2. **Network Error Handling**:
   - If an `IOException` or HTTP failure occurs, show an error banner with a "Retry" button.
   - Fall back smoothly to the bundled offline catalog so user testing and offline grading remain 100% functional.
3. **Empty States**:
   - Menu: "No coffee items found" when search yields zero items.
   - Cart: Empty cart graphic with "Your cart is empty" and "Explore Coffee Menu" action button.

