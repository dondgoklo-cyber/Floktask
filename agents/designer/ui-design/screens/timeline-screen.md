# Timeline Screen - UI Design Documentation
# **Floktask - Personal Life OS**
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Task: DE-003

---

## 📋 Overview

This document describes the **Timeline Screen** UI design for Floktask, including all visual elements, components, states, and interactions.

---

## 🎨 Screen Layout

### Structure (Hierarchy)

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE SCREEN                                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  HEADER BAR                                                    ││
│  │  ┌─────────────────────────────────────────────────────────┐││
│  │  │  [←]  Timeline          [+]    [Today] [3D] [Week] [Month]  │││
│  │  └─────────────────────────────────────────────────────────┘││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  MAIN CONTENT                                                 ││
│  │  ┌─────────┬───────────────────────────────────────────────┐││
│  │  │         │                                               │││
│  │  │  TIME   │                                               │││
│  │  │  AXIS   │              CONTENT AREA                      │││
│  │  │         │                                               │││
│  │  │ 00:00  ├───────────────────────────────────────────────┤││
│  │  │ 01:00  │                                               │││
│  │  │ ...    │  [TASK BLOCKS]                                 │││
│  │  │ 23:00  │                                               │││
│  │  │         │  ┌─────────────┐                              │││
│  │  │         │  │ Task Title  │                              │││
│  │  │         │  │ 10:00-12:00 │ ← Task Block                  │││
│  │  │         │  └─────────────┘                              │││
│  │  │         │       ↓ (Current Time Line - Red)            │││
│  │  └─────────┴───────────────────────────────────────────────┘││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  BOTTOM BAR (Optional)                                       ││
│  │  [+ Add Task]  [Pomodoro: 25:00]  [Settings]                 ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Components

### 1. Header Bar

**Purpose:** Navigation and view controls

```
┌─────────────────────────────────────────────────────────────────┐
│  [← Back]  Timeline  [+ Add]  [Today] [3-Day] [Week] [Month] [Pomodoro] │
└─────────────────────────────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 56dp | `spacing.xxxl` |
| Background | Surface | `colors.surface` |
| Elevation | Level 2 | `shadows.level_2` |
| Padding | 16dp horizontal | `spacing.md` |

**Elements:**
- **Back Button:** IconButton (24dp x 24dp)
- **Title:** "Timeline" (Title Large, 22sp Bold)
- **Add Button:** IconButton (24dp x 24dp) → Opens AddTaskSheet
- **View Toggles:** SegmentedButton with 5 options
- **Pomodoro Toggle:** IconButton (optional, for quick access)

---

### 2. Time Axis

**Purpose:** Show time scale

```
┌─────────┐
│  00:00  │
│  01:00  │
│  02:00  │
│  ...    │
│  23:00  │
└─────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Width | 56dp | Custom |
| Background | Surface Variant | `colors.surface_variant` |
| Label Color | On Surface Variant | `colors.on_surface_variant` |
| Label Font | 11sp Medium | `typography.timeline.time` |
| Label Padding | 4dp vertical | `spacing.xs` |
| Height per Hour (Daily) | 60dp | Custom |
| Height per Hour (3-Day) | 30dp | Custom |
| Height per Hour (Weekly) | 15dp | Custom |

**Hour Labels:**
- Format: 24-hour (00:00 - 23:00)
- Alternative: 12-hour (12:00 AM - 11:00 PM) based on user preference
- Alignment: Right-aligned

---

### 3. Content Area

**Purpose:** Main timeline content

