# Floktask Design System - Input Components
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05

## 📋 Overview

Input components allow users to enter and edit text, numbers, dates, and other data. This document defines all input variants, states, and usage guidelines for Floktask.

---

## 🎨 Input Variants

### 1. Text Field (Single Line)
**Purpose:** Short text input

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 12dp | `border_radius.md` |
| Height | 56dp | `spacing.xxxl` |
| Padding | 16h x 16v | `spacing.md` |
| Typography | Body Medium | `typography.body.medium` |

**States:**
- **Default:** Border = Outline
- **Focused:** Border = Primary 500, 2dp
- **Error:** Border = Error 500, 2dp
- **Disabled:** Background = Surface (40%), Border = Outline (40%)

---

### 2. Outlined Text Field
**Purpose:** Text input with outlined border

| Property | Value | Token |
|----------|-------|-------|
| Background | Transparent | - |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 12dp | `border_radius.md` |
| Height | 56dp | `spacing.xxxl` |
| Padding | 16h x 16v | `spacing.md` |

**States:**
- **Default:** Border = Outline
- **Focused:** Border = Primary 500, 2dp
- **Error:** Border = Error 500, 2dp

---

### 3. Filled Text Field
**Purpose:** Text input with filled background

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface Container Highest | `colors.surface_container_highest` |
| Border | None | - |
| Border Radius | 12dp | `border_radius.md` |
| Height | 56dp | `spacing.xxxl` |
| Padding | 16h x 16v | `spacing.md` |

**States:**
- **Default:** Background = Surface Container Highest
- **Focused:** Background = Surface Container High, Border = Primary 500 (bottom only)
- **Error:** Background = Error 50 (12%)

---

### 4. Multiline Text Field (Text Area)
**Purpose:** Long text input, descriptions, notes

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 12dp | `border_radius.md` |
| Min Height | 120dp | Custom |
| Padding | 16h x 16v | `spacing.md` |
| Typography | Body Medium | `typography.body.medium` |

**Features:**
- Auto-grow with content
- Scrollable when exceeds max height
- Character counter (optional)

---

### 5. Search Field
**Purpose:** Search input with icon

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 24dp | `border_radius.lg_2` (Pill shape) |
| Height | 48dp | `spacing.xxl_2` |
| Padding | 16h x 16v | `spacing.md` |
| Leading Icon | Search (24dp) | `spacing.lg_2` |
| Placeholder | "Search..." | - |

**States:**
- **Default:** Border = Outline
- **Focused:** Border = Primary 500, 2dp
- **With Text:** Clear button appears

---

### 6. Dropdown / Select
**Purpose:** Single selection from list

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 12dp | `border_radius.md` |
| Height | 48dp | `spacing.xxl_2` |
| Padding | 16h x 16v | `spacing.md` |
| Typography | Body Medium | `typography.body.medium` |
| Trailing Icon | Dropdown Arrow | - |

**States:**
- **Default:** Border = Outline
- **Focused:** Border = Primary 500, 2dp
- **Open:** Border = Primary 500, 2dp, Dropdown open
- **Error:** Border = Error 500, 2dp

---

### 7. Date Picker
**Purpose:** Date selection

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 12dp | `border_radius.md` |
| Height | 48dp | `spacing.xxl_2` |
| Padding | 16h x 16v | `spacing.md` |
| Typography | Body Medium | `typography.body.medium` |
| Trailing Icon | Calendar | - |

**Dialog:**
- Modal with calendar picker
- Today highlighted in Primary 500
- Selected date in Primary 500
- Navigation arrows

---

### 8. Time Picker
**Purpose:** Time selection

| Property | Value | Token |
|----------|-------|-------|
| Background | Surface | `colors.surface` |
| Border | 1dp Outline | `colors.outline` |
| Border Radius | 12dp | `border_radius.md` |
| Height | 48dp | `spacing.xxl_2` |
| Padding | 16h x 16v | `spacing.md` |
| Typography | Body Medium | `typography.body.medium` |
| Trailing Icon | Clock | - |

**Dialog:**
- Modal with time picker
- Hour/Minute selection
- AM/PM toggle (if applicable)

---

### 9. Switch
**Purpose:** Toggle between two states

| Property | Value | Token |
|----------|-------|-------|
| Track Width | 48dp | Custom |
| Track Height | 24dp | `spacing.lg_2` |
| Track Radius | 12dp | `border_radius.md` |
| Thumb Size | 20dp | Custom |
| Thumb Radius | 10dp | Custom |
| Padding | 2dp | Custom |

**States:**
- **On:** Track = Primary 500, Thumb = On Primary
- **Off:** Track = Outline, Thumb = Surface
- **Disabled:** 40% opacity

---

### 10. Checkbox
**Purpose:** Multiple selection, boolean choice

| Property | Value | Token |
|----------|-------|-------|
| Size | 20dp x 20dp | Custom |
| Border Radius | 4dp | `border_radius.xxs` |
| Border | 2dp Outline | `colors.outline` |
| Check Size | 14dp | Custom |
| Check Color | On Primary | `colors.on_primary` |

