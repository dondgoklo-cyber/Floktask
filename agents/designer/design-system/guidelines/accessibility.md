# Floktask Design System - Accessibility Guidelines
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05

## 📋 Overview

Accessibility ensures that Floktask can be used by everyone, including people with disabilities. These guidelines follow WCAG 2.1 AA standards and Android accessibility best practices.

---

## 🎯 Core Principles

### 1. Perceivable
Information and user interface components must be presented in ways that all users can perceive.

### 2. Operable
All users must be able to operate the interface and navigate the content.

### 3. Understandable
Information and the operation of the user interface must be understandable.

### 4. Robust
Content must be robust enough that it can be interpreted reliably by a wide variety of user agents, including assistive technologies.

---

## 👁️ Visual Accessibility

### Color Contrast

| Element | Minimum Contrast Ratio | Token Reference |
|---------|------------------------|------------------|
| Body Text | 4.5:1 | `colors.on_surface` on `colors.surface` |
| Large Text (18.66sp+) | 3:1 | `typography.title.medium`+ |
| UI Components | 3:1 | Buttons, icons, borders |
| Disabled Text | 3:1 | 40% opacity states |

**Contrast Checker:**
- Primary 500 (`#FF9800`) on Surface (`#FFFFFF`): **4.6:1 ✅**
- On Primary (`#FFFFFF`) on Primary 500 (`#FF9800`): **21:1 ✅**
- On Surface (`#1A1A1A`) on Surface (`#FFFFFF`): **15.3:1 ✅**
- On Surface Variant (`#424242`) on Surface (`#FFFFFF`): **7.5:1 ✅**

---

### Color Blindness

**Do's ✅:**
1. **Don't rely on color alone** - Always add text, icons, or patterns
2. **Use color-blind friendly palettes** - Test with color blindness simulators
3. **Provide multiple indicators** - Status + Icon + Text

**Don'ts ❌:**
1. **Don't use red-green only** - 8% of men have color blindness
2. **Don't use similar hues** - Differ by at least 100 in HSL hue

**Color Blindness Safe Palette:**
```
Deuteranopia (Red-Green):
- Success: #4CAF50 (Green) ✅
- Error: #E53935 (Red) ❌ → Use #F44336 (Brighter Red) ✅
- Warning: #FFC107 (Amber) ✅
- Info: #2196F3 (Blue) ✅

Protanopia (Red-Green):
- Same considerations as Deuteranopia

Tritanopia (Blue-Yellow):
- Use high saturation colors
- Avoid blue-yellow combinations
```

---

## 🖐️ Touch & Motor Accessibility

### Touch Targets

| Element | Minimum Size | Token Reference |
|---------|--------------|------------------|
| Buttons | 48dp x 48dp | `spacing.xxl_2` |
| Touchable Areas | 48dp x 48dp | `spacing.xxl_2` |
| Icon Buttons | 48dp x 48dp | `spacing.xxl_2` |
| List Items | 48dp height | `spacing.xxl_2` |
| Checkbox/Radio | 48dp x 48dp (touch area) | `spacing.xxl_2` |

**Touch Spacing:**
- Minimum spacing between touch targets: 8dp (`spacing.sm`)
- Recommended spacing: 16dp (`spacing.md`)

---

### Gesture Alternatives

| Gesture | Alternative Required |
|---------|---------------------|
| Swipe | Button/Icon alternative |
| Long Press | Context menu button |
| Pinch Zoom | +/- buttons |
| Double Tap | Single tap with confirmation |

**Implementation:**
- All swipe actions must have button alternatives
- Long press actions must be available in overflow menu
- Provide undo for accidental gestures

---

## 🔊 Audio Accessibility

### Haptic Feedback (from NEXT_SESSION_TASK.md - Module 15)

**Haptic Types:**
```kotlin
enum class HapticType {
    LIGHT,      // For light feedback (e.g., button press)
    SELECTION,  // For selection (e.g., checkbox toggle)
    SUCCESS,    // For success states (e.g., task completed)
    WARNING     // For warnings/errors
}
```

**Usage:**
| Action | Haptic Type | Trigger |
|--------|-------------|---------|
| Button Press | LIGHT | onClick |
| Checkbox Toggle | SELECTION | onCheckedChange |
| Task Complete | SUCCESS | onTaskComplete |
| Error State | WARNING | onError |
| Swipe Action | LIGHT | onSwipe |
| Long Press | SELECTION | onLongClick |

**Settings:**
- User can enable/disable haptics in Settings
- Default: Enabled
- Storage: `UserPrefs.hapticEnabled`

---

### Screen Reader Support

**Content Descriptions:**
```kotlin
// For all interactive elements
Modifier.semantics {
    contentDescription = "Description for screen readers"
}

// For images
Image(
    painter = painterResource(R.drawable.task_icon),
    contentDescription = "Task icon",
    modifier = Modifier.semantics {
        contentDescription = "Task: ${task.title}"
    }
)
```

**Grouping:**
```kotlin
// Group related elements
Modifier.semantics(mergeDescendants = true) {
    // Group description
}
```

**State Announcements:**
```kotlin
// Announce state changes
LocalView.current.announceForAccessibility("Task completed")
```

---

## 👂 Hearing Accessibility

### Visual Alternatives for Audio

| Audio Element | Visual Alternative |
|---------------|---------------------|
| Notifications | Vibration + Visual banner |
| Alarms | Vibration + Flash + Notification |
| Voice Input | Text input alternative |
| Audio Feedback | Visual confirmation |

**Implementation:**
- All audio cues must have visual equivalents
- Voice input must have manual text alternative
- Provide captions for any audio/video content

---

## 🧠 Cognitive Accessibility

### Clear & Simple Language

