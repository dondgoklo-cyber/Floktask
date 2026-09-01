# Floktask Design System - Responsive Design Guidelines
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05

## 📋 Overview

Responsive design ensures that Floktask provides an optimal experience across all device sizes and orientations. This document defines the responsive design system, breakpoints, and adaptation strategies.

---

## 📱 Device Categories & Breakpoints

### Breakpoint System

| Category | Breakpoint | Min Width | Max Width | Description |
|----------|------------|-----------|-----------|-------------|
| **Mobile (Phone)** | `mobile` | 0dp | 599dp | Small handheld devices |
| **Mobile (Large Phone)** | `mobile_large` | 600dp | 767dp | Larger phones, small tablets |
| **Tablet** | `tablet` | 768dp | 1023dp | Tablets in portrait/landscape |
| **Tablet (Large)** | `tablet_large` | 1024dp | 1439dp | Large tablets, small laptops |
| **Desktop** | `desktop` | 1440dp | ∞ | Laptops, desktops |

### Breakpoint Constants

```kotlin
// Breakpoints.kt
object FloktaskBreakpoints {
    const val MOBILE = 0
    const val MOBILE_LARGE = 600
    const val TABLET = 768
    const val TABLET_LARGE = 1024
    const val DESKTOP = 1440
}
```

---

## 🎨 Responsive Design Strategies

### 1. Layout Adaptation

#### Mobile (0dp - 599dp)
```
┌─────────────────────┐
│  App Bar             │
├─────────────────────┤
│  Content Area        │
│  Single column       │
│  Full width          │
│  Scrollable          │
├─────────────────────┤
│  Bottom Navigation   │
└─────────────────────┘
```

**Characteristics:**
- Single column layout
- Bottom navigation bar
- Full-width cards
- Stacked elements
- Minimal margins (16dp)

#### Mobile Large (600dp - 767dp)
```
┌─────────────────────┐
│  App Bar             │
├─────────────────────┤
│  Content Area        │
│  Single column       │
│  Max width: 600dp    │
│  Centered            │
├─────────────────────┤
│  Bottom Navigation   │
└─────────────────────┘
```

**Characteristics:**
- Single column layout
- Centered content (600dp max)
- Slightly larger margins (24dp)
- Bottom navigation bar

#### Tablet (768dp - 1023dp)
```
┌─────────────────────────────────────┐
│  App Bar                             │
├─────────────────────────────────────┤
│  Navigation Rail    Content Area      │
│  (256dp)           (Flexible)         │
│                    Max: 768dp          │
│                    Centered            │
└─────────────────────────────────────┘
```

**Characteristics:**
- Navigation rail on left
- Two-column layout possible
- Larger margins (24dp-32dp)
- Max content width: 768dp

#### Tablet Large (1024dp - 1439dp)
```
┌─────────────────────────────────────────────────┐
│  App Bar                                         │
├─────────────────────────────────────────────────┤
│  Navigation Rail    Content Area                   │
│  (256dp-288dp)   (Flexible)                        │
│                  Max: 1024dp                       │
│                  Centered                         │
└─────────────────────────────────────────────────┘
```

**Characteristics:**
- Navigation rail on left
- Multi-column layouts
- Larger margins (32dp)
- Max content width: 1024dp

#### Desktop (1440dp+)
```
┌─────────────────────────────────────────────────────────┐
│  App Bar                                                   │
├─────────────────────────────────────────────────────────┤
│  Navigation Rail    Content Area                             │
│  (256dp-288dp)   (Flexible)                                  │
│                  Max: 1200dp-1440dp                         │
│                  Centered                                   │
└─────────────────────────────────────────────────────────┘
```

**Characteristics:**
- Navigation rail on left
- Multi-column layouts
- Large margins (32dp-48dp)
- Max content width: 1200dp-1440dp

---

## 📐 Responsive Spacing

### Margin Scale by Breakpoint