**States:**
- **Default:** Border = Outline, Background = Surface
- **Selected:** Background = Primary 500, Border = Primary 500
- **Indeterminate:** Dash icon, Background = Primary 500
- **Disabled:** 40% opacity
- **Error:** Border = Error 500

---

### 11. Radio Button
**Purpose:** Single selection from group

| Property | Value | Token |
|----------|-------|-------|
| Outer Size | 20dp | Custom |
| Inner Size | 12dp | Custom |
| Border | 2dp Outline | `colors.outline` |
| Dot Size | 8dp | Custom |
| Dot Color | Primary 500 | `colors.primary.500` |

**States:**
- **Default:** Border = Outline, Background = Surface
- **Selected:** Border = Primary 500, Dot = Primary 500
- **Disabled:** 40% opacity
- **Error:** Border = Error 500

---

### 12. Slider
**Purpose:** Continuous value selection

| Property | Value | Token |
|----------|-------|-------|
| Track Height | 4dp | Custom |
| Track Radius | 2dp | `border_radius.xs` |
| Track Color | Outline | `colors.outline` |
| Active Track Color | Primary 500 | `colors.primary.500` |
| Thumb Size | 20dp | Custom |
| Thumb Radius | 10dp | Custom |
| Thumb Color | On Primary | `colors.on_primary` |
| Thumb Border | 2dp Primary 500 | `colors.primary.500` |

**States:**
- **Default:** Track = Outline, Thumb = On Primary
- **Active:** Track = Primary 500 (active part)
- **Disabled:** 40% opacity

---

## 🎭 Input States

### All Inputs Support:

| State | Description | Visual Treatment |
|-------|-------------|------------------|
| **Default** | Normal state | Standard colors |
| **Focus** | Keyboard focus | Focus ring (2dp) + color change |
| **Hover** | Mouse over | Subtle color change |
| **Pressed** | Active press | Ripple effect |
| **Disabled** | Not interactive | 40% opacity, no interaction |
| **Error** | Validation error | Error color, error message |
| **Read Only** | View only | No border, subtle background |

---

### Text Field States

| State | Border | Background | Label |
|-------|--------|------------|-------|
| Default | Outline (1dp) | Surface | On Surface Variant |
| Focused | Primary 500 (2dp) | Surface | Primary 500 |
| Error | Error 500 (2dp) | Surface | Error 500 |
| Disabled | Outline (40%) | Surface (40%) | On Surface (40%) |

---

### Filled Text Field States

| State | Border | Background | Label |
|-------|--------|------------|-------|
| Default | None | Surface Container Highest | On Surface Variant |
| Focused | Primary 500 (bottom, 2dp) | Surface Container High | Primary 500 |
| Error | None | Error 50 (12%) | Error 500 |
| Disabled | None | Surface Container (40%) | On Surface (40%) |

---

## 📐 Input Sizing

### Large Inputs
- **Height:** 56dp (`spacing.xxxl`)
- **Padding:** 16h x 16v (`spacing.md`)
- **Typography:** Body Large (`typography.body.large`)

### Medium Inputs (Default)
- **Height:** 48dp (`spacing.xxl_2`)
- **Padding:** 16h x 16v (`spacing.md`)
- **Typography:** Body Medium (`typography.body.medium`)

### Small Inputs
- **Height:** 40dp (`spacing.xl_2`)
- **Padding:** 12h x 12v (`spacing.sm_2`)
- **Typography:** Body Small (`typography.body.small`)

---

## 🎯 Input Usage Guidelines

### Do's ✅
1. **Use appropriate input type** - Text for text, Number for numbers, etc.
2. **Always include labels** - Never use placeholder as only label
3. **Use helper text for guidance** - Explain format, requirements
4. **Validate on blur** - Don't validate on every keystroke
5. **Show error states clearly** - Use error color and message
6. **Use appropriate keyboard** - Numeric for numbers, Email for emails
7. **Consider accessibility** - Minimum touch target 48dp

### Don'ts ❌
1. **Don't use placeholder as label** - Placeholder disappears on focus
2. **Don't disable without reason** - Provide explanation if disabled
3. **Don't use too many inputs** - Break long forms into sections
4. **Don't change input type** - Keep consistent behavior
5. **Don't ignore validation** - Always provide feedback

---

## 🔗 Input Component Specifications

### Text Field Props

```kotlin
@Composable
fun FloktaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    helperText: String? = null,
    errorText: String? = null,
    variant: TextFieldVariant = TextFieldVariant.Outlined,
    size: TextFieldSize = TextFieldSize.Medium,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors = FloktaskTextFieldDefaults.colors(variant),
    shape: Shape = RoundedCornerShape(12.dp)
) {
    // Implementation
}
```

---

## 📁 Related Files

- [Color Tokens](../tokens/colors.yaml)
- [Typography Tokens](../tokens/typography.yaml)
- [Spacing Tokens](../tokens/spacing.yaml)
- [Border Radius Tokens](../tokens/border-radius.yaml)

---

## 🎨 Figma Reference

- **Component:** Input Fields
- **Library:** Floktask Design System
- **Frame:** Input Variants

---

*Last updated: 2026-09-05*
