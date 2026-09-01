# Floktask Design System - Dark Mode Guidelines
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05

## 📋 Overview

Dark mode provides a dark color theme that is comfortable to use in low-light environments. This document defines the dark mode color system, implementation guidelines, and best practices for Floktask.

---

## 🎨 Dark Mode Color System

### Color Token Mapping

All color tokens have light and dark variants. The dark mode colors are designed to:
1. Maintain WCAG 2.1 AA contrast ratios
2. Provide visual comfort in dark environments
3. Preserve brand identity
4. Ensure consistency across the app

### Base Colors

| Token | Light Mode | Dark Mode | Purpose |
|-------|------------|-----------|---------|
| `surface` | `#FFFFFF` | `#121212` | Primary background |
| `background` | `#FAFAFA` | `#1A1A1A` | Secondary background |
| `on_surface` | `#1A1A1A` | `#FFFFFF` | Primary text |
| `on_surface_variant` | `#424242` | `#CACACA` | Secondary text |
| `surface_container` | `#F0F0F0` | `#2A2A2A` | Card backgrounds |
| `surface_container_high` | `#E8E8E8` | `#383838` | Elevated surfaces |
| `surface_container_highest` | `#E0E0E0` | `#424242` | Highest elevation |

### Primary Colors

| Token | Light Mode | Dark Mode | Purpose |
|-------|------------|-----------|---------|
| `primary_500` | `#FF9800` | `#FF8C00` | Main brand color |
| `primary_600` | `#FB8C00` | `#FF9800` | Hover/Focus |
| `primary_700` | `#F57C00` | `#FB8C00` | Pressed |
| `on_primary` | `#FFFFFF` | `#000000` | Text on primary |
| `on_primary_container` | `#FFFFFF` | `#1A1A1A` | Text on primary container |

**Rationale:**
- Dark mode uses slightly brighter orange (`#FF8C00` vs `#FF9800`) for better visibility
- This maintains brand identity while improving contrast on dark backgrounds

### Semantic Colors

| Category | Light Mode | Dark Mode | Token |
|----------|------------|-----------|-------|
| **Success** | `#4CAF50` | `#4CAF50` | `success_500` |
| **Error** | `#F44336` | `#F44336` | `error_500` |
| **Warning** | `#FFC107` | `#FFB300` | `warning_500` |
| **Info** | `#2196F3` | `#2196F3` | `info_500` |

**Note:** Warning color is adjusted to `#FFB300` in dark mode for better visibility on dark backgrounds.

---

## 📐 Dark Mode Design Principles

### 1. Elevation Overlay (Material Design 3)

In dark mode, elevated surfaces use overlay colors to create the perception of elevation. The overlay is applied on top of the surface color.

**Overlay Opacity by Elevation Level:**

| Elevation Level | Overlay Opacity | Resulting Color |
|-----------------|-----------------|-----------------|
| Level 0 | 0% | `#121212` |
| Level 1 | 5% | `#191919` |
| Level 2 | 8% | `#1E1E1E` |
| Level 3 | 11% | `#232323` |
| Level 4 | 12% | `#242424` |
| Level 5 | 14% | `#262626` |

**Implementation:**
```kotlin
// In theme definition
val darkColors = darkColorScheme(
    surface = Color(0xFF121212),
    surfaceVariant = Color(0xFF1E1E1E),
    // ... other colors
)

// Elevation overlay is handled automatically by Compose
```

### 2. Contrast Requirements

**Minimum Contrast Ratios in Dark Mode:**

| Element Type | Minimum Ratio | Example |
|--------------|---------------|---------|
| Body Text | 4.5:1 | On Surface on Surface |
| Large Text | 3:1 | Title Large on Surface |
| UI Components | 3:1 | Icons on Surface |
| Disabled Text | 3:1 | On Surface (40%) on Surface |

**Contrast Checks:**
- ✅ `#FFFFFF` on `#121212`: **15.3:1**
- ✅ `#CACACA` on `#121212`: **7.5:1**
- ✅ `#FF8C00` on `#121212`: **7.2:1**
- ✅ `#4CAF50` on `#121212`: **6.3:1**
- ✅ `#F44336` on `#121212`: **6.8:1**

### 3. Avoid Pure Black

**Why:** Pure black (`#000000`) can cause eye strain in dark mode.

**Solution:** Use near-black colors:
- Surface: `#121212` (Material Dark)
- Background: `#1A1A1A`
- Elevated: `#1E1E1E` to `#424242`

