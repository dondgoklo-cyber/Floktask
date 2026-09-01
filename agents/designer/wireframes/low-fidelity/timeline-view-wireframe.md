# Floktask Timeline View - Low Fidelity Wireframes
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Task: DE-003

## 📋 Overview

This document contains **low-fidelity wireframes** for Floktask's Timeline View. These wireframes focus on layout, structure, and information hierarchy without visual design details.

---

## 🎯 Design Goals

1. **Clarity:** Easy to understand at a glance
2. **Efficiency:** Quick task scheduling and management
3. **Flexibility:** Multiple view modes (Day, 3-Day, Week, Month)
4. **Integration:** Seamless with Floktask's existing features
5. **Accessibility:** Works for all users

---

## 📐 Wireframe Structure

### ASCII Wireframes

#### 1. Daily View (Default)

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Pomodoro]│
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬─────────────────────────────────────────────────────┐│
│  │         │                                                     ││
│  │  00:00 │                                                     ││
│  │  01:00 │                                                     ││
│  │  02:00 │                                                     ││
│  │  03:00 │                                                     ││
│  │  04:00 │                                                     ││
│  │  05:00 │  ┌─────────────────────────┐                         ││
│  │  06:00 │  │ Morning Routine          │                         ││
│  │  07:00 │  │ 06:00 - 07:30            │                         ││
│  │  08:00 │  └─────────────────────────┘                         ││
│  │  09:00 │  ┌─────────────────────────────────────────┐          ││
│  │  10:00 │  │ Deep Work Block                            │          ││
│  │  11:00 │  │ 09:00 - 12:00                              │          ││
│  │  12:00 │  │ ┌─────────────┐                            │          ││
│  │        │  │ │ Design UI  │  ┌─────────────┐              │          ││
│  │ 13:00 │  │ │ 10:00-12:00 │  │ Meeting     │              │          ││
│  │        │  │ └─────────────┘  │ 11:00-12:00 │              │          ││
│  │ 14:00 │  │                  └─────────────┘              │          ││
│  │  15:00 │  └─────────────────────────────────────────┘          ││
│  │  16:00 │  ┌─────────────┐                                      ││
│  │  17:00 │  │ Gym          │                                      ││
│  │  18:00 │  │ 16:00-18:00  │                                      ││
│  │  19:00 │  └─────────────┘                                      ││
│  │  20:00 │  ┌─────────────┐                                      ││
│  │  21:00 │  │ Dinner       │                                      ││
│  │  22:00 │  │ 20:00-21:30  │                                      ││
│  │  23:00 │  └─────────────┘                                      ││
│  │         │                                                     ││
│  └─────────┴─────────────────────────────────────────────────────┘│
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │  │
│  │  │  + Add  │ │  Today  │ │  3-Day  │ │  Week   │          │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Time Axis (Left):** 56dp wide, shows hours 00:00-23:00
- **Content Area:** Tasks displayed as blocks
- **Current Time:** Red line with "NOW" label (not shown in this static wireframe)
- **Task Blocks:** Variable width based on duration
- **Overlap:** Tasks can overlap (Meeting and Design UI at 10:00-12:00)
- **Time Blocks:** Deep Work Block spans 3 hours
- **Bottom Bar:** View mode toggles

---

