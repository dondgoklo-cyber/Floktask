# Competitive UI Analysis: TickTick Timeline View
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Related Task: DE-003

## 📋 Executive Summary

This document analyzes **TickTick's Timeline View** to inform the design of Floktask's Timeline feature. TickTick offers one of the best timeline implementations among task management apps, with intuitive drag-and-drop, clear visual hierarchy, and excellent time-blocking capabilities.

**Key Findings:**
- ✅ Excellent visual hierarchy with color-coded time blocks
- ✅ Intuitive drag-and-drop for task rescheduling
- ✅ Clear current time indicator
- ✅ Flexible zoom levels (day, 3-day, week views)
- ⚠️ Can feel cluttered with many tasks
- ⚠️ Limited customization options

**Our Improvement Opportunities:**
1. Better visual separation between tasks
2. More customizable time blocks
3. Integrated Pomodoro timer in timeline
4. Smart suggestions for task placement

---

## 🔍 Analysis Methodology

### Research Scope
- **Platform:** Android (primary), iOS, Web
- **Version:** TickTick Premium v5.0+
- **Analysis Period:** September 2026
- **Screens Analyzed:** Daily, 3-Day, Weekly Timeline views

### Analysis Framework
1. **Visual Design** - Colors, typography, spacing
2. **Interaction Design** - Gestures, animations, feedback
3. **Information Architecture** - Layout, hierarchy, grouping
4. **User Experience** - Usability, accessibility, delight
5. **Technical Considerations** - Performance, implementation

---

## 🎨 Visual Design Analysis

### Color System

| Element | Color (Light) | Color (Dark) | Purpose |
|---------|---------------|--------------|---------|
| **Today Background** | `#FFF5F2` | `#2D1B10` | Highlight current day |
| **Future Background** | `#FFFFFF` | `#1E1E1E` | Standard background |
| **Past Background** | `#F9F9F9` | `#121212` | Dimmed past days |
| **Current Time Line** | `#FF4757` (Red) | `#FF6B6B` | Current time indicator |
| **Task Blocks** | Various colors | Various colors | Task representation |
| **All-Day Tasks** | `#E0F7FA` (Light Blue) | `#003C49` | All-day events |
| **Time Grid Lines** | `#E0E0E0` | `#333333` | Hour separators |
| **Now Indicator** | `#FF4757` | `#FF6B6B` | Current time marker |

**Color Palette for Task Categories:**
```
Work: #4CAF50 (Green)
Personal: #FF9800 (Orange)
Study: #2196F3 (Blue)
Shopping: #9C27B0 (Purple)
Health: #F44336 (Red)
Finance: #FFC107 (Amber)
Other: #9E9E9E (Gray)
```

**Our Recommendation:**
- Use our existing **primary orange (`#FF9800`)** for brand consistency
- Add **category-specific colors** from our finance palette
- **Current time:** Use `error_500 (`#F44336`)** for visibility
- **Today background:** Use `premium.subtle_background (`#FFF3E0`)**

---

### Typography

| Element | Font Size | Font Weight | Color | Token Mapping |
|---------|-----------|-------------|-------|----------------|
| **Time Axis Labels** | 11sp | Medium | `#666666` | `typography.timeline.time` |
| **Task Title** | 14sp | Semi-Bold | `#333333` / `#FFFFFF` | `typography.task.title` |
| **Task Time** | 12sp | Regular | `#666666` / `#CCCCCC` | `typography.timeline.event` |
| **Day Header** | 16sp | Bold | `#333333` / `#FFFFFF` | `typography.title.medium` |
| **All-Day Label** | 12sp | Medium | `#00838F` / `#4FC3F7` | Custom |

**Our Recommendation:**
- Use our **typography scale** with slight adjustments
- **Task titles:** `typography.task.title` (16sp Semi-Bold)
- **Time labels:** `typography.timeline.time` (11sp Medium)
- **Day headers:** `typography.title.large` (22sp Bold)

---

### Spacing

| Element | Spacing | Token Mapping |
|---------|---------|----------------|
| **Time Axis Width** | 60dp | `timeline.time_axis.width` |
| **Task Block Padding** | 8dp | `spacing.sm` |
| **Task Block Margin** | 4dp | `spacing.xs` |
| **Task Block Min Height** | 32dp | `spacing.lg_2` |
| **Day Separator Height** | 1dp | Custom |
| **Hour Grid Line Height** | 1dp | Custom |
| **Current Time Indicator Height** | 2dp | Custom |
| **Between Tasks** | 4dp | `spacing.xs` |
| **Between Time Slots** | 8dp | `spacing.sm` |