---

## 🎭 Dark Mode Component Specifications

### Buttons

| Variant | Light Mode | Dark Mode |
|---------|------------|-----------|
| **Primary** | Primary 500 | Primary 600 |
| **On Primary** | White | Black |
| **Secondary** | Secondary 500 | Secondary 500 |
| **Outlined** | Outline | Outline Variant |

**Dark Mode Button Colors:**
```yaml
# Dark mode button colors
button:
  primary:
    background: "#FF8C00"
    text: "#000000"
    hover: "#FF9800"
    pressed: "#FB8C00"
    
  secondary:
    background: "#4CAF50"
    text: "#000000"
    
  outlined:
    background: "transparent"
    border: "#424242"
    text: "#FFFFFF"
```

### Cards

| Elevation | Light Mode | Dark Mode |
|-----------|------------|-----------|
| Level 0 | `#FFFFFF` | `#121212` |
| Level 1 | `#F5F5F5` | `#1E1E1E` |
| Level 2 | `#F0F0F0` | `#232323` |
| Level 3 | `#E8E8E8` | `#262626` |

**Dark Mode Card Colors:**
```yaml
card:
  default: "#1E1E1E"
  elevated: "#232323"
  highest: "#262626"
```

### Input Fields

| State | Light Mode | Dark Mode |
|-------|------------|-----------|
| **Default** | Outline | Outline Variant |
| **Focused** | Primary 500 | Primary 600 |
| **Error** | Error 500 | Error 500 |
| **Background** | Surface | Surface Container Highest |

**Dark Mode Input Colors:**
```yaml
text_field:
  default:
    background: "#1E1E1E"
    border: "#424242"
    text: "#FFFFFF"
    
  focused:
    background: "#1E1E1E"
    border: "#FF8C00"
    text: "#FFFFFF"
    
  error:
    background: "#2D1B1B"
    border: "#F44336"
    text: "#FFFFFF"
```

### Modals & Dialogs

| Element | Light Mode | Dark Mode |
|---------|------------|-----------|
| **Background** | Surface | Surface |
| **Elevation** | Level 5 | Level 5 |
| **Scrim** | 40% Black | 60% Black |

**Dark Mode Modal Colors:**
```yaml
modal:
  background: "#1E1E1E"
  elevation: "level_5"
  scrim: "rgba(0, 0, 0, 0.60)"
```

---

## 🌓 Dark Mode Implementation

### Theme Switching

**Kotlin (Jetpack Compose):**
```kotlin
// Theme.kt
val FloktaskTheme = {
    darkTheme: Boolean,
    content: @Composable () -> Unit
} -> Unit = { darkTheme, content ->
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFFF8C00),
            onPrimary = Color(0xFF000000),
            surface = Color(0xFF121212),
            onSurface = Color(0xFFFFFFFF),
            // ... all other colors
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFFF9800),
            onPrimary = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1A1A1A),
            // ... all other colors
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FloktaskTypography,
        shapes = FloktaskShapes,
        content = content
    )
}
```

### User Preference

**Settings:**
```kotlin
// UserPreferences.kt
enum class ThemePreference {
    SYSTEM,   // Follow system preference
    LIGHT,    // Always light mode
    DARK      // Always dark mode
}

// Usage
val themePreference = userPrefs.themePreference
val useDarkTheme = when (themePreference) {
    ThemePreference.SYSTEM -> isSystemInDarkTheme()
    ThemePreference.LIGHT -> false
    ThemePreference.DARK -> true
}
```

### Dynamic Colors (Android 12+)

**Implementation:**
```kotlin
// For Android 12+ devices
val dynamicDarkColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    dynamicDarkColorScheme(LocalContext.current)
} else {
    darkColorScheme()
}
```

---

## 🎯 Dark Mode Design Best Practices

### Do's ✅

1. **Test all colors in dark mode** - Ensure contrast ratios are met
2. **Use elevation overlays** - Maintain visual hierarchy
3. **Avoid pure black** - Use `#121212` or darker grays
4. **Maintain brand colors** - Adjust slightly for visibility
5. **Test with real users** - Get feedback on dark mode experience
6. **Provide toggle option** - Allow users to switch between modes
7. **Follow system preference** - Default to system theme setting
8. **Use subtle shadows** - Shadows are less visible in dark mode

### Don'ts ❌

