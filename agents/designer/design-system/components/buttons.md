# Floktask Design System - Buttons
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05

## 📋 Overview

Buttons are the primary call-to-action elements in Floktask. This document defines all button variants, states, and usage guidelines.

---

## 🎨 Button Variants

### 1. Primary Button
**Purpose:** Main actions, most important CTAs

| Property | Value | Token |
|----------|-------|-------|
| Background | Primary 500 | `colors.primary.500` |
| Text Color | On Primary | `colors.on_primary` |
| Border | None | - |
| Border Radius | 12dp | `border_radius.md` |
| Padding | 24h x 12v | `spacing.lg` x `spacing.sm` |
| Min Height | 40dp | `spacing.xl_2` |
| Typography | Button Large | `typography.button.large` |

**Usage:**
- Create task
- Save changes
- Confirm actions

---

### 2. Primary Button (Dark Theme)

| Property | Value | Token |
|----------|-------|-------|
| Background | Primary 600 | `colors.primary.600` |
| Text Color | On Primary | `colors.on_primary` |

---

### 3. Secondary Button
**Purpose:** Secondary actions, less prominent

| Property | Value | Token |
|----------|-------|-------|
| Background | Secondary 500 | `colors.secondary.500` |
| Text Color | On Secondary | `colors.on_secondary` |
| Border | None | - |
| Border Radius | 12dp | `border_radius.md` |
| Padding | 24h x 12v | `spacing.lg` x `spacing.sm` |

**Usage:**
- Cancel actions
- Back navigation
- Alternative options

---

### 4. Outlined Button
**Purpose:** Tertiary actions, subtle emphasis

| Property | Value | Token |
|----------|-------|-------|
| Background | Transparent | - |
| Text Color | Primary 500 | `colors.primary.500` |
| Border | 1dp Primary 500 | `colors.primary.500` |
| Border Radius | 12dp | `border_radius.md` |
| Padding | 24h x 12v | `spacing.lg` x `spacing.sm` |

**Usage:**
- Filter buttons
- Toggle options
- Less important actions

---

### 5. Text Button
**Purpose:** Minimal emphasis, inline actions

| Property | Value | Token |
|----------|-------|-------|
| Background | Transparent | - |
| Text Color | Primary 500 | `colors.primary.500` |
| Border | None | - |
| Border Radius | 0dp | `border_radius.none` |
| Padding | 8h x 4v | `spacing.sm` x `spacing.xs` |

**Usage:**
- Inline actions in text
- Small contextual actions
- Link-like behavior

---

### 6. Icon Button
**Purpose:** Action buttons with icons only

| Property | Value | Token |
|----------|-------|-------|
| Background | Transparent | - |
| Icon Color | On Surface Variant | `colors.on_surface_variant` |
| Border | None | - |
| Border Radius | 12dp | `border_radius.md` |
| Size | 40dp x 40dp | `spacing.xl_2` |
| Icon Size | 24dp | `spacing.lg_2` |
| Ripple | Circular | - |

**Variants:**
- **Filled:** Background = Surface Container High
- **Tonal:** Background = Secondary Container
- **Outlined:** Border = Outline

---

### 7. Floating Action Button (FAB)
**Purpose:** Primary floating action

| Property | Value | Token |
|----------|-------|-------|
| Background | Primary 500 | `colors.primary.500` |
| Icon Color | On Primary | `colors.on_primary` |
| Border Radius | 16dp | `border_radius.md_2` |
| Size | 56dp x 56dp | Custom |
| Icon Size | 24dp | `spacing.lg_2` |
| Shadow | Level 4 | `shadows.level_4` |
| Shadow (Pressed) | Level 3 | `shadows.level_3` |

**Variants:**
- **Standard:** 56dp
- **Small:** 40dp
- **Extended:** With text label

---

### 8. App Floating Action Button
**Purpose:** Premium visual system FAB

| Property | Value | Token |
|----------|-------|-------|
| Background | Primary Gradient | `premium.gradient_1` |
| Icon Color | White | `#FFFFFF` |
| Border Radius | 28dp | `border_radius.xl` |
| Size | 56dp x 56dp | Custom |
| Press Scale | 0.92 | - |
| Animation | 150ms tween | - |

