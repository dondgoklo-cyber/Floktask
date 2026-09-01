# Gantt Screen - UI Design Documentation
# **Floktask - Personal Life OS**
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Task: DE-004

---

## 📋 Overview

This document describes the **Gantt Screen** UI design for Floktask, including all visual elements, components, states, and interactions for visualizing project timelines and task dependencies.

---

## 🎨 Screen Layout

### Structure (Hierarchy)

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE (Horizontal)                                      ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │       ││
│  │  │  Mon    │  Tue    │  Wed    │  Thu    │  Fri    │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │  TASK   │                                                 │ ││
│  │  │  LIST   │              GANTT AREA                         │ ││
│  │  │         │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │  │ 1. Task │  │ Task A      │──▶│ Task B      │              │ ││
│  │  │    A    │  │ Jan 1-3     │  │ Jan 3-5     │              │ ││
│  │  │ 2. Task │  │ ████████    │  │ ████████    │              │ ││
│  │  │    B    │  └─────────────┘  └─────────────┘              │ ││
│  │  │ 3. Task │       ↓ (Today Line - Orange)                   │ ││
│  │  │    C    │  ┌─────────────────────────────────────────┐  │ ││
│  │  │         │  │  💎 Milestone: Project Deadline             │  │ ││
│  │  │         │  │  Jan 5                                        │  │ ││
│  │  │         │  └─────────────────────────────────────────┘  │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
│  [+ Add Task]  [Critical Path: ON]  [Dependencies: ON]             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Components

### 1. Header Bar

**Purpose:** Navigation and view controls

```
┌─────────────────────────────────────────────────────────────────┐
│  [← Back]  Gantt Chart  [+]  [Week] [Month] [Year] [Today]      │
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
- **Title:** "Gantt Chart" (Title Large, 22sp Bold)
- **Add Button:** IconButton (24dp x 24dp) → Opens AddTaskSheet
- **View Toggles:** SegmentedButton with 4 options (Week, Month, Year, Today)
- **Toggles:** Critical Path, Dependencies (switches)

---

### 2. Time Scale

**Purpose:** Show time scale for orientation

```
┌─────────────────────────────────────────────────────────────────┐
│  TIME SCALE: January 1 - 31, 2026                                  │
│  ┌─────────┬─────────┬─────────┬─────────┬─────────┐            │
│  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │            │
│  │  Mon    │  Tue    │  Wed    │  Thu    │  Fri    │            │
│  └─────────┴─────────┴─────────┴─────────┴─────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height (Week View) | 48dp | Custom |
| Height (Month View) | 56dp | Custom |
| Height (Year View) | 64dp | Custom |
| Background | Surface Variant | `colors.surface_variant` |
| Text Color | On Surface Variant | `colors.on_surface_variant` |
| Divider Height | 1dp | Custom |
| Divider Color | Outline Variant | `colors.outline_variant` |
| Major Divider Height | 2dp | Custom |
| Major Divider Color | Outline | `colors.outline` |

**Levels:**
- **Week View:** Shows days with day names
- **Month View:** Shows dates with week numbers
- **Year View:** Shows months

---

### 3. Task List (Left Side)

**Purpose:** List view of tasks with details

```
┌─────────────┐
│  TASK LIST   │
├─────────────┤
│  1. Task A   │
│     Jan 1-3 │
│  2. Task B   │
│     Jan 3-5 │
│  3. Task C   │
│     Jan 2-4 │
│  ...         │
└─────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Width | 25% of screen | Custom |
| Min Width | 200dp | Custom |
| Max Width | 300dp | Custom |
| Background | Surface | `colors.surface` |
| Row Height | 40dp | `spacing.xxl_2` |
| Padding | 12dp | `spacing.sm_2` |

**Columns:**
1. **Task Name** (Body Medium, 14sp)
2. **Start Date** (Body Small, 12sp)
3. **End Date** (Body Small, 12sp)
4. **Duration** (Body Small, 12sp) - Optional
5. **Progress** (Progress bar) - Optional

**Grouping Options:**
- By Category
- By Priority
- By Project
- By Status

---

### 4. Gantt Area (Right Side)

**Purpose:** Visual timeline with task bars

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT AREA                                                      │
│  ┌─────────────┐  ┌─────────────┐                              │
│  │ Task A      │  │ Task B      │                              │
│  │ Jan 1-3     │  │ Jan 3-5     │                              │
│  │ ████████    │  │ ████████    │                              │
│  └─────────────┘  └─────────────┘                              │
│       ↓ (Today Line)                                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  💎 Milestone: Project Deadline                         │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Width | 75% of screen | Custom |
| Background | Surface | `colors.surface` |
| Grid Line Height | 1dp | Custom |
| Grid Line Color | Outline Variant | `colors.outline_variant` |
| Major Grid Line Height | 2dp | Custom |
| Major Grid Line Color | Outline | `colors.outline` |

---

### 5. Task Bar

**Purpose:** Visual representation of a task in Gantt chart

```
┌─────────────────────────┐
│ Task A                  │
│ Jan 1 - Jan 3           │
│ ████████████████████    │ ← Progress fill (optional)
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Selected Height | 40dp | Custom |
| Min Width | 20dp | Custom |
| Border Radius | 4dp | `border_radius.xxs` |
| Padding | 8dp horizontal, 4dp vertical | `spacing.sm`, `spacing.xs` |
| Margin | 4dp | `spacing.xs` |
| Elevation (Default) | Level 1 | `shadows.level_1` |
| Elevation (Hover) | Level 2 | `shadows.level_2` |