```
┌─────────────────────────────────────────────────────────────────┐
│  Background: Today (Premium Subtle), Future (Surface), Past (Surface Variant) │
│  Grid Lines: Horizontal lines at each hour (Outline Variant, 1dp) │
│  Current Time: Red line (Error 500, 2dp) + "СЕЙЧАС" label         │
└─────────────────────────────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Background (Today) | Premium Subtle | `premium.subtle_background` |
| Background (Future) | Surface | `colors.surface` |
| Background (Past) | Surface Variant | `colors.surface_variant` |
| Grid Line Color | Outline Variant | `colors.outline_variant` |
| Grid Line Height | 1dp | Custom |

---

### 4. Task Block

**Purpose:** Visual representation of a task in timeline

```
┌─────────────────────────┐
│ Task Title              │ ← 14sp Semi-Bold
│                         │
│ 10:00 - 12:00           │ ← 12sp Regular
│                         │
│ [Priority Badge]        │ ← Optional
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Min Width | 100dp | Custom |
| Min Height | 40dp | Custom |
| Padding | 8dp horizontal, 4dp vertical | `spacing.sm`, `spacing.xs` |
| Margin | 4dp | `spacing.xs` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Elevation (Default) | Level 1 | `shadows.level_1` |
| Elevation (Hover) | Level 2 | `shadows.level_2` |
| Elevation (Selected) | Level 3 | `shadows.level_3` |

**Content:**
- **Title:** Task title (14sp Semi-Bold)
- **Time:** Start and end time (12sp Regular)
- **Priority Badge:** Optional, for high/critical priority
- **Category Indicator:** Color bar on left (4dp width)

**Colors by Priority:**
- Low: Gray 200
- Medium: Amber 200
- High: Orange 200
- Critical: Red 200

**Colors by Category:**
- Work: Green 500
- Personal: Orange 500 (Brand)
- Study: Blue 500
- Shopping: Purple 500
- Health: Red 500
- Finance: Amber 500
- Other: Gray 500

---

### 5. All-Day Task Block

**Purpose:** Tasks that span the entire day

```
┌─────────────────────────────────────────────────────────────────┐
│ All-Day Task Title                                          [Menu] │
└─────────────────────────────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Width | 100% - Time Axis Width | Custom |
| Padding | 8dp horizontal | `spacing.sm` |
| Border Radius | 4dp | `border_radius.xxs` |
| Background (Light) | Finance Income (12%) | `#4CAF50` (12% opacity) |
| Background (Dark) | Finance Income (24%) | `#4CAF50` (24% opacity) |
| Typography | 14sp Medium | `typography.body.medium` |

**Placement:** At the top of the timeline, above all other tasks

---

### 6. Time Block

**Purpose:** Fixed time periods for specific activities (Time Blocking feature)

```
┌─────────────────────────────────────────────────────────────────┐
│  🏢 Deep Work Block                                        [Menu] │
│  Fixed: 09:00 - 12:00                                    Recurring: Mon-Fri │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  [Task 1]                                           │   │
│  │  [Task 2]                                           │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Min Height | 64dp | Custom |
| Padding | 12dp | `spacing.md` |
| Border Radius | 12dp | `border_radius.md` |
| Background | Category Color (20%) | Custom |
| Border | Category Color (1dp) | Custom |
| Elevation | Level 2 | `shadows.level_2` |

**Header:**
- Icon: Block type icon (🏢 for Deep Work, 🍽️ for Lunch, etc.)
- Title: Block name
- Time: Fixed time range
- Recurring: Days of week (Mon-Fri, etc.)
- Menu: Context menu button

**Content:** Tasks can be placed inside time blocks

---

### 7. Current Time Indicator

**Purpose:** Show current time in timeline

```
Current Time Line: │ (2dp, Error 500)
                  ▼
                  СЕЙЧАС (10sp, Error 500)
```

| Property | Value | Token |
|----------|-------|-------|
| Line Height | 2dp | Custom |
| Line Color | Error 500 | `colors.error_500` |
| Label | "СЕЙЧАС" | - |
| Label Font | 10sp Medium | Custom |
| Label Color | Error 500 | `colors.error_500` |
| Animation | Smooth transition (1000ms linear) | Custom |

**Behavior:**
- Updates every minute
- Smooth animation to new position
- Label appears when line is in visible area
- Label disappears when line is at edge

---

### 8. Overlap Indicator

**Purpose:** Show when multiple tasks overlap

```
┌─────────────────────────┐
│ +3 more tasks           │
│ Click to expand          │
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Min Width | 100dp | Custom |
| Height | 32dp | Custom |
| Padding | 8dp | `spacing.sm` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Background | Surface Container High | `colors.surface_container_high` |
| Typography | 12sp Medium | `typography.label.medium` |
| Icon | + (Add) | - |

