# Floktask Design System - Cards
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05

## 📋 Overview

Cards are surface elements that display content and actions on a single topic. They act as entry points to more detailed information.

---

## 🎨 Card Variants

### 1. Standard Card
**Purpose:** Default card for most use cases

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | None | - |
| Border Radius | 12dp | `border_radius.md` |
| Padding | 16dp | `spacing.md` |
| Elevation | Level 2 | `shadows.level_2` |
| Typography | Body Medium | `typography.body.medium` |

**Usage:**
- Task cards
- Note cards
- Settings sections

---

### 2. Elevated Card
**Purpose:** Cards with more prominence

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | None | - |
| Border Radius | 12dp | `border_radius.md` |
| Padding | 16dp | `spacing.md` |
| Elevation | Level 3 | `shadows.level_3` |

**Usage:**
- Dashboard cards
- Featured content
- Premium sections

---

### 3. Outlined Card
**Purpose:** Cards with subtle border

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 12dp | `border_radius.md` |
| Padding | 16dp | `spacing.md` |
| Elevation | Level 0 | `shadows.level_0` |

**Usage:**
- Form sections
- Input groups
- Bordered containers

---

### 4. Premium Card
**Purpose:** Cards with premium visual treatment

| Property | Value | Token |
|----------|-------|-------|
| Background | Premium Subtle | `premium.subtle_background` |
| Border | None | - |
| Border Radius | 16dp | `border_radius.md_2` |
| Padding | 20dp | `spacing.md_2` |
| Elevation | Level 2 | `shadows.level_2` |
| Shadow | Premium Soft | `shadows.premium_soft` |

**Usage:**
- Premium features
- Empty states
- Special content

---

### 5. Compact Card
**Purpose:** Cards for dense layouts

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | None | - |
| Border Radius | 8dp | `border_radius.sm_2` |
| Padding | 12dp | `spacing.sm` |
| Elevation | Level 1 | `shadows.level_1` |

**Usage:**
- Task list items
- Compact previews
- Dense layouts

---

## 📐 Card Sizes

### Large Cards
- **Min Width:** 360dp
- **Padding:** 24dp (`spacing.lg_2`)
- **Elevation:** Level 3 (`shadows.level_3`)

### Medium Cards (Default)
- **Min Width:** 280dp
- **Padding:** 16dp (`spacing.md`)
- **Elevation:** Level 2 (`shadows.level_2`)

### Small Cards
- **Min Width:** 200dp
- **Padding:** 12dp (`spacing.sm`)
- **Elevation:** Level 1 (`shadows.level_1`)

---

## 🎭 Card States

| State | Description | Visual Treatment |
|-------|-------------|------------------|
| **Default** | Normal state | Standard elevation |
| **Hover** | Mouse over | Elevation increase (Level 2 → Level 3) |
| **Focus** | Keyboard focus | Focus ring (2dp) |
| **Pressed** | Active press | Elevation decrease (Level 2 → Level 1) |
| **Selected** | Selected state | Border = Primary 500, Elevation = Level 3 |
| **Disabled** | Not interactive | 40% opacity |

---

## 🎯 Card Content Areas

### Header
- **Height:** 40dp (`spacing.xl_2`)
- **Padding:** 16dp (`spacing.md`)
- **Typography:** Title Medium (`typography.title.medium`)
- **Border Bottom:** 1dp Outline Variant (`colors.outline_variant`)

### Content
- **Padding:** 16dp (`spacing.md`)
- **Typography:** Body Medium (`typography.body.medium`)

### Actions
- **Padding:** 8dp (`spacing.sm`)
- **Spacing:** 8dp between buttons (`spacing.sm`)
- **Alignment:** End (right)

### Footer
- **Height:** 32dp (`spacing.lg_2`)
- **Padding:** 16dp (`spacing.md`)
- **Border Top:** 1dp Outline Variant (`colors.outline_variant`)

---

## 🏗️ Card Layout Patterns