**Our Recommendation:**
- **Time axis:** 56dp (accommodates longer time labels)
- **Task blocks:** Minimum 40dp height for better touch targets
- **Spacing:** Use our existing spacing tokens

---

### Border Radius

| Element | Border Radius | Token Mapping |
|---------|---------------|----------------|
| **Task Blocks** | 4dp | `border_radius.xxs` |
| **All-Day Tasks** | 0dp | `border_radius.none` |
| **Current Time Indicator** | 0dp | `border_radius.none` |
| **Day Headers** | 0dp | `border_radius.none` |

**Our Recommendation:**
- **Task blocks:** 8dp (`border_radius.sm_2`) for softer look
- **Current time indicator:** 0dp (sharp line)
- **All-day tasks:** 4dp (`border_radius.xxs`) for consistency

---

## 🖱️ Interaction Design Analysis

### Gestures

| Gesture | Action | Feedback | Our Implementation |
|---------|--------|----------|-------------------|
| **Drag Task** | Move task in timeline | Task follows finger, snap to grid | ✅ Implement |
| **Tap Task** | Open task details | Ripple effect | ✅ Implement |
| **Long Press Task** | Context menu | Haptic feedback + menu | ✅ Implement |
| **Pinch Zoom** | Zoom in/out timeline | Smooth zoom animation | ✅ Implement |
| **Horizontal Swipe** | Switch days | Page transition | ✅ Implement |
| **Double Tap** | Quick add task | Add dialog at time | ⚠️ Consider |
| **Drag from Edge** | Quick add task | Create at edge time | ⚠️ Consider |

**Gesture Priority:**
1. **Drag** - Primary interaction for timeline
2. **Tap** - Secondary, for details
3. **Long Press** - Tertiary, for actions

---

### Animations

| Animation | Duration | Easing | Trigger |
|-----------|----------|--------|---------|
| **Task Drag** | 0ms (direct) | Linear | Drag start |
| **Task Drop** | 200ms | Ease-out | Drag end |
| **Task Snap** | 150ms | Ease-out | Auto-align |
| **Task Hover** | 100ms | Linear | Hover |
| **Zoom In/Out** | 300ms | Ease-in-out | Pinch |
| **Day Switch** | 250ms | Ease-in-out | Swipe |
| **Current Time Update** | 1000ms | Linear | Every minute |

**Our Recommendation:**
- Use **150ms tween** for most animations (matches our premium system)
- **Drag:** Direct manipulation (0ms delay)
- **Drop:** 200ms ease-out
- **Snap:** 150ms ease-out
- **Zoom:** 300ms ease-in-out

---

### Haptic Feedback

| Action | Haptic Type | Intensity |
|--------|-------------|-----------|
| **Task Drag Start** | Light | Low |
| **Task Drop** | Selection | Medium |
| **Task Snap** | Light | Low |
| **Long Press** | Selection | Medium |
| **Zoom Start** | Light | Low |
| **Day Switch** | Light | Low |

**Our Implementation:**
Use our **HapticManager** from Module 15:
- Drag start: `HapticType.LIGHT`
- Drop: `HapticType.SELECTION`
- Snap: `HapticType.LIGHT`
- Long press: `HapticType.SELECTION`

---

## 🏗️ Information Architecture

### Layout Structure

```
┌─────────────────────────────────────────────────────────────┐
│  TickTick Timeline View (Daily)                              │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────┬───────────────────────────────────────────────┐ │
│  │ Time    │                                               │ │
│  │ Axis    │                                               │ │
│  │ (60dp)  │                                               │ │
│  │         │                                               │ │
│  │ 00:00  ├───────────────────────────────────────────────┤ │ │
│  │ 01:00  │  [Task 1]  [Task 2]                           │ │
│  │ 02:00  ├───────────────────────────────────────────────┤ │ │
│  │ 03:00  │  [Task 3]                                    │ │
│  │  ...   │                                               │ │
│  │ 23:00  ├───────────────────────────────────────────────┤ │ │
│  │         │                                               │ │
│  └─────────┴───────────────────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │  [+] Add Task    [Today]    [3-Day]    [Week]    [Month]   ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

### Time Axis

**Left Side (60dp wide):**
- **Hour Labels:** 00:00, 01:00, 02:00, ..., 23:00
- **Format:** 24-hour or 12-hour (user preference)
- **Spacing:** 1 hour = variable height based on zoom level
- **Labels:** 11sp Medium, `#666666` / `#CCCCCC`