**Behavior:**
- Shows count of overlapping tasks
- Click/tap to expand and show all tasks
- Hover to show tooltip with task list

---

### 9. Add Task Quick Action

**Purpose:** Quick task creation from timeline

```
┌─────────────────────────┐
│    + Add Task           │
│    at 14:30             │
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Width | 160dp | Custom |
| Height | 40dp | `spacing.xxl_2` |
| Padding | 12dp | `spacing.sm_2` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Background | Primary 500 | `colors.primary_500` |
| Typography | 14sp Medium | `typography.button.medium` |
| Icon | + (Add) | - |

**Behavior:**
- Appears when clicking on empty space in timeline
- Pre-fills start time based on click position
- Opens AddTaskSheet for full task creation

---

### 10. Pomodoro Integration

**Purpose:** Pomodoro timer integrated into timeline

```
┌─────────────────────────────────────────────────────────────────┐
│  Design UI                                        [Start] [Pause] │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ⏱️ 25:00        Pomodoro Session #1                     │   │
│  └─────────────────────────────────────────────────────────┘   │
│  10:00 - 12:00                                                  │
└─────────────────────────────────────────────────────────────────┘
```

**Pomodoro Controls:**
- **Start:** Begins Pomodoro session (25 min work)
- **Pause:** Pauses current session
- **Stop:** Ends session early
- **Resume:** Continues paused session

**Pomodoro States:**
- **Work:** Timer counting down (25:00 → 00:00)
- **Short Break:** 5 min break timer
- **Long Break:** 15 min break timer
- **Paused:** Timer paused
- **Completed:** Session completed, ready for next

**Visual:**
- Timer display: Large digits (24sp Bold)
- Progress bar: Shows time remaining
- Session indicator: "Pomodoro Session #1"
- Color: Work = Green 500, Break = Blue 500

---

### 11. Context Menu

**Purpose:** Actions on long-press/right-click

```
┌─────────────────────────┐
│  📝 View Details        │
├─────────────────────────┤
│  ✏️ Edit               │
├─────────────────────────┤
│  🗑️ Delete             │
├─────────────────────────┤
│  📅 Reschedule          │
├─────────────────────────┤
│  🎯 Set Priority         │
├─────────────────────────┤
│  🏷️ Change Category     │
├─────────────────────────┤
│  ⏱️ Start Pomodoro      │
├─────────────────────────┤
│  🔄 Duplicate            │
├─────────────────────────┤
│  📤 Share                │
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Min Width | 180dp | Custom |
| Max Width | 240dp | Custom |
| Item Height | 40dp | `spacing.xxl_2` |
| Border Radius | 12dp | `border_radius.md` |
| Elevation | Level 3 | `shadows.level_3` |
| Background | Surface | `colors.surface` |
| Divider | Outline Variant (1dp) | `colors.outline_variant` |
| Item Padding | 16dp horizontal, 8dp vertical | `spacing.md`, `spacing.sm` |
| Typography | 14sp Medium | `typography.body.medium` |

**Menu Items:**
1. View Details → Opens TaskDetailSheet
2. Edit → Opens AddTaskSheet in edit mode
3. Delete → Shows confirmation dialog
4. Reschedule → Enables drag mode
5. Set Priority → Shows priority selector
6. Change Category → Shows category selector
7. Start Pomodoro → Starts Pomodoro for this task
8. Duplicate → Creates copy of task
9. Share → Opens share dialog

---

### 12. View Mode Toggles

**Purpose:** Switch between different timeline views

