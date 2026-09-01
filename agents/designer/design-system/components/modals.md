# Floktask Design System - Modals & Dialogs
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05

## 📋 Overview

Modals and dialogs are temporary surfaces that appear in front of app content to provide critical information or ask for decisions. This document defines all modal variants, states, and usage guidelines for Floktask.

---

## 🎨 Modal Variants

### 1. Alert Dialog
**Purpose:** Critical decisions, confirmations, warnings

```
┌─────────────────────────────────────┐
│                                     │
│  Title (Optional)                    │
│  ━━━━━━━━━━━━━━━━━━━━━━━━        │
│                                     │
│  Message text explaining the         │
│  situation or asking for decision    │
│                                     │
│  ━━━━━━━━━━━━━━━━━━━━━━━━        │
│                                     │
│  [Cancel]        [Confirm]          │
│                                     │
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Max Width | 560dp | Custom |
| Border Radius | 24dp | `border_radius.lg_2` |
| Padding | 24dp | `spacing.lg_2` |
| Elevation | Level 5 | `shadows.level_5` |
| Background | Surface | `colors.surface` |
| Title Typography | Title Large | `typography.title.large` |
| Message Typography | Body Medium | `typography.body.medium` |
| Button Spacing | 8dp | `spacing.sm` |

**Usage:**
- Confirm destructive actions
- Important decisions
- Error messages
- Warnings

---

### 2. Confirmation Dialog
**Purpose:** Simple yes/no confirmations

```
┌─────────────────────────────────────┐
│                                     │
│  Are you sure?                       │
│  ━━━━━━━━━━━━━━━━━━━━━━━━        │
│                                     │
│  This action cannot be undone.       │
│                                     │
│  ━━━━━━━━━━━━━━━━━━━━━━━━        │
│                                     │
│           [Cancel]    [Delete]       │
│                                     │
└─────────────────────────────────────┘
```

---

### 3. Input Dialog
**Purpose:** Collect user input in a modal

```
┌─────────────────────────────────────┐
│                                     │
│  Create New Task                     │
│  ━━━━━━━━━━━━━━━━━━━━━━━━        │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ Task Title                    │ │ │
│  └───────────────────────────────┘ │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ Description (Optional)       │ │ │
│  │                               │ │ │
│  │                               │ │ │
│  └───────────────────────────────┘ │
│                                     │
│  ━━━━━━━━━━━━━━━━━━━━━━━━        │
│                                     │
│           [Cancel]      [Save]      │
│                                     │
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Max Width | 560dp | Custom |
| Input Padding | 16dp | `spacing.md` |
| Input Spacing | 16dp | `spacing.md` |

---

### 4. Full Screen Dialog
**Purpose:** Complex forms or detailed content

```
┌─────────────────────────────────────┐
│  ← Back    Title              Close  │
├─────────────────────────────────────┤
│                                     │
│  Scrollable content area              │
│  Can contain multiple sections        │
│  Forms, lists, detailed information   │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  [Cancel]           [Save]            │
│                                     │
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 100% | Full screen |
| Border Radius | 0dp (top: 24dp) | `border_radius.lg_2` (top only) |
| Elevation | Level 4 | `shadows.level_4` |
| Background | Surface | `colors.surface` |

---

### 5. Bottom Sheet
**Purpose:** Contextual actions from bottom

```
┌─────────────────────────────────────┐
│                                     │
│  Existing content (dimmed)            │
│                                     │
├─────────────────────────────────────┤
│  ┌─────────────────────────────────┐│
│  │  ███ Draggable Handle            ││
│  │                                 ││
│  │  Title                          ││
│  │  ━━━━━━━━━━━━━━━━━━━━━━━        ││
│  │                                 ││
│  │  Action 1                       ││
│  │  Action 2                       ││
│  │  Action 3                       ││
│  │                                 ││
│  └─────────────────────────────────┘│
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | Auto (max 80%) | Custom |
| Border Radius | 24dp (top) | `border_radius.lg_2` (top only) |
| Elevation | Level 4 | `shadows.level_4` |
| Background | Surface | `colors.surface` |
| Handle Height | 4dp | Custom |
| Handle Width | 32dp | `spacing.xxl_2` |
| Handle Radius | 2dp | `border_radius.xs` |
| Handle Color | On Surface Variant | `colors.on_surface_variant` |