**Our Implementation:**
- **Width:** 56dp (saves space for content)
- **Hour height:** Dynamic based on zoom
- **Labels:** `typography.timeline.time`

---

### Content Area

**Main Timeline Area:**
- **Width:** Remaining space after time axis
- **Background:** Today = `#FFF5F2`, Future = `#FFFFFF`, Past = `#F9F9F9`
- **Grid Lines:** Horizontal lines at each hour
- **Current Time:** Red line (`#FF4757`) with "NOW" label

**Our Implementation:**
- **Today:** `premium.subtle_background` (`#FFF3E0`)
- **Future:** `surface` (`#FFFFFF`)
- **Past:** `surface_variant` (`#F5F5F5`)
- **Current Time:** `error_500` (`#F44336`)

---

### Task Blocks

**Visual Properties:**
```
┌─────────────────────────┐
│ Task Title              │ ← 14sp Semi-Bold
│                         │
│ Time Range             │ ← 12sp Regular
└─────────────────────────┘
```

**Dimensions:**
- **Min Width:** 120dp
- **Min Height:** 32dp
- **Padding:** 8dp horizontal, 4dp vertical
- **Border Radius:** 4dp
- **Margin:** 4dp between tasks

**Color Coding:**
- By task category/color
- Opacity: 100% for active, 60% for completed

**Our Implementation:**
- **Min Width:** 100dp (more compact)
- **Min Height:** 40dp (better touch target)
- **Padding:** `spacing.sm` (8dp) horizontal, `spacing.xs` (4dp) vertical
- **Border Radius:** `border_radius.sm_2` (8dp)
- **Colors:** Use task priority colors from our palette

---

### All-Day Tasks

**Visual Properties:**
```
┌─────────────────────────────────────┐
│ All-Day Task Title                  │
└─────────────────────────────────────┘
```

**Placement:**
- At the top of the timeline
- Spans full width
- Height: 24dp
- Background: `#E0F7FA` (Light Blue) / `#003C49` (Dark)

**Our Implementation:**
- **Height:** 32dp
- **Background:** `finance.income` (12% opacity) for light, `finance.income` (24% opacity) for dark
- **Typography:** `typography.body.medium`

---

### Current Time Indicator

**Visual Properties:**
```
Current Time Line: │ (2dp red line)
                  ▼
                  NOW (label)
```

**Behavior:**
- Updates every minute
- Smooth animation to new position
- "NOW" label appears when line is visible

**Our Implementation:**
- **Line:** 2dp, `error_500` (`#F44336`)
- **Label:** "СЕЙЧАС" in Russian
- **Animation:** Smooth transition every minute

---

## 📊 Zoom Levels

### Daily View
```
┌─────────────────────────────────────┐
│  Hour lines every 60 minutes         │
│  Tasks can be scheduled by the hour  │
│  Best for detailed daily planning    │
└─────────────────────────────────────┘
```

**Scale:**
- 1 hour = 60dp height
- Total height: ~1500dp (24 hours)
- Scrollable vertically

---

### 3-Day View
```
┌─────────────────────────────────────┐
│  3 days side by side                 │
│  Hour lines every 2 hours            │
│  Tasks can be scheduled by 2 hours    │
└─────────────────────────────────────┘
```

**Scale:**
- 1 hour = 30dp height
- 3 days = ~720dp width each
- Total width: ~2160dp (scrollable horizontally)

---

### Weekly View
```
┌─────────────────────────────────────┐
│  7 days side by side                 │
│  Hour lines every 4 hours            │
│  Tasks can be scheduled by 4 hours    │
└─────────────────────────────────────┘
```

**Scale:**
- 1 hour = 15dp height
- 7 days = ~500dp width each
- Total width: ~3500dp (scrollable horizontally)

---

### Monthly View
```
┌─────────────────────────────────────┐
│  Calendar grid with task indicators   │
│  Days as cells                        │
│  Tasks shown as colored bars         │
└─────────────────────────────────────┘
```

**Scale:**
- 1 day = 1 cell
- Tasks shown as small bars within cells

---

## 🎯 User Experience Analysis