#### 2. 3-Day View

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Pomodoro]│
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬──────────────┬──────────────┬──────────────┐         │
│  │         │ Today        │ Tomorrow     │ Day After    │         │
│  │  00:00  ├──────────────┼──────────────┼──────────────┤         │
│  │  02:00  │              │              │              │         │
│  │  04:00  │              │              │              │         │
│  │  06:00  │  ┌────────┐  │              │              │         │
│  │  08:00  │  │ Morning │  │ ┌────────┐  │              │         │
│  │  10:00  │  │ Routine│  │ │ Morning │  │              │         │
│  │  12:00  │  └────────┘  │ │ Routine│  │              │         │
│  │        │              │ └────────┘  │              │         │
│  │  14:00  │  ┌─────────────────────┐  │              │              │
│  │  16:00  │  │ Deep Work            │  │ ┌────────┐  │              │
│  │  18:00  │  │ 14:00-18:00          │  │ │ Gym    │  │              │
│  │  20:00  │  └─────────────────────┘  │ └────────┘  │              │
│  │  22:00  │                          │              │              │
│  └─────────┴──────────────┴──────────────┴──────────────┘         │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │  [+] [Today] [3D] [Week] [Month] [Pomodoro]                     │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **3 Columns:** One for each day
- **Time Axis:** Shared across all days
- **Hour Lines:** Every 2 hours (00:00, 02:00, 04:00, etc.)
- **Scrollable:** Horizontal scrolling for more days
- **Consistent Height:** Same vertical scale as Daily view

---

#### 3. Weekly View

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Pomodoro]│
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬──────────┬──────────┬──────────┬──────────┬──────┐ │
│  │         │ Mon      │ Tue      │ Wed      │ Thu      │ Fri  │ │
│  │  00:00  ├──────────┼──────────┼──────────┼──────────┼──────┤ │
│  │  04:00  │          │          │          │          │      │ │
│  │  08:00  │ ┌────┐  │ ┌────┐  │          │ ┌────┐  │      │ │
│  │  12:00  │ │Morn│  │ │Morn│  │ ┌────┐  │ │Morn│  │      │ │
│  │  16:00  │ └────┘  │ └────┘  │ │Work│  │ └────┘  │      │ │
│  │  20:00  │          │          │ └────┘  │          │      │ │
│  │  24:00  ├──────────┼──────────┼──────────┼──────────┼──────┤ │
│  └─────────┴──────────┴──────────┴──────────┴──────────┴──────┘ │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │  [+] [Today] [3D] [Week] [Month] [Pomodoro]                     │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **7 Columns:** One for each day of the week
- **Time Axis:** Shared, shows every 4 hours (00:00, 04:00, 08:00, etc.)
- **Compact:** Tasks shown as small blocks
- **Scrollable:** Horizontal scrolling for all 7 days
- **Day Headers:** Mon, Tue, Wed, Thu, Fri, Sat, Sun

---

#### 4. Monthly View

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Pomodoro]│
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  SUN  MON  TUE  WED  THU  FRI  SAT                           │ │
│  ├─────────────────────────────────────────────────────────────┤ │
│  │                                                       SEPTEMBER  │ │
│  ├─────────────────────────────────────────────────────────────┤ │
│  │  1    2    3    4    5    6    7                             │ │
│  │ ┌────┐     ┌────┐     ┌────┐                                  │ │
│  │ │    │     │    │     │    │                                  │ │
│  │ └────┘     └────┘     └────┘                                  │ │
│  ├─────────────────────────────────────────────────────────────┤ │
│  │  8    9   10   11   12   13   14                             │ │
│  │ ┌────┐     ┌────┐     ┌────┐     ┌────┐                      │ │
│  │ │    │     │    │     │    │     │    │                      │ │
│  │ └────┘     └────┘     └────┘     └────┘                      │ │
│  ├─────────────────────────────────────────────────────────────┤ │
│  │ 15   16   17   18   19   20   21                             │ │
│  │                     ┌────┐     ┌────┐                          │ │
│  │                     │    │     │    │                          │ │
│  │                     └────┘     └────┘                          │ │
│  ├─────────────────────────────────────────────────────────────┤ │
│  │ 22   23   24   25   26   27   28                             │ │
│  │ ┌────┐     ┌────┐                                             │ │
│  │ │    │     │    │                                             │ │
│  │ └────┘     └────┘                                             │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  Legend: ┌────┐ = Task exists, color indicates category/priority   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │  [+] [Today] [3D] [Week] [Month] [Pomodoro]                     │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Calendar Grid:** Standard month view
- **Task Indicators:** Small colored bars in cells
- **Color Coding:** By category or priority
- **Navigation:** Previous/next month (not shown, would be in header)
- **Week Header:** Day abbreviations