| Breakpoint | Horizontal Margin | Vertical Margin | Max Width |
|------------|-------------------|-----------------|-----------|
| Mobile | 16dp | 24dp | 100% |
| Mobile Large | 24dp | 32dp | 600dp |
| Tablet | 24dp | 32dp | 768dp |
| Tablet Large | 32dp | 40dp | 1024dp |
| Desktop | 32dp-48dp | 40dp-48dp | 1200dp-1440dp |

### Padding Scale by Breakpoint

| Element | Mobile | Tablet | Desktop |
|---------|--------|--------|---------|
| Page | 16dp | 24dp | 32dp |
| Card | 16dp | 20dp | 24dp |
| Button | 24h x 12v | 32h x 16v | 32h x 16v |
| Input | 16h x 16v | 20h x 20v | 24h x 20v |

---

## 🎯 Typography Responsive Scale

### Font Size Scale by Breakpoint

| Typography | Mobile | Tablet | Desktop | Scale Factor |
|------------|--------|--------|---------|---------------|
| Display Large | 45sp | 52sp | 57sp | +12.5% → +25% |
| Display Medium | 36sp | 42sp | 45sp | +12.5% → +25% |
| Display Small | 30sp | 34sp | 36sp | +12.5% → +20% |
| Headline Large | 28sp | 32sp | 34sp | +12.5% → +20% |
| Headline Medium | 24sp | 28sp | 30sp | +12.5% → +25% |
| Headline Small | 20sp | 24sp | 26sp | +20% → +30% |
| Title Large | 18sp | 20sp | 22sp | +12.5% → +25% |
| Title Medium | 16sp | 18sp | 20sp | +12.5% → +25% |
| Title Small | 14sp | 16sp | 16sp | +12.5% → +15% |
| Body Large | 16sp | 18sp | 18sp | +12.5% → +15% |
| Body Medium | 14sp | 14sp | 16sp | 0% → +15% |
| Body Small | 12sp | 12sp | 14sp | 0% → +15% |

**Implementation:**
```kotlin
// ResponsiveTypography.kt
fun responsiveTextStyle(
    baseStyle: TextStyle,
    breakpoint: Int
): TextStyle {
    val scale = when {
        breakpoint >= FloktaskBreakpoints.DESKTOP -> 1.25f
        breakpoint >= FloktaskBreakpoints.TABLET_LARGE -> 1.125f
        breakpoint >= FloktaskBreakpoints.TABLET -> 1.125f
        else -> 1.0f
    }
    return baseStyle.copy(
        fontSize = baseStyle.fontSize * scale
    )
}
```

---

## 🏗️ Navigation Patterns

### Bottom Navigation (Mobile)
```
┌─────────────────────┐
│  Content Area        │
├─────────────────────┤
│  [Today] [Tasks]     │
│  [Finance] [Notes]   │
│  [Habits] [More]     │
└─────────────────────┘
```

**Breakpoints:** Mobile, Mobile Large
**Behavior:**
- 5 items maximum
- Scrollable if more items
- Active item highlighted

### Navigation Rail (Tablet/Desktop)
```
┌─────────────────────────────────────┐
│  [Today]                           │
│  [Tasks]                           │
│  [Finance]                         │
│  [Notes]                           │
│  [Habits]                          │
│  [Settings]                         │
│                                     │
│  Content Area                       │
└─────────────────────────────────────┘
```

**Breakpoints:** Tablet, Tablet Large, Desktop
**Width:** 256dp - 288dp
**Behavior:**
- Full list of navigation items
- Icons + text labels
- Active item highlighted
- Can be collapsed

### Navigation Drawer (Optional)
```
┌─────────────────────────────────────┐
│  ☰ Menu                            │
│                                     │
│  Content Area                       │
└─────────────────────────────────────┘

When opened:
┌─────────────┬─────────────────────┐
│  Navigation  │                     │
│  Items       │                     │
│              │                     │
│  [Today]     │                     │
│  [Tasks]     │  Content Area        │
│  [Finance]   │                     │
│  ...        │                     │
└─────────────┴─────────────────────┘
```

**Breakpoints:** All (optional)
**Behavior:**
- Overlay on mobile
- Permanent on tablet/desktop
- Can be collapsed

---