**Content:**
- **Title:** Task name (12sp Semi-Bold)
- **Duration:** Start and end dates (10sp Regular)
- **Progress:** Optional fill for completed portion
- **Handles:** Small drag handles on sides for resizing

**Colors by Category:**
- Work: `#4CAF50` (Green)
- Personal: `#FF9800` (Orange - Brand)
- Study: `#2196F3` (Blue)
- Shopping: `#9C27B0` (Purple)
- Health: `#F44336` (Red)
- Finance: `#FFC107` (Amber)
- Other: `#9E9E9E` (Gray)

**Colors by Priority:**
- Low: `#9E9E9E` (Gray)
- Medium: `#FFC107` (Amber)
- High: `#FF9800` (Orange)
- Critical: `#F44336` (Red)

---

### 6. Dependency Line

**Purpose:** Show relationships between tasks

```
┌─────────────┐     ┌─────────────┐
│ Task A      │─────▶│ Task B      │
└─────────────┘     └─────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Line Width | 2dp | Custom |
| Line Color | `#9E9E9E` | `gantt.dependency.line` |
| Line Style | Dashed | Custom |
| Arrow Size | 8dp | Custom |
| Arrow Color | `#9E9E9E` (or `#F44336` for critical) | `gantt.dependency.arrow` |

**Dependency Types:**
1. **Finish-to-Start (FS):** Task A must finish before Task B starts (most common)
2. **Start-to-Start (SS):** Task A must start before Task B starts
3. **Finish-to-Finish (FF):** Task A must finish before Task B finishes
4. **Start-to-Finish (SF):** Task A must start before Task B finishes

---

### 7. Milestone

**Purpose:** Mark important dates/deadlines

```
    ▼
┌─────────┐
│  💎 M1  │
│ Deadline│
└─────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Shape | Diamond | Custom |
| Size | 24dp x 24dp (minimum) | Custom |
| Border Radius | 2dp | `border_radius.xs` |
| Background | `#9C27B0` | `gantt.milestone.background` |
| Border | White, 2dp | Custom |
| Text Color | White | `gantt.milestone.text` |
| Typography | 10sp Bold | Custom |

**Placement:**
- On the timeline at the milestone date
- Can be dragged to change date
- Shows label on hover

---

### 8. Critical Path

**Purpose:** Highlight tasks that affect project deadline

```
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│ Task A      │──▶│ Task B      │──▶│ Task C      │
│ (Red border)│  │ (Red border)│  │ (Red border)│
└─────────────┘  └─────────────┘  └─────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Background | `#FFEBEE` (Light) / `#2D1B10` (Dark) | `gantt.critical_path.background` |
| Border | `#F44336` (Light) / `#FF6B6B` (Dark), 2dp | `gantt.critical_path.border` |
| Text Color | On Surface | - |

**Calculation:**
- Automatically determined based on task dependencies
- Tasks that, if delayed, will delay the project end date
- Can be toggled on/off

---

### 9. Today Indicator

**Purpose:** Show current date in timeline

```
    ┌───────┐
    │ TODAY │
    │СЕГОДНЯ│
    └───────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Line Width | 2dp | Custom |
| Line Color | `#FF9800` (Light) / `#FF8C00` (Dark) | `gantt.today` |
| Label | "TODAY" or "СЕГОДНЯ" | - |
| Label Typography | 10sp Medium | Custom |
| Label Color | Primary | `gantt.today` |

---

### 10. Context Menu