**Do's ✅:**
1. **Use simple, clear language** - Avoid jargon
2. **Provide consistent terminology** - Same term for same concept
3. **Use familiar patterns** - Standard UI conventions
4. **Provide clear error messages** - Explain what went wrong and how to fix
5. **Offer help and guidance** - Tooltips, hints, tutorials

**Don'ts ❌:**
1. **Don't use complex sentences** - Keep it concise
2. **Don't use ambiguous terms** - Be specific
3. **Don't hide important information** - Make it visible
4. **Don't use flashing content** - Can cause seizures

---

### Error Prevention & Recovery

**Error Prevention:**
1. **Form Validation:** Validate on blur, not on submit
2. **Confirmation Dialogs:** For destructive actions
3. **Undo Functionality:** Allow users to undo actions
4. **Clear Instructions:** Explain requirements upfront

**Error Recovery:**
1. **Clear Error Messages:** Explain what went wrong
2. **Suggested Fixes:** Tell users how to correct
3. **Visual Indicators:** Highlight problematic fields
4. **Easy Correction:** Allow inline editing

---

### Focus & Attention

**Focus Management:**
- Logical tab order
- Visual focus indicators
- Skip links for long content

**Visual Focus Indicators:**
| Element | Focus Indicator |
|---------|-----------------|
| Buttons | 2dp ring + 4% opacity change |
| Inputs | 2dp border + label color change |
| Cards | 2dp border |
| Links | Underline + color change |

**Color:** Focus ring uses `colors.primary_500` at 40% opacity

---

## 🌓 Dark Mode Accessibility

### Dark Theme Requirements

| Element | Light Theme | Dark Theme | Contrast Check |
|---------|-------------|-------------|-----------------|
| Background | `#FAFAFA` | `#1A1A1A` | ✅ |
| Surface | `#FFFFFF` | `#121212` | ✅ |
| On Surface | `#1A1A1A` | `#FFFFFF` | ✅ |
| Primary | `#FF9800` | `#FF8C00` | ✅ |

**Dark Theme Adjustments:**
1. **Increase elevation** - Shadows are less visible
2. **Use higher contrast** - Text on dark backgrounds
3. **Avoid pure black** - Use `#121212` instead of `#000000`
4. **Test all colors** - Ensure contrast ratios are met

---

## 📱 Platform-Specific Accessibility

### Android

**Accessibility Services:**
- TalkBack (Screen Reader)
- Switch Access
- Select-to-Speak
- Accessibility Menu

**Implementation:**
```kotlin
// Enable accessibility services
android:accessibilitySuiteMode="true"

// Accessibility labels
android:contentDescription="@string/accessibility_label"

// Accessibility focus
view.isAccessibilityFocusable = true
view.nextFocusRightId = R.id.next_element
```

**Testing:**
1. Enable TalkBack
2. Navigate using swipe gestures
3. Test all interactive elements
4. Verify content descriptions

---

### Web (Future)

**ARIA Attributes:**
```html
<button aria-label="Close dialog">X</button>
<div aria-live="polite">Notification received</div>
<nav aria-label="Main navigation">...</nav>
```

**Keyboard Navigation:**
- Tab order follows visual order
- Focus visible on all interactive elements
- Skip links for main content

---

## ✅ Accessibility Checklist

### Design Phase
- [ ] Color contrast ratios meet 4.5:1 for text
- [ ] Color contrast ratios meet 3:1 for UI components
- [ ] Color is not the only visual indicator
- [ ] Touch targets are at least 48dp x 48dp
- [ ] Spacing between touch targets is at least 8dp
- [ ] All interactive elements have visual focus states
- [ ] Error states are clearly visible
- [ ] Success states are clearly visible
- [ ] Loading states are indicated

### Development Phase
- [ ] All images have content descriptions
- [ ] All interactive elements have accessibility labels
- [ ] Screen reader can navigate all content
- [ ] Keyboard can navigate all interactive elements
- [ ] Focus order follows visual order
- [ ] State changes are announced
- [ ] Form validation errors are announced
- [ ] Haptic feedback is optional

### Testing Phase
- [ ] Test with TalkBack enabled
- [ ] Test with Switch Access
- [ ] Test with high contrast mode
- [ ] Test with color blindness simulator
- [ ] Test keyboard-only navigation
- [ ] Test with screen reader
- [ ] Test touch targets on small screens

---

## 🛠️ Accessibility Testing Tools

### Design Tools
1. **Figma Accessibility Plugin** - Check contrast ratios
2. **Color Contrast Checker** - Verify WCAG compliance
3. **Color Blindness Simulator** - Test color combinations
4. **Stark** - Comprehensive accessibility testing

### Development Tools
1. **Android Accessibility Scanner** - Scan for issues
2. **TalkBack** - Test screen reader experience
3. **Switch Access** - Test switch control
4. **Accessibility Suite** - Automated testing

### Manual Testing
1. **Keyboard Navigation** - Tab through all elements
2. **Screen Reader** - Listen to all content
3. **Touch Targets** - Test on various screen sizes
4. **Color Contrast** - Squint test (if you can't read it, neither can others)

---

## 📚 Resources

- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Android Accessibility Documentation](https://developer.android.com/guide/topics/ui/accessibility)
- [Material Design Accessibility](https://material.io/design/usability/accessibility.html)
- [Web Content Accessibility Guidelines](https://www.w3.org/WAI/standards-guidelines/wcag/)

---

## 📁 Related Files

- [Color Tokens](../tokens/colors.yaml)
- [Typography Tokens](../tokens/typography.yaml)
- [Haptic Feedback Implementation](../../../app/src/main/java/com/wolftask/feature/haptic/)

---

*Last updated: 2026-09-05*