## 🎨 Component Adaptation

### Buttons

| Property | Mobile | Tablet | Desktop |
|----------|--------|--------|---------|
| Min Width | 64dp | 80dp | 80dp |
| Height | 40dp | 48dp | 48dp |
| Padding | 24h x 12v | 32h x 16v | 32h x 16v |
| Icon Size | 24dp | 24dp | 24dp |

### Cards

| Property | Mobile | Tablet | Desktop |
|----------|--------|--------|---------|
| Max Width | 100% | 360dp | 400dp |
| Padding | 16dp | 20dp | 24dp |
| Elevation | Level 2 | Level 2 | Level 3 |
| Border Radius | 12dp | 12dp | 16dp |

### Input Fields

| Property | Mobile | Tablet | Desktop |
|----------|--------|--------|---------|
| Height | 48dp | 56dp | 56dp |
| Padding | 16h x 16v | 20h x 20v | 24h x 20v |
| Border Radius | 12dp | 12dp | 12dp |

### Lists

| Property | Mobile | Tablet | Desktop |
|----------|--------|--------|---------|
| Item Height | 48dp | 56dp | 56dp |
| Padding | 16h x 12v | 20h x 16v | 24h x 16v |
| Columns | 1 | 1-2 | 1-3 |

### Modals

| Property | Mobile | Tablet | Desktop |
|----------|--------|--------|---------|
| Max Width | 100% - 32dp | 560dp | 600dp |
| Max Height | 100% - 64dp | 80% | 80% |
| Border Radius | 24dp | 24dp | 24dp |
| Elevation | Level 5 | Level 5 | Level 5 |

### Bottom Sheets

| Property | Mobile | Tablet | Desktop |
|----------|--------|--------|---------|
| Max Height | 80% | 80% | 70% |
| Border Radius | 24dp (top) | 24dp (top) | 24dp (top) |
| Elevation | Level 4 | Level 4 | Level 4 |

---

## 📱 Orientation Handling

### Portrait (Default)
```
┌─────────────┐
│  App Bar    │
├─────────────┤
│             │
│  Content    │
│             │
├─────────────┤
│  Bottom Nav │
└─────────────┘
```

**Behavior:**
- Standard mobile layout
- Bottom navigation visible
- Full-height content

### Landscape
```
┌─────────────────────────────┐
│  App Bar    Content Area     │
├─────────────────────────────┤
│  Bottom Navigation (if room)│
└─────────────────────────────┘

Or:

┌─────────────────────────────┐
│  App Bar                    │
├─────────────────────────────┤
│  Navigation Rail  Content    │
└─────────────────────────────┘
```

**Behavior:**
- If width > height: Use landscape layout
- If width > 768dp: Use navigation rail
- If width < 768dp: Use bottom navigation or compact rail
- Content area adjusts to available space

**Implementation:**
```kotlin
// Check orientation
val configuration = LocalConfiguration.current
val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

// Responsive layout
if (isLandscape && windowWidth > FloktaskBreakpoints.TABLET) {
    NavigationRailLayout()
} else if (windowWidth >= FloktaskBreakpoints.TABLET) {
    NavigationRailLayout()
} else {
    BottomNavigationLayout()
}
```

---

## 🎯 Responsive Design Best Practices

### Do's ✅

1. **Design mobile-first** - Start with smallest screen, scale up
2. **Use relative units** - dp/sp for Android, rem/em for Web
3. **Test on real devices** - Various sizes and orientations
4. **Maintain touch targets** - 48dp minimum on all screens
5. **Use flexible layouts** - ConstraintLayout, Flexbox
6. **Optimize images** - Different sizes for different screens
7. **Consider performance** - Larger screens = more content = more rendering
8. **Test breakpoints** - Ensure smooth transitions between breakpoints

### Don'ts ❌

1. **Don't use fixed widths** - Use max-width and percentages
2. **Don't hide content** - Reflow, don't hide
3. **Don't assume orientation** - Handle both portrait and landscape
4. **Don't ignore tablets** - Test on larger screens
5. **Don't use tiny text** - Scale typography appropriately
6. **Don't break accessibility** - Maintain contrast and touch targets
7. **Don't overcomplicate** - Keep layouts simple and maintainable