**Purpose:** Actions on right-click/long press

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
│  🔗 Create Dependency    │
├─────────────────────────┤
│  🎯 Set as Milestone     │
├─────────────────────────┤
│  🏷️ Change Category     │
├─────────────────────────┤
│  🔄 Duplicate            │
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
1. **View Details** → Opens TaskDetailSheet
2. **Edit** → Opens AddTaskSheet in edit mode
3. **Delete** → Shows confirmation dialog
4. **Reschedule** → Enables drag mode for task
5. **Create Dependency** → Enables dependency creation mode
6. **Set as Milestone** → Converts task to milestone
7. **Change Category** → Shows category selector
8. **Duplicate** → Creates copy of task

---

### 11. Add Task from Gantt

**Purpose:** Quick task creation from Gantt chart

```
┌─────────────────────────┐
│    + Add Task           │
│    Jan 3 - Jan 5        │ ← Pre-filled based on selection
└─────────────────────────┘

Then opens:
┌─────────────────────────┐
│  Title: [______________]│
│  Start: [Jan 3    ▼]   │
│  End:   [Jan 5    ▼]   │
│  Category: [Work  ▼]   │
│  Priority: [High  ▼]    │
│  [Cancel]      [Save]   │
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Initial Button Width | 160dp | Custom |
| Initial Button Height | 40dp | `spacing.xxl_2` |
| Initial Button Padding | 12dp | `spacing.sm_2` |
| Initial Button Border Radius | 8dp | `border_radius.sm_2` |
| Initial Button Background | Primary 500 | `colors.primary.500` |
| Dialog Width | 320dp | Custom |
| Dialog Padding | 24dp | `spacing.lg_2` |

---

### 12. Dependency Creation Mode

**Purpose:** Visual feedback when creating dependencies

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART (Dependency Mode)                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────┬─────────────────────────────────────────────────┐ ││
│  │  TASK   │                                                 │ ││
│  │  LIST   │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │         │  │ Task A      │  │ Task B      │              │ ││
│  │         │  │ Jan 1-3     │  │ Jan 3-5     │              │ ││
│  │         │  │ ◄───────────┘  └─────►      │ ← Preview line   │ ││
│  │         │  │             │              │              │ ││
│  │         │  └─────────────┘              └─────────────┘      │ ││
│  │         │                                                     │ ││
│  │         │  Drag from Task A to Task B to create dependency   │ ││
│  │         │                                                     │ ││
│  └─────────┴─────────────────────────────────────────────────┘ ││
│                                                                     │
│  [Cancel Dependency Mode]                                         │
└─────────────────────────────────────────────────────────────────┘
```

**Properties:**
- **Preview Line:** Dashed line follows cursor
- **Valid Target:** Highlight on tasks that can be dependencies
- **Invalid Target:** Red highlight on invalid targets
- **Cancel:** Button to exit dependency mode

---

## 🎭 States

### Screen States

| State | Description | Visual |
|-------|-------------|--------|
| **Loading** | Initial load | Skeleton screen + Progress indicator |
| **Empty** | No tasks | Empty state illustration + "Add your first task" |
| **Error** | Load error | Error illustration + Retry button |
| **Offline** | No connection | Offline indicator + Limited functionality |

### Task Bar States

| State | Visual |
|-------|--------|
| **Default** | Normal colors, Level 1 elevation |
| **Hover** | Slight color shift, Level 2 elevation |
| **Selected** | Primary border (2dp), Level 3 elevation |
| **Pressed** | Darker colors, Level 0 elevation |
| **Dragging** | Semi-transparent, Level 4 elevation, drag preview |
| **Resizing** | Semi-transparent, resize handles visible |
| **Completed** | 40% opacity, checkmark icon |
| **Overdue** | Error border (1dp), Error 50 background (12%) |

### Dependency States

| State | Visual |
|-------|--------|
| **Default** | Gray dashed line |
| **Hover** | Primary solid line |
| **Selected** | Primary solid line, thicker |
| **Critical** | Red solid line |

---

## 🖱️ Interactions

### Gestures

| Gesture | Action | Feedback |
|---------|--------|----------|
| **Tap (Task Bar)** | Open task details | Ripple effect |
| **Tap (Empty Space)** | Quick add task | Add dialog at position |
| **Long Press (Task Bar)** | Open context menu | Haptic (Selection) + Menu |
| **Drag (Task Bar)** | Move task in timeline | Task follows finger, snap-to-grid |
| **Drag (Edge Handle)** | Resize task duration | Task resizes, duration updates |
| **Drag (Task → Task)** | Create dependency | Dependency preview line |
| **Pinch** | Zoom in/out | Smooth zoom animation |
| **Horizontal Drag** | Pan timeline | Smooth scroll |
| **Vertical Scroll** | Scroll task list | Smooth scroll |

### Animations