---

#### 5. Task Detail in Timeline (Long Press Context Menu)

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Pomodoro]│
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬─────────────────────────────────────────────────────┐│
│  │         │                                                     ││
│  │  10:00  │  ┌─────────────────────────┐                         ││
│  │  11:00  │  │ Design UI              ║                         ││
│  │  12:00  │  │ 10:00 - 12:00          ║                         ││
│  │         │  └─────────────────────────╘                         ││
│  │         │       ↓ (Long press here)                             ││
│  │         │  ┌─────────────────────────────────────────────┐    ││
│  │         │  │  Context Menu                            │    ││
│  │         │  ├─────────────────────────────────────────────┤    ││
│  │         │  │  📝 View Details                         │    ││
│  │         │  │  ✏️ Edit                                │    ││
│  │         │  │  🗑️ Delete                              │    ││
│  │         │  │  📅 Reschedule                          │    ││
│  │         │  │  🎯 Set Priority                         │    ││
│  │         │  │  🏷️ Change Category                      │    ││
│  │         │  │  ⏱️ Start Pomodoro                      │    ││
│  │         │  │  🔄 Duplicate                            │    ││
│  │         │  │  📤 Share                                │    ││
│  │         │  └─────────────────────────────────────────────┘    ││
│  │         │                                                     ││
│  └─────────┴─────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Context Menu:** Appears on long press
- **Menu Items:** 8-10 common actions
- **Icons:** Each item has an icon
- **Dividers:** Visual separation between groups
- **Position:** Appears near the task, avoids edges

---

#### 6. Task Creation from Timeline

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Pomodoro]│
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬─────────────────────────────────────────────────────┐│
│  │         │                                                     ││
│  │  14:00  │  ┌─────────────────────────────────────────────┐    ││
│  │  15:00  │  │  ┌─────────────────────────┐               │    ││
│  │  16:00  │  │  │                         │               │    ││
│  │         │  │  │    + Add Task           │ ← Click here    │    ││
│  │         │  │  │    at 14:30             │               │    ││
│  │         │  │  │                         │               │    ││
│  │         │  │  └─────────────────────────┘               │    ││
│  │         │  │                                              │    ││
│  │         │  │  ┌─────────────────────────────────────────┐│    ││
│  │         │  │  │ Title: [______________]               ││    ││
│  │         │  │  │                                         ││    ││
│  │         │  │  │ Start: [14:30    ▼]                  ││    ││
│  │         │  │  │ Duration: [1 hour ▼]                  ││    ││
│  │         │  │  │                                         ││    ││
│  │         │  │  │ Category: [Work    ▼]               ││    ││
│  │         │  │  │ Priority: [High    ▼]                ││    ││
│  │         │  │  │                                         ││    ││
│  │         │  │  │ [Cancel]              [Save]          ││    ││
│  │         │  │  └─────────────────────────────────────────┘│    ││
│  │         │  │                                              │    ││
│  │         │  └─────────────────────────────────────────────┘    ││
│  │         │                                                     ││
│  └─────────┴─────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Quick Add:** Click on empty space in timeline
- **Smart Defaults:** Time pre-filled based on click position
- **Inline Form:** Appears directly in timeline
- **Fields:** Title, Start time, Duration, Category, Priority
- **Actions:** Cancel and Save

---