---

### 6. Snackbar
**Purpose:** Brief notifications at bottom

```
┌─────────────────────────────────────┐
│                                     │
│  Existing content                    │
│                                     │
├─────────────────────────────────────┤
│  ┌─────────────────────────────────┐│
│  │  Message text here              ││
│  │  Optional action button →       ││
│  └─────────────────────────────────┘│
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 48dp | `spacing.xxl_2` |
| Max Width | 600dp | Custom |
| Border Radius | 12dp | `border_radius.md` |
| Elevation | Level 5 | `shadows.level_5` |
| Background | Inverse Surface | `colors.inverse_surface` |
| Text Color | Inverse On Surface | `colors.inverse_on_surface` |
| Duration | 4.5 seconds | Custom |

---

### 7. Toast
**Purpose:** Very brief notifications

```
┌─────────────────────────────────────┐
│                                     │
│  ┌─────────────────────────────┐   │
│  │  Brief message               │   │
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | Auto | Custom |
| Max Width | 400dp | Custom |
| Border Radius | 8dp | `border_radius.sm_2` |
| Background | On Surface (90%) | `colors.on_surface` (90% opacity) |
| Text Color | Surface | `colors.surface` |
| Duration | 2 seconds | Custom |

---

### 8. Context Menu
**Purpose:** Actions on long-press or right-click

```
┌─────────────────────────────────────┐
│                                     │
│  ┌─────────────────────────────┐   │
│  │  Action 1                    │   │
│  ├─────────────────────────────┤   │
│  │  Action 2                    │   │
│  ├─────────────────────────────┤   │
│  │  Action 3                    │   │
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Min Width | 160dp | Custom |
| Max Width | 280dp | Custom |
| Item Height | 40dp | `spacing.xl_2` |
| Border Radius | 12dp | `border_radius.md` |
| Elevation | Level 3 | `shadows.level_3` |
| Background | Surface | `colors.surface` |
| Divider | 1dp Outline Variant | `colors.outline_variant` |
| Item Padding | 16h x 8v | `spacing.md` x `spacing.sm` |

---

### 9. Create Menu Sheet
**Purpose:** Quick creation from FAB (from NEXT_SESSION_TASK.md)

```
┌─────────────────────────────────────┐
│  ┌─────────────────────────────────┐│
│  │  + Create                       ││
│  ├─────────────────────────────────┤│
│  │  📝 Task                        ││
│  │  📁 Project                     ││
│  │  💰 Finance                     ││
│  │  📄 Note                        ││
│  │  🎯 Goal                        ││
│  │  📅 Habit                       ││
│  │  🎤 Voice Task Creation         ││
│  └─────────────────────────────────┘│
└─────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Width | 200dp | Custom |
| Border Radius | 16dp | `border_radius.md_2` |
| Elevation | Level 4 | `shadows.level_4` |
| Background | Surface | `colors.surface` |
| Item Height | 48dp | `spacing.xxl_2` |
| Icon Size | 24dp | `spacing.lg_2` |
| Text Typography | Label Large | `typography.label.large` |

---

### 10. Add Transaction Sheet
**Purpose:** Finance transaction input (from NEXT_SESSION_TASK.md)