| Animation | Duration | Easing |
|-----------|----------|--------|
| Task Drag | 0ms (direct) | Linear |
| Task Drop | 200ms | Ease-out |
| Task Resize | 150ms | Ease-out |
| Dependency Create | 200ms | Ease-out |
| Zoom | 250ms | Ease-in-out |
| Pan | 0ms | Linear |
| View Switch | 200ms | Ease-in-out |
| Context Menu Open | 150ms | Ease-out |
| Context Menu Close | 100ms | Ease-in |

---

## 🎯 Usage Guidelines

### Do's ✅
1. **Use consistent colors** - Always use category/priority colors
2. **Maintain touch targets** - Minimum 48dp for all interactive elements
3. **Show feedback** - Always show visual feedback on interactions
4. **Handle dependencies** - Show dependency lines clearly
5. **Highlight critical path** - Make it easy to see what affects deadlines
6. **Respect time zones** - Display dates in user's time zone
7. **Show today** - Always show today indicator
8. **Optimize for mobile** - Large touch targets, simplified layout

### Don'ts ❌
1. **Don't overload** - Don't show too many tasks at once
2. **Don't hide information** - Always show task name and dates
3. **Don't block UI** - Don't show modals that block the entire Gantt
4. **Don't ignore performance** - Optimize for smooth scrolling and animations
5. **Don't make it complex** - Keep it simple for personal use

---

## 📱 Responsive Design

### Mobile (0dp - 599dp)
```
┌─────────────────────┐
│  Gantt              │
├─────────────────────┤
│  [Week] [Month]     │
├─────────────────────┤
│  ┌───────────────┐ │
│  │ Time Scale    │ ← Horizontal, scrollable
│  └───────────────┘ │
│  ┌───────────────┐ │
│  │ Task Bars     │ ← Scrollable horizontally
│  │ ████ ████     │
│  └───────────────┘ │
│  [← Swipe →]        │
├─────────────────────┤
│  [+] [Today]        │
└─────────────────────┘
```

**Characteristics:**
- **Vertical Layout:** Time scale on top
- **Scrollable:** Horizontal scrolling for timeline
- **Touch-Friendly:** Large touch targets (48dp minimum)
- **Simplified:** Only essential controls
- **Task List:** Hidden by default, toggleable

### Tablet (600dp - 1023dp)
```
┌─────────────────────────────────────────────────────────────────┐
│  Gantt Chart                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┬─────────────────────────────────────────────┐│
│  │  Time Scale  │  Gantt Area                                   ││
│  ├─────────────┼─────────────────────────────────────────────┤│
│  │  Task List   │  ┌─────────┐ ┌─────────┐                    ││
│  │              │  │ Task A  │ │ Task B  │                    ││
│  │  1. Task A  │  └─────────┘ └─────────┘                    ││
│  │  2. Task B  │                                             ││
│  │              │                                             ││
│  └─────────────┴─────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

**Characteristics:**
- **Two-Panel Layout:** Task list (30%) + Gantt area (70%)
- **Scrollable:** Both panels scrollable independently
- **Touch-Friendly:** Large touch targets
- **All Controls:** All view toggles visible

### Desktop (1024dp+)
```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┬─────────────────────────────────────────────┐│
│  │  TIME SCALE  │  GANTT AREA                                   ││
│  ├─────────────┼─────────────────────────────────────────────┤│
│  │  TASK LIST   │  ┌─────────────┐ ┌─────────────┐              ││
│  │              │  │ Task A      │──▶│ Task B      │              ││
│  │  1. Task A   │  └─────────────┘ └─────────────┘              ││
│  │     Jan 1-3 │                                                     ││
│  │  2. Task B   │  ┌─────────────────────────────────────────┐  ││
│  │     Jan 3-5 │  │  💎 Milestone: Project Deadline             │  ││
│  │              │  └─────────────────────────────────────────┘  ││
│  │  3. Task C   │                                                     ││
│  │     Jan 2-4 │                                                     ││
│  │              │                                                     ││
│  └─────────────┴─────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Characteristics:**
- **Two-Panel Layout:** Task list (25%) + Gantt area (75%)
- **All Features:** Full functionality
- **Keyboard Support:** Full keyboard navigation
- **Mouse Support:** All mouse interactions

---

## 🔗 Related Files

- [Design Specification](../output/design-specs/gantt-spec.md)
- [Wireframes](../wireframes/low-fidelity/gantt-charts-wireframe.md)
- [Competitive Analysis](../../research/competitive-ui-analysis/gantt-charts-ui-analysis.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Timeline Screen](./timeline-screen.md)

---

*Document Version: 1.0.0*
*Last Updated: 2026-09-05*
*Status: In Progress*