#### 7. Pomodoro Integration

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Pomodoro]│
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬─────────────────────────────────────────────────────┐│
│  │         │                                                     ││
│  │  10:00  │  ┌─────────────────────────────────────────────┐    ││
│  │  11:00  │  │  Design UI                                        │    ││
│  │  12:00  │  │  ┌─────────────────────────────────────────┐│    ││
│  │         │  │  │  ⏱️ 25:00    [Start] [Pause] [Stop]          ││    ││
│  │         │  │  │  Pomodoro Session #1                          ││    ││
│  │         │  │  └─────────────────────────────────────────┘│    ││
│  │         │  │  10:00 - 12:00                                  │    ││
│  │         │  └─────────────────────────────────────────────┘    ││
│  │         │                                                     ││
│  │  12:30  │  ┌─────────────────────────────────────────────┐    ││
│  │  13:00  │  │  ⏳ 05:00    [Resume]                           │    ││
│  │         │  │  Short Break                                  │    ││
│  │         │  └─────────────────────────────────────────────┘    ││
│  │         │                                                     ││
│  └─────────┴─────────────────────────────────────────────────────┘│
│                                                                     │
│  Current Time: 12:25 (red line)                                       │
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Pomodoro Controls:** Integrated into task block
- **Timer Display:** Shows remaining time
- **Session Indicator:** "Pomodoro Session #1"
- **Break Timer:** Shows break time after session
- **Controls:** Start, Pause, Stop, Resume

---

#### 8. Time Blocking Mode

```
┌─────────────────────────────────────────────────────────────────┐
│  TIMELINE                          [+] [Today] [3D] [Week] [Month] [Blocks] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬─────────────────────────────────────────────────────┐│
│  │         │                                                     ││
│  │  09:00  │  ┌─────────────────────────────────────────────┐    ││
│  │  10:00  │  │  🏢 Deep Work Block                            │    ││
│  │  11:00  │  │  Fixed: 09:00 - 12:00                           │    ││
│  │  12:00  │  │  Recurring: Mon-Fri                            │    ││
│  │         │  │  ┌─────────────┐                              │    ││
│  │         │  │  │ Design UI  │ ← Tasks can be placed inside   │    ││
│  │         │  │  │ 10:00-12:00 │                              │    ││
│  │         │  │  └─────────────┘                              │    ││
│  │         │  └─────────────────────────────────────────────┘    ││
│  │         │                                                     ││
│  │  13:00  │  ┌─────────────────────────────────────────────┐    ││
│  │  14:00  │  │  🍽️ Lunch Break                              │    ││
│  │  15:00  │  │  Fixed: 13:00 - 14:00                           │    ││
│  │         │  └─────────────────────────────────────────────┘    ││
│  │         │                                                     ││
│  │  18:00  │  ┌─────────────────────────────────────────────┐    ││
│  │  19:00  │  │  🏋️ Gym Time                                │    ││
│  │  20:00  │  │  Fixed: 18:00 - 20:00                           │    ││
│  │         │  └─────────────────────────────────────────────┘    ││
│  │         │                                                     ││
│  └─────────┴─────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Time Blocks:** Fixed time periods for specific activities
- **Visual Distinction:** Different styling from regular tasks
- **Recurring:** Can be set to repeat on specific days
- **Task Placement:** Tasks can be placed inside blocks
- **Block Types:** Deep Work, Lunch, Gym, etc.

---

## 📊 Information Hierarchy

### Visual Priority

1. **Highest Priority (Most Visible)**
   - Current time indicator (red line)
   - Active Pomodoro timers
   - Overlapping tasks (visual stacking)

2. **High Priority**
   - Task blocks
   - Day separators
   - Time axis labels

3. **Medium Priority**
   - Hour grid lines
   - All-day tasks
   - View mode toggles

4. **Low Priority**
   - Empty space
   - Background colors

---

## 🎯 User Flow

### Primary Flow: Schedule a Task

```
User sees empty space in timeline
    ↓
User taps on empty space
    ↓
Quick add dialog appears at that time
    ↓
User enters task details
    ↓
User taps Save
    ↓
Task appears in timeline at specified time
```

### Secondary Flow: Reschedule a Task

```
User finds task in timeline
    ↓
User long-presses on task
    ↓