```
[Today] [3-Day] [Week] [Month] [Pomodoro]
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Padding | 8dp horizontal | `spacing.sm` |
| Border Radius | 16dp (Pill) | `border_radius.md_2` |
| Background (Active) | Primary 500 | `colors.primary_500` |
| Background (Inactive) | Transparent | - |
| Text Color (Active) | On Primary | `colors.on_primary` |
| Text Color (Inactive) | On Surface Variant | `colors.on_surface_variant` |
| Typography | 12sp Medium | `typography.label.medium` |

**View Modes:**
1. **Today:** Shows only current day
2. **3-Day:** Shows current day + next 2 days
3. **Week:** Shows 7 days (current week)
4. **Month:** Shows calendar grid with task indicators
5. **Pomodoro:** Shows only tasks with Pomodoro sessions

---

## 🎭 States

### Screen States

| State | Description | Visual |
|-------|-------------|--------|
| **Loading** | Initial load | Skeleton screen + Progress indicator |
| **Empty** | No tasks | Empty state illustration + "Add your first task" |
| **Error** | Load error | Error illustration + Retry button |
| **Offline** | No connection | Offline indicator + Limited functionality |

### Task States

| State | Visual |
|-------|--------|
| **Default** | Normal colors, Level 1 elevation |
| **Hover** | Slight color shift, Level 2 elevation |
| **Selected** | Primary border (2dp), Level 3 elevation |
| **Pressed** | Darker colors, Level 0 elevation |
| **Dragging** | Semi-transparent, Level 4 elevation |
| **Completed** | 40% opacity, checkmark icon |
| **Overdue** | Error border (1dp), Error 50 background (12%) |

---

## 🖱️ Interactions

### Gestures

| Gesture | Action | Feedback |
|---------|--------|----------|
| **Tap (Task)** | Open task details | Ripple effect |
| **Tap (Empty Space)** | Quick add task | Add dialog at position |
| **Long Press (Task)** | Open context menu | Haptic (Selection) + Menu |
| **Drag (Task)** | Move task in timeline | Task follows finger, snap-to-grid |
| **Drop (Task)** | Place task at new time | Snap animation + Haptic (Light) |
| **Pinch** | Zoom in/out | Smooth zoom animation |
| **Horizontal Swipe** | Switch days/views | Page transition |
| **Vertical Scroll** | Scroll timeline | Smooth scroll |

### Animations

| Animation | Duration | Easing |
|-----------|----------|--------|
| Task Drag | 0ms (direct) | Linear |
| Task Drop | 200ms | Ease-out |
| Task Snap | 150ms | Ease-out |
| Zoom | 300ms | Ease-in-out |
| View Switch | 250ms | Ease-in-out |
| Current Time Update | 1000ms | Linear |
| Pomodoro Timer | 1000ms | Linear |
| Context Menu Open | 150ms | Ease-out |
| Context Menu Close | 100ms | Ease-in |

---

## 🎯 Usage Guidelines

### Do's ✅
1. **Use consistent colors** - Always use category/priority colors
2. **Maintain touch targets** - Minimum 48dp for all interactive elements
3. **Show feedback** - Always show visual feedback on interactions
4. **Handle overlaps** - Show overlap indicators when tasks overlap
5. **Respect time zones** - Display times in user's time zone
6. **Show current time** - Always show current time indicator

### Don'ts ❌
1. **Don't overload** - Don't show too many tasks at once
2. **Don't hide information** - Always show task title and time
3. **Don't block UI** - Don't show modals that block the entire timeline
4. **Don't ignore performance** - Optimize for smooth scrolling and animations

---

## 📱 Responsive Design

### Mobile (0dp - 599dp)
- **Time Axis:** 56dp width
- **Task Blocks:** Min 100dp width
- **View Modes:** Today, 3-Day, Week (scrollable)
- **Bottom Bar:** Always visible
- **Context Menu:** Full screen dialog

### Tablet (600dp - 1023dp)
- **Time Axis:** 64dp width
- **Task Blocks:** Min 120dp width
- **View Modes:** Today, 3-Day, Week
- **Bottom Bar:** Optional
- **Context Menu:** Bottom sheet

### Desktop (1024dp+)
- **Time Axis:** 72dp width
- **Task Blocks:** Min 140dp width
- **View Modes:** All modes
- **Bottom Bar:** Hidden
- **Context Menu:** Floating menu

---

## 🔗 Related Files

- [Design Specification](../output/design-specs/timeline-spec.md)
- [Wireframes](../wireframes/low-fidelity/timeline-view-wireframe.md)
- [Competitive Analysis](../research/competitive-ui-analysis/ticktick-ui-analysis.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)

---

*Document Version: 1.0.0*
*Last Updated: 2026-09-05*
*Status: In Progress*