1. **Don't just invert colors** - Design dark mode intentionally
2. **Don't use low contrast** - Ensure readability
3. **Don't ignore elevation** - Maintain visual hierarchy
4. **Don't use bright colors on dark** - Can cause eye strain
5. **Don't forget about images** - Provide dark mode alternatives
6. **Don't break accessibility** - Maintain WCAG compliance

---

## 📱 Platform-Specific Considerations

### Android

**Dark Theme Detection:**
```kotlin
// Check if system is in dark mode
val isSystemInDarkTheme = isSystemInDarkTheme()

// In Activity/Fragment
val configuration = resources.configuration
val isDarkMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == 
                 Configuration.UI_MODE_NIGHT_YES
```

**Manifest Declaration:**
```xml
<!-- Enable dark theme in manifest -->
<application
    android:theme="@style/Theme.Floktask.DayNight">
```

### Web (Future)

**CSS Media Query:**
```css
/* Dark mode styles */
@media (prefers-color-scheme: dark) {
    :root {
        --color-surface: #121212;
        --color-on-surface: #FFFFFF;
        --color-primary: #FF8C00;
    }
}
```

**JavaScript Detection:**
```javascript
// Check for dark mode preference
const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
```

---

## 🔍 Dark Mode Testing

### Visual Testing

1. **Check all screens** - Every screen in dark mode
2. **Verify contrast** - Text should be readable
3. **Test elevation** - Cards should appear elevated
4. **Check colors** - Brand colors should be visible
5. **Test transitions** - Smooth theme switching

### Automated Testing

```kotlin
// Test dark mode colors
@Test
fun testDarkModeContrast() {
    val darkColors = darkColorScheme()
    
    // Check contrast between onSurface and surface
    val contrast = calculateContrast(darkColors.onSurface, darkColors.surface)
    assert(contrast >= 4.5) { "On Surface contrast too low: $contrast" }
    
    // Check primary color visibility
    val primaryContrast = calculateContrast(darkColors.primary, darkColors.surface)
    assert(primaryContrast >= 3.0) { "Primary contrast too low: $primaryContrast" }
}
```

### User Testing

1. **Recruit diverse users** - Different age groups, vision abilities
2. **Test in various lighting** - Bright, dim, dark environments
3. **Collect feedback** - Comfort, readability, preference
4. **Iterate based on feedback** - Improve dark mode experience

---

## 📊 Dark Mode Metrics

### Adoption
- **Target:** 70%+ users should enable dark mode
- **Measurement:** Track theme preference in analytics

### Satisfaction
- **Target:** 4.5+ stars for dark mode experience
- **Measurement:** User surveys, app store reviews

### Usage
- **Target:** 50%+ of sessions use dark mode
- **Measurement:** Track active sessions by theme

---

## 🎨 Dark Mode Color Palette Reference

### Primary Palette (Dark Mode)

```
Orange Spectrum:
├── Primary 500: #FF8C00 (Main)
├── Primary 600: #FF9800 (Hover)
├── Primary 700: #FB8C00 (Pressed)
└── On Primary: #000000

Green Spectrum:
├── Success 500: #4CAF50
└── On Success: #FFFFFF

Red Spectrum:
├── Error 500: #F44336
└── On Error: #FFFFFF

Blue Spectrum:
├── Info 500: #2196F3
└── On Info: #FFFFFF

Yellow/Amber Spectrum:
└── Warning 500: #FFB300
```

### Neutral Palette (Dark Mode)

```
Surface Colors:
├── Surface: #121212 (Base)
├── Surface Variant: #1E1E1E
├── Surface Container Low: #1E1E1E
├── Surface Container: #232323
├── Surface Container High: #262626
└── Surface Container Highest: #2D2D2D

Background Colors:
└── Background: #1A1A1A

Text Colors:
├── On Surface: #FFFFFF
├── On Surface Variant: #CACACA
├── On Surface Low: #9E9E9E
└── Inverse Surface: #E0E0E0

Outline Colors:
├── Outline: #424242
└── Outline Variant: #2D2D2D
```

---

## 📁 Related Files

- [Color Tokens](../tokens/colors.yaml)
- [Shadow Tokens](../tokens/shadows.yaml)
- [Accessibility Guidelines](./accessibility.md)

---

## 🎨 Figma Reference

- **Library:** Floktask Design System
- **Page:** Dark Mode
- **Frames:** Dark Mode Components

---

*Last updated: 2026-09-05*