---

## 🎭 Button States

### All Button Variants Support:

| State | Description | Visual Treatment |
|-------|-------------|------------------|
| **Default** | Normal state | Standard colors |
| **Hover** | Mouse over | Elevation increase, color shift |
| **Focus** | Keyboard focus | Focus ring (2dp) |
| **Pressed** | Active press | Scale down (0.96-0.98), elevation decrease |
| **Disabled** | Not interactive | 40% opacity, no interaction |
| **Loading** | Async operation | Spinner + disabled state |

---

### Primary Button States

| State | Background | Text Color | Elevation |
|-------|------------|------------|-----------|
| Default | Primary 500 | On Primary | Level 1 |
| Hover | Primary 600 | On Primary | Level 2 |
| Focus | Primary 500 | On Primary | Level 1 + Ring |
| Pressed | Primary 700 | On Primary | Level 0 |
| Disabled | Primary 500 (40%) | On Primary (40%) | Level 0 |

---

### Outlined Button States

| State | Background | Text Color | Border | Elevation |
|-------|------------|------------|--------|-----------|
| Default | Transparent | Primary 500 | Primary 500 | Level 0 |
| Hover | Primary 50 (12%) | Primary 500 | Primary 500 | Level 0 |
| Focus | Transparent | Primary 500 | Primary 500 | Level 0 + Ring |
| Pressed | Primary 50 (24%) | Primary 500 | Primary 500 | Level 0 |
| Disabled | Transparent | Primary 500 (40%) | Primary 500 (40%) | Level 0 |

---

### FAB States

| State | Background | Icon Color | Shadow | Scale |
|-------|------------|------------|--------|-------|
| Default | Primary 500 | On Primary | Level 4 | 1.0 |
| Hover | Primary 600 | On Primary | Level 5 | 1.0 |
| Focus | Primary 500 | On Primary | Level 4 | 1.0 + Ring |
| Pressed | Primary 700 | On Primary | Level 3 | 0.92 |
| Disabled | Primary 500 (40%) | On Primary (40%) | Level 0 | 1.0 |

---

## 📐 Button Sizes

### Large Buttons
- **Height:** 48dp (`spacing.xxl_2`)
- **Padding:** 32h x 16v (`spacing.xl` x `spacing.md_2`)
- **Typography:** Button Large (`typography.button.large`)

### Medium Buttons (Default)
- **Height:** 40dp (`spacing.xl_2`)
- **Padding:** 24h x 12v (`spacing.lg` x `spacing.sm`)
- **Typography:** Button Medium (`typography.button.medium`)

### Small Buttons
- **Height:** 32dp (`spacing.lg_2`)
- **Padding:** 16h x 8v (`spacing.md` x `spacing.sm`)
- **Typography:** Button Small (`typography.button.small`)

---

## 🎯 Button Usage Guidelines

### Do's ✅
1. **Use Primary buttons for main actions** - Create, Save, Confirm
2. **Use Secondary buttons for secondary actions** - Cancel, Back
3. **Use Outlined buttons for tertiary actions** - Filter, Toggle
4. **Use Text buttons for minimal emphasis** - Inline actions
5. **Use FAB for primary floating actions** - Add new item
6. **Maintain consistent spacing** - 8dp between buttons
7. **Use appropriate size** - Large for important, Small for compact spaces

### Don'ts ❌
1. **Don't use multiple Primary buttons in one view** - Only one primary CTA
2. **Don't use disabled buttons without explanation** - Provide context
3. **Don't use buttons for navigation** - Use navigation components
4. **Don't use buttons for non-actions** - Use text or cards instead
5. **Don't break the hierarchy** - Primary > Secondary > Outlined > Text

---

## 🔗 Button Component Specifications

### Props

```kotlin
@Composable
fun FloktaskButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: ButtonColors = FloktaskButtonDefaults.colors(variant),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
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

- **Component:** Buttons
- **Library:** Floktask Design System
- **Frame:** Button Variants

---

*Last updated: 2026-09-05*