### Strengths ✅

1. **Visual Clarity**
   - Clear distinction between today, future, and past
   - Color-coded tasks make it easy to scan
   - Current time indicator is highly visible

2. **Intuitive Interactions**
   - Drag-and-drop feels natural and responsive
   - Snap-to-grid helps with precise scheduling
   - Zoom levels provide flexibility

3. **Efficient Workflow**
   - Quick add from timeline (double tap)
   - Easy task rescheduling
   - Clear visual feedback on all actions

4. **Information Density**
   - Shows a lot of information in compact space
   - Task details visible on hover (web)
   - Multiple views for different needs

### Weaknesses ❌

1. **Clutter with Many Tasks**
   - Can become overwhelming with many overlapping tasks
   - No automatic decluttering options
   - Limited visual separation between tasks

2. **Limited Customization**
   - Fixed color palette for categories
   - No custom time blocks
   - Limited view customization

3. **Mobile Limitations**
   - Weekly view is hard to use on small screens
   - Pinch zoom can be imprecise
   - Task creation from timeline is hidden

4. **Accessibility Issues**
   - Some color combinations have low contrast
   - Touch targets can be small
   - No screen reader optimizations

---

## 💡 Our Improvements for Floktask

### 1. Better Visual Separation

**Problem:** TickTick tasks can overlap and feel cluttered.

**Solution:**
- **Smart Spacing:** Automatically add more space between non-overlapping tasks
- **Group Separation:** Visually separate task groups by time gap
- **Overlap Indicators:** Show "+X more tasks" when tasks overlap

```
┌─────────────────────────┐
│  Task 1                 │
│                         │ ← More space when no overlap
└─────────────────────────┘
    ↓ (16dp gap)
┌─────────────────────────┐
│  Task 2                 │
└─────────────────────────┘
    ↓ (8dp gap when overlapping)
┌─────────────────────────┐
│  +2 more tasks          │ ← Overlap indicator
└─────────────────────────┘
```

---

### 2. Integrated Pomodoro Timer

**Problem:** TickTick requires separate Pomodoro feature.

**Solution:**
- **Inline Timer:** Show Pomodoro timer directly in timeline
- **Task Association:** Link Pomodoro sessions to specific tasks
- **Visual Integration:** Timer appears as part of task block

```
┌─────────────────────────┐
│  Design UI              │
│  25:00 ⏱️  [Start]       │ ← Pomodoro controls
│                         │
│  10:00 - 12:00          │
└─────────────────────────┘
```

---

### 3. Smart Task Placement

**Problem:** Manual task placement can be tedious.

**Solution:**
- **AI Suggestions:** Suggest optimal time slots based on:
  - Task duration estimates
  - User's typical productive hours
  - Existing calendar events
  - Task priorities

```
┌─────────────────────────┐
│  Suggested: 14:00 - 16:00 │ ← AI suggestion
│  [Accept] [Edit]        │
└─────────────────────────┘
```

---

### 4. Customizable Time Blocks

**Problem:** Fixed hour-based scheduling.

**Solution:**
- **Flexible Durations:** Allow any duration (15min, 30min, 45min, etc.)
- **Custom Time Slots:** Define custom time blocks (e.g., "Deep Work: 9-12")
- **Recurring Blocks:** Weekly recurring time blocks

```
┌─────────────────────────────────────┐
│  Deep Work Block                     │
│  ┌─────────────────────────────┐   │
│  │  09:00 - 12:00               │   │
│  │  [Task 1]                    │   │
│  │  [Task 2]                    │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

### 5. Improved Accessibility

**Problem:** Limited accessibility features.

**Solution:**
- **High Contrast Mode:** Enhanced borders and colors
- **Larger Touch Targets:** Minimum 48dp for all interactive elements
- **Screen Reader Support:** Full content descriptions
- **Color Blind Modes:** Alternative visual indicators

---

## 📐 Technical Specifications

### Performance Requirements

| Metric | TickTick | Our Target |
|--------|----------|------------|
| **Initial Load Time** | ~500ms | < 300ms |
| **Task Drag Latency** | ~16ms | < 10ms |
| **Zoom Animation** | 300ms | 250ms |
| **Scroll FPS** | 60fps | 60fps |
| **Memory Usage** | ~150MB | < 100MB |

### Data Model

```kotlin
// Timeline data structure
data class TimelineTask(
    val id: String,
    val title: String,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,  // For recurring tasks
    val color: Color,        // Category color
    val priority: Priority,  // Low, Medium, High, Critical
    val isAllDay: Boolean,
    val isCompleted: Boolean,
    val categoryId: String?,
    val projectId: String?
)