Context menu appears
    ↓
User selects "Reschedule"
    ↓
User drags task to new time
    ↓
Task snaps to new position
    ↓
Task is rescheduled
```

### Tertiary Flow: Start Pomodoro

```
User finds task in timeline
    ↓
User taps on task
    ↓
Task details appear
    ↓
User taps "Start Pomodoro"
    ↓
Pomodoro timer appears in task block
    ↓
Timer starts counting down
```

---

## 📐 Layout Specifications

### Time Axis

| Property | Value | Token |
|----------|-------|-------|
| Width | 56dp | Custom |
| Background | Surface Variant | `colors.surface_variant` |
| Label Color | On Surface Variant | `colors.on_surface_variant` |
| Label Size | 11sp | `typography.timeline.time` |
| Label Padding | 4dp | `spacing.xs` |
| Hour Height (Daily) | 60dp | Custom |
| Hour Height (3-Day) | 30dp | Custom |
| Hour Height (Weekly) | 15dp | Custom |

### Content Area

| Property | Value | Token |
|----------|-------|-------|
| Background (Today) | Premium Subtle | `premium.subtle_background` |
| Background (Future) | Surface | `colors.surface` |
| Background (Past) | Surface Variant | `colors.surface_variant` |
| Grid Line Color | Outline Variant | `colors.outline_variant` |
| Grid Line Height | 1dp | Custom |

### Task Blocks

| Property | Value | Token |
|----------|-------|-------|
| Min Width | 100dp | Custom |
| Min Height | 40dp | Custom |
| Padding | 8dp h, 4dp v | `spacing.sm`, `spacing.xs` |
| Margin | 4dp | `spacing.xs` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Elevation | Level 1 | `shadows.level_1` |
| Hover Elevation | Level 2 | `shadows.level_2` |

### Current Time Indicator

| Property | Value | Token |
|----------|-------|-------|
| Height | 2dp | Custom |
| Color | Error 500 | `colors.error_500` |
| Label | "СЕЙЧАС" | - |
| Label Size | 10sp | Custom |
| Label Color | Error 500 | `colors.error_500` |
| Animation | Smooth transition | Custom |

---

## 🎨 Visual Style Notes

### Colors by Task Status

| Status | Background | Text | Border |
|--------|------------|------|--------|
| Default | Category Color | On Category | None |
| Hover | Category Color (90%) | On Category | None |
| Selected | Category Color (80%) | On Category | Primary 500 (2dp) |
| Completed | Category Color (40%) | On Category (40%) | None |
| Overdue | Error 50 (12%) | Error 900 | Error 500 (1dp) |

### Colors by Task Priority

| Priority | Background | Text |
|----------|------------|------|
| Low | Gray 200 | On Surface |
| Medium | Primary 100 | Primary 900 |
| High | Primary 200 | Primary 900 |
| Critical | Error 100 | Error 900 |

---

## ✅ Wireframe Validation

### Usability Checklist

- [x] Clear visual hierarchy
- [x] Intuitive layout
- [x] Logical information flow
- [x] Appropriate spacing
- [x] Consistent patterns
- [x] Accessible touch targets
- [x] Responsive to different screens

### Design System Compliance

- [x] Uses existing color tokens
- [x] Uses existing typography tokens
- [x] Uses existing spacing tokens
- [x] Follows component patterns
- [x] Maintains brand identity

---

## 📁 Next Steps

1. **High-Fidelity Design** → Create detailed visual designs
2. **Prototype** → Build interactive prototype
3. **Design Spec** → Write comprehensive specification
4. **User Testing** → Validate with users
5. **Iteration** → Refine based on feedback

---

## 🎯 Related Documents

- [TickTick UI Analysis](../../research/competitive-ui-analysis/ticktick-ui-analysis.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)

---

*Last updated: 2026-09-05*
*Status: Low-Fidelity Complete ✅*
*Next: High-Fidelity Design*