### 1. Task Card
```
┌─────────────────────────────────────┐
│ Header: Title + Priority Badge        │
├─────────────────────────────────────┤
│ Content: Description                   │
│          Due Date + Tags              │
├─────────────────────────────────────┤
│ Actions: Checkbox + Menu              │
└─────────────────────────────────────┘
```

**Specs:**
- **Width:** 100% (parent)
- **Padding:** 16dp (`spacing.md`)
- **Header Height:** Auto
- **Content Spacing:** 8dp (`spacing.sm`)
- **Action Spacing:** 8dp (`spacing.sm`)

---

### 2. Finance Card
```
┌─────────────────────────────────────┐
│ Balance Amount (Large)               │
│ Category Label                       │
├─────────────────────────────────────┤
│ Chart/Graph                         │
├─────────────────────────────────────┤
│ Recent Transactions                  │
└─────────────────────────────────────┘
```

**Specs:**
- **Width:** 100% (parent)
- **Padding:** 20dp (`spacing.md_2`)
- **Balance Font:** Finance Amount Large (`typography.finance.amount_large`)
- **Chart Height:** 120dp

---

### 3. Note Card
```
┌─────────────────────────────────────┐
│ Header: Title + Pin Icon              │
├─────────────────────────────────────┤
│ Content: Preview Text                 │
│          (2 lines max)                 │
├─────────────────────────────────────┤
│ Footer: Date + Folder + Menu          │
└─────────────────────────────────────┘
```

**Specs:**
- **Width:** 100% (parent)
- **Padding:** 16dp (`spacing.md`)
- **Preview Lines:** 2
- **Footer Height:** 32dp (`spacing.lg_2`)

---

### 4. Dashboard Card
```
┌─────────────────────────────────────┐
│ Icon + Title                          │
├─────────────────────────────────────┤
│ Value (Large)                         │
│ Subtitle                              │
├─────────────────────────────────────┤
│ Action Button                        │
└─────────────────────────────────────┘
```

**Specs:**
- **Width:** 160dp (minimum)
- **Padding:** 20dp (`spacing.md_2`)
- **Value Font:** Display Small (`typography.display.small`)

---

## 🎨 Card Styling

### Background Colors by Context

| Context | Background | On Background |
|---------|------------|---------------|
| Default | Surface | On Surface |
| Primary | Primary 50 | Primary 900 |
| Secondary | Secondary 50 | Secondary 900 |
| Error | Error 50 | Error 900 |
| Warning | Warning 50 | Warning 900 |
| Success | Success 50 | Success 900 |

---

### Border Colors by Context

| Context | Border Color |
|---------|--------------|
| Default | Outline |
| Selected | Primary 500 |
| Error | Error 500 |
| Warning | Warning 500 |
| Success | Success 500 |

---

## 📱 Responsive Behavior

### Mobile
- **Width:** 100% - 16dp margin
- **Elevation:** Level 2
- **Border Radius:** 12dp

### Tablet
- **Width:** 360dp (max)
- **Elevation:** Level 2
- **Border Radius:** 12dp

### Desktop
- **Width:** 400dp (max)
- **Elevation:** Level 3
- **Border Radius:** 16dp

---

## 🔗 Component Specifications

### Props

```kotlin
@Composable
fun FloktaskCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Standard,
    elevation: Elevation = CardDefaults.elevation(variant),
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.colors(variant),
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    // Implementation
}
```

---

## 📁 Related Files

- [Color Tokens](../tokens/colors.yaml)
- [Typography Tokens](../tokens/typography.yaml)
- [Spacing Tokens](../tokens/spacing.yaml)
- [Shadow Tokens](../tokens/shadows.yaml)
- [Border Radius Tokens](../tokens/border-radius.yaml)

---

## 🎨 Figma Reference

- **Component:** Cards
- **Library:** Floktask Design System
- **Frame:** Card Variants

---

*Last updated: 2026-09-05*