---

## 🔧 Implementation Guidelines

### Compose Implementation

```kotlin
// ResponsiveLayout.kt
@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val windowWidth = LocalWindowSize.current.width
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = when {
                    windowWidth >= FloktaskBreakpoints.DESKTOP -> 48.dp
                    windowWidth >= FloktaskBreakpoints.TABLET_LARGE -> 32.dp
                    windowWidth >= FloktaskBreakpoints.TABLET -> 24.dp
                    else -> 16.dp
                },
                vertical = when {
                    windowWidth >= FloktaskBreakpoints.DESKTOP -> 48.dp
                    windowWidth >= FloktaskBreakpoints.TABLET_LARGE -> 40.dp
                    else -> 24.dp
                }
            )
    ) {
        content()
    }
}
```

### Navigation Implementation

```kotlin
// Navigation.kt
@Composable
fun FloktaskNavigation() {
    val windowWidth = LocalWindowSize.current.width
    val isLandscape = LocalConfiguration.current.orientation == 
                      Configuration.ORIENTATION_LANDSCAPE
    
    if (windowWidth >= FloktaskBreakpoints.TABLET || isLandscape) {
        // Navigation Rail for tablet/landscape
        NavigationRailLayout()
    } else {
        // Bottom Navigation for mobile
        BottomNavigationLayout()
    }
}
```

### Responsive Typography Implementation

```kotlin
// Typography.kt
@Composable
fun ResponsiveText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier,
    breakpoint: Int = LocalWindowSize.current.width
) {
    val responsiveStyle = when {
        breakpoint >= FloktaskBreakpoints.DESKTOP -> style.copy(
            fontSize = style.fontSize * 1.25f
        )
        breakpoint >= FloktaskBreakpoints.TABLET_LARGE -> style.copy(
            fontSize = style.fontSize * 1.125f
        )
        breakpoint >= FloktaskBreakpoints.TABLET -> style.copy(
            fontSize = style.fontSize * 1.125f
        )
        else -> style
    }
    
    Text(
        text = text,
        style = responsiveStyle,
        modifier = modifier
    )
}
```

---

## 📊 Responsive Testing

### Device Testing Matrix

| Device | Width (dp) | Height (dp) | PPI | Orientation | Test Coverage |
|--------|------------|--------------|-----|------------|---------------|
| Pixel 5 | 393 | 851 | 443 | Portrait | ✅ |
| Pixel 5 | 851 | 393 | 443 | Landscape | ✅ |
| Pixel 7 | 412 | 892 | 443 | Portrait | ✅ |
| Pixel 7 | 892 | 412 | 443 | Landscape | ✅ |
| Pixel Tablet | 1180 | 840 | 270 | Portrait | ✅ |
| Pixel Tablet | 840 | 1180 | 270 | Landscape | ✅ |
| Nexus 7 | 600 | 960 | 242 | Portrait | ✅ |
| Nexus 7 | 960 | 600 | 242 | Landscape | ✅ |
| iPad Air | 820 | 1180 | 264 | Portrait | ✅ |
| iPad Air | 1180 | 820 | 264 | Landscape | ✅ |

### Testing Checklist

- [ ] Test on all breakpoint ranges
- [ ] Test portrait and landscape orientations
- [ ] Test with different font scales
- [ ] Test with different display sizes
- [ ] Test touch targets on all screens
- [ ] Test navigation between breakpoints
- [ ] Test modal/dialog sizing
- [ ] Test image scaling
- [ ] Test performance on large screens
- [ ] Test accessibility on all screens

---

## 📁 Related Files

- [Spacing Tokens](../tokens/spacing.yaml)
- [Typography Tokens](../tokens/typography.yaml)
- [Accessibility Guidelines](./accessibility.md)

---

## 🎨 Figma Reference

- **Library:** Floktask Design System
- **Page:** Responsive Design
- **Frames:** Breakpoint Layouts

---

*Last updated: 2026-09-05*