```
┌─────────────────────────────────────┐
│  Add Transaction                     │
├─────────────────────────────────────┤
│  ┌─────────────────────────────────┐│
│  │ Amount                        │ ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ Category                      │ ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ Date                          │ ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ Description                   │ ││
│  └─────────────────────────────────┘│
│  Type: Income │ Expense │ Transfer     │
│  Currency: RUB │ USD │ EUR │ GBP        │
├─────────────────────────────────────┤
│  [Cancel]             [Save]           │
└─────────────────────────────────────┘
```

---

### 11. Voice Task Sheet
**Purpose:** Voice input for task creation (from NEXT_SESSION_TASK.md)

```
┌─────────────────────────────────────┐
│  Voice Task Creation                  │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────────┐│
│  │ 🎤 "Создать задачу на завтра"   ││
│  └─────────────────────────────────┘│
│                                     │
│  Parsed:                            │
│  - Action: Create task              │
│  - Date: Tomorrow                   │
│                                     │
│  ┌─────────────────────────────────┐│
│  │ Manual edit...                  │ ││
│  └─────────────────────────────────┘│
│                                     │
├─────────────────────────────────────┤
│  [Cancel]             [Confirm]        │
└─────────────────────────────────────┘
```

---

## 🎭 Modal States

### All Modals Support:

| State | Description | Visual Treatment |
|-------|-------------|------------------|
| **Open** | Visible | Full opacity, elevation |
| **Closing** | Transition out | Fade out + slide down |
| **Closed** | Not visible | Zero opacity, no elevation |

---

### Dialog States

| State | Background | Elevation | Scrim |
|-------|------------|-----------|-------|
| Open | Surface | Level 5 | 40% Black |
| Closing | Surface | Level 5 → 0 | 40% → 0% Black |

---

### Bottom Sheet States

| State | Elevation | Scrim | Draggable |
|-------|-----------|-------|------------|
| Open | Level 4 | 40% Black | Yes |
| Half Expanded | Level 4 | 40% Black | Yes |
| Fully Expanded | Level 4 | 40% Black | Yes |
| Closing | Level 4 → 0 | 40% → 0% | No |

---

## 📐 Modal Sizing

### Dialog Sizes
- **Small:** Max Width = 320dp
- **Medium (Default):** Max Width = 480dp
- **Large:** Max Width = 560dp
- **Full Screen:** Width = 100%, Height = 100%

### Bottom Sheet Sizes
- **Small:** Height = 200dp (40%)
- **Medium:** Height = 400dp (60%)
- **Large:** Height = 560dp (80%)
- **Full:** Height = 100% - Status Bar

---

## 🎯 Modal Usage Guidelines

### Do's ✅
1. **Use Alert Dialogs for critical decisions** - Deletion, irreversible actions
2. **Use Bottom Sheets for contextual actions** - From FAB, list items
3. **Use Full Screen Dialogs for complex forms** - Many fields, sections
4. **Always include a way to dismiss** - Cancel button, close icon, back
5. **Keep it simple** - One primary action, one secondary
6. **Use appropriate elevation** - Higher than content below
7. **Consider accessibility** - Focus trapping, keyboard navigation

### Don'ts ❌
1. **Don't use modals for non-critical info** - Use toasts or snackbar
2. **Don't stack modals** - One modal at a time
3. **Don't make modals too large** - Keep content focused
4. **Don't block without escape** - Always allow dismissal
5. **Don't use modals on small screens** - Use full screen instead

---

## 🔗 Modal Component Specifications

### Alert Dialog Props

```kotlin
@Composable
fun FloktaskAlertDialog(
    onDismiss: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(24.dp),
    colors: AlertDialogColors = FloktaskAlertDialogDefaults.colors(),
    properties: DialogProperties = DialogProperties()
) {
    // Implementation
}
```

---

### Bottom Sheet Props

```kotlin
@Composable
fun FloktaskBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // Implementation with ModalBottomSheetLayout
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

- **Component:** Modals & Dialogs
- **Library:** Floktask Design System
- **Frame:** Modal Variants

---

*Last updated: 2026-09-05*