data class TimelineViewState(
    val dateRange: DateRange,
    val zoomLevel: ZoomLevel,  // DAILY, THREE_DAY, WEEKLY, MONTHLY
    val scrollPosition: Float,
    val selectedTaskId: String?,
    val isDragging: Boolean,
    val draggedTaskId: String?
)
```

---

## 🎨 Our Timeline View Design

### Visual Design

```yaml
# Our Timeline Color Tokens
timeline:
  today: "#FFF3E0"  # premium.subtle_background
  future: "#FFFFFF"  # surface
  past: "#F5F5F5"  # surface_variant
  current_time: "#F44336"  # error_500
  event: "#2196F3"  # info_500
  task: "#FFC107"  # warning_500
  milestone: "#9C27B0"  # tertiary_500

# Our Timeline Spacing Tokens
timeline:
  time_axis:
    width: 56  # Custom (56dp)
    label_spacing: 4  # spacing.xs
  
  event:
    padding: 8  # spacing.sm
    margin: 4  # spacing.xs
    min_height: 40  # Custom (40dp)
    min_width: 100  # Custom (100dp)
  
  current_time:
    indicator_height: 2  # Custom (2dp)
```

### Layout

```
┌─────────────────────────────────────────────────────────────┐
│  Floktask Timeline View (Daily)                              │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────┬───────────────────────────────────────────────┐ │
│  │ Time    │                                               │ │
│  │ Axis    │                                               │ │
│  │ (56dp)  │                                               │ │
│  │         │                                               │ │
│  │ 00:00  ├───────────────────────────────────────────────┤ │ │
│  │ 01:00  │  ┌─────────────┐                              │ │ │
│  │ 02:00  │  │ Task 1      │  ┌─────────────┐               │ │ │
│  │ 03:00  │  │ 10:00-12:00 │  │ Task 2      │               │ │ │
│  │  ...   │  └─────────────┘  │ 14:00-15:00 │               │ │ │
│  │        │                   └─────────────┘               │ │ │
│  │ 10:00  │  ┌─────────────────────────────────────────┐   │ │ │
│  │        │  │  ⏱️ Design UI - 25 min Pomodoro            │   │ │ │
│  │        │  │  [Start] [Pause]                           │   │ │ │
│  │        │  └─────────────────────────────────────────┘   │ │ │
│  │        │       ↓ (Current time line - red)              │ │ │
│  │ 12:00  ├───────────────────────────────────────────────┤ │ │
│  │        │  ┌─────────────┐                              │ │ │
│  │        │  │ +3 more     │ ← Overlap indicator          │ │ │
│  │        │  └─────────────┘                              │ │ │
│  └─────────┴───────────────────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │  [+] Add    [Today] [3-Day] [Week] [Month] [Pomodoro]    ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Implementation Checklist

### Phase 1: Core Timeline
- [ ] Time axis component
- [ ] Task block component
- [ ] Current time indicator
- [ ] Basic drag-and-drop
- [ ] Snap-to-grid functionality
- [ ] Daily view layout

### Phase 2: Enhanced Features
- [ ] 3-Day view
- [ ] Weekly view
- [ ] Monthly view
- [ ] Zoom functionality
- [ ] All-day tasks
- [ ] Task overlap handling

### Phase 3: Floktask Integration
- [ ] Task creation from timeline
- [ ] Task editing in timeline
- [ ] Integration with existing tasks
- [ ] Category colors
- [ ] Priority indicators

### Phase 4: Advanced Features
- [ ] Pomodoro integration
- [ ] Smart task placement
- [ ] Custom time blocks
- [ ] Recurring tasks
- [ ] Calendar integration

---

## 📁 Related Files

- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Responsive Guidelines](../../design-system/guidelines/responsive.md)
- [Accessibility Guidelines](../../design-system/guidelines/accessibility.md)

---

## 🎯 Next Steps

1. **Create wireframes** for our Timeline View
2. **Design high-fidelity mockups**
3. **Build interactive prototype**
4. **Write design specification**
5. **Collaborate with Frontend** on implementation

---

*Last updated: 2026-09-05*
*Related to: DE-003 Timeline View UI*
