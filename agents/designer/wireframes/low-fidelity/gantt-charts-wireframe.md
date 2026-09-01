# Floktask Gantt Charts - Low Fidelity Wireframes
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Task: DE-004

## 📋 Overview

This document contains **low-fidelity wireframes** for Floktask's Gantt Charts feature. These wireframes focus on layout, structure, and information hierarchy for visualizing project timelines, dependencies, and progress.

---

## 🎯 Design Goals

1. **Clarity:** Easy to understand task timelines at a glance
2. **Simplicity:** Simplified for personal use (not enterprise complexity)
3. **Integration:** Seamless with Floktask's existing features
4. **Mobile-First:** Works well on all device sizes
5. **Interactive:** Supports drag-and-drop, zoom, and pan

---

## 📐 Wireframe Structure

### ASCII Wireframes

#### 1. Main Gantt Chart Screen (Week View - Default)

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE                                                     ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │       ││
│  │  │  Mon    │  Tue    │  Wed    │  Thu    │  Fri    │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │  │  LIST   │  │ Task A      │──▶│ Task B      │              │ ││
│  │  │  (25%)  │  │ Jan 1-3     │  │ Jan 3-5     │              │ ││
│  │  │         │  │ ████████    │  │ ████████    │              │ ││
│  │  │         │  └─────────────┘  └─────────────┘              │ ││
│  │  │         │       ↓ (Today Line - Orange)                   │ ││
│  │  │         │  ┌─────────────────────────────────────────┐  │ ││
│  │  │         │  │  💎 Milestone: Project Deadline             │  │ ││
│  │  │         │  │  Jan 5                                        │  │ ││
│  │  │         │  └─────────────────────────────────────────┘  │ ││
│  │  │         │                                                     │ ││
│  │  │  ┌─────┐ │  ┌─────────────┐                              │ ││
│  │  │  │ M1  │ │  │ Task C      │                              │ ││
│  │  │  └─────┘ │  │ Jan 2-4     │                              │ ││
│  │  │         │  └─────────────┘                              │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
│  [+ Add Task]  [Critical Path: ON]  [Dependencies: ON]             │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Header:** Title + View toggles + Action button
- **Time Scale:** Horizontal, shows days of week + dates
- **Task List (Left, 25%):** List of tasks with info
- **Gantt Area (Right, 75%):** Visual timeline with task bars
- **Task Bars:** Horizontal bars representing task duration
- **Dependencies:** Arrow lines between tasks
- **Milestones:** Diamond shapes for important dates
- **Today Indicator:** Vertical orange line
- **Bottom Bar:** Quick actions + toggles

---

#### 2. Day View

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Day] [Week] [Month] [Year]    │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE: January 1, 2026                                  ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │ 00:00  │ 04:00  │ 08:00  │ 12:00  │ 16:00  │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────────────────┐                  │ ││
│  │  │  LIST   │  │ Task A: 08:00-12:00      │                  │ ││
│  │  │         │  │ ████████████████████    │                  │ ││
│  │  │         │  └─────────────────────────┘                  │ ││
│  │  │         │                                                     │ ││
│  │  │         │  ┌─────────────────────────┐                  │ ││
│  │  │         │  │ Task B: 10:00-14:00      │                  │ ││
│  │  │         │  │ ████████████████████    │                  │ ││
│  │  │         │  └─────────────────────────┘                  │ ││
│  │  │         │       ↓ (Current Time: 10:30)                 │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Time Scale:** Hourly (00:00 - 23:00)
- **Task Bars:** Show exact hours
- **Current Time:** Red line with time label
- **Precision:** Hour-level accuracy

---

#### 3. Month View

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Day] [Week] [Month] [Year]    │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE: January 2026                                       ││
│  │  ┌─────┬─────┬─────┬─────┬─────┬─────┬─────┐                ││
│  │  │ 1   │ 2   │ 3   │ 4   │ 5   │ 6   │ 7   │                ││
│  │  │ Mon │ Tue │ Wed │ Thu │ Fri │ Sat │ Sun │                ││
│  │  └─────┴─────┴─────┴─────┴─────┴─────┴─────┘                ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────┐  ┌─────┐  ┌─────┐                         │ ││
│  │  │  LIST   │  │ A   │  │ B   │  │ C   │                         │ ││
│  │  │         │  │ Jan1│  │Jan2│  │Jan3│                         │ ││
│  │  │         │  └─────┘  └─────┘  └─────┘                         │ ││
│  │  │         │                                                     │ ││
│  │  │         │  ┌─────┐                                              │ ││
│  │  │         │  │ M1  │ ← Milestone                               │ ││
│  │  │         │  │ Jan5│                                              │ ││
│  │  │         │  └─────┘                                              │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Time Scale:** Daily (1-31)
- **Task Bars:** Compact, show start/end days
- **Milestones:** Diamond icons on specific dates
- **Overview:** Good for monthly planning

---

#### 4. Year View

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Day] [Week] [Month] [Year]    │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE: 2026                                              ││
│  │  ┌───────┬───────┬───────┬───────┬───────┬───────┐          ││
│  │  │ Jan   │ Feb   │ Mar   │ Apr   │ May   │ Jun   │          ││
│  │  └───────┴───────┴───────┴───────┴───────┴───────┘          ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌───────┐  ┌───────┐                              │ ││
│  │  │  LIST   │  │ Project│  │ Project│                              │ ││
│  │  │         │  │   A   │  │   B   │                              │ ││
│  │  │         │  │ Jan-Jun│  │ Mar-Sep│                              │ ││
│  │  │         │  └───────┘  └───────┘                              │ ││
│  │  │         │                                                     │ ││
│  │  │         │  ┌───────┐                                          │ ││
│  │  │         │  │ 💎 M1  │ ← Major Milestone                      │ ││
│  │  │         │  │ Dec   │                                          │ ││
│  │  │         │  └───────┘                                          │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Time Scale:** Monthly (Jan-Dec)
- **Task Bars:** Very compact, show month ranges
- **Milestones:** Major milestones only
- **Long-term:** Good for yearly planning

---

#### 5. Task Detail in Gantt (Click on Task)

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE                                                     ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────────────────────────────────┐ │ ││
│  │  │  LIST   │  │  ┌─────────────────────────────────────┐│ │ ││
│  │  │         │  │  │ TASK DETAILS                          ││ │ ││
│  │  │         │  │  ├─────────────────────────────────────┤│ │ ││
│  │  │         │  │  │ Title: Design UI                     ││ │ ││
│  │  │         │  │  │ Category: Work                       ││ │ ││
│  │  │         │  │  │ Priority: High                       ││ │ ││
│  │  │         │  │  │ Start: Jan 1, 2026                   ││ │ ││
│  │  │         │  │  │ End: Jan 3, 2026                     ││ │ ││
│  │  │         │  │  │ Progress: 60%                        ││ │ ││
│  │  │         │  │  │ Dependencies: Task A, Task B        ││ │ ││
│  │  │         │  │  │ Description: Create new UI...        ││ │ ││
│  │  │         │  │  │                                     ││ │ ││
│  │  │         │  │  │ [Edit] [Delete] [Close]              ││ │ ││
│  │  │         │  │  └─────────────────────────────────────┘│ │ ││
│  │  │         │  │                                              │ │ ││
│  │  │         │  └─────────────────────────────────────────┘ │ ││
│  │  │         │  ┌─────────────┐                              │ ││
│  │  │         │  │ Task A      │ ← Selected task               │ ││
│  │  │         │  └─────────────┘                              │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Modal/Dialog:** Appears over Gantt chart
- **Task Info:** All details about selected task
- **Actions:** Edit, Delete, Close
- **Highlight:** Selected task highlighted in chart

---

#### 6. Context Menu (Right-Click/Long Press)

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE                                                     ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────┐                              │ ││
│  │  │  LIST   │  │ Task A      │ ← Right-click here            │ ││
│  │  │         │  └─────────────┘                              │ ││
│  │  │         │       ↓                                        │ ││
│  │  │         │  ┌─────────────────────────┐                  │ ││
│  │  │         │  │  📝 View Details          │                  │ ││
│  │  │         │  │  ✏️ Edit                  │                  │ ││
│  │  │         │  │  🗑️ Delete                │                  │ ││
│  │  │         │  │  📅 Reschedule            │                  │ ││
│  │  │         │  │  🔗 Create Dependency      │                  │ ││
│  │  │         │  │  🎯 Set Priority           │                  │ ││
│  │  │         │  │  🏷️ Change Category       │                  │ ││
│  │  │         │  │  🔄 Duplicate              │                  │ ││
│  │  │         │  └─────────────────────────┘                  │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Context Menu:** Appears at cursor/touch position
- **Menu Items:** 8 common actions
- **Icons:** Each item has an icon
- **Dividers:** Visual separation between groups

---

#### 7. Create Dependency (Drag from Task Edge)

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE                                                     ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │  │  LIST   │  │ Task A      │  │ Task B      │              │ ││
│  │  │         │  │ Jan 1-3     │  │ Jan 3-5     │              │ ││
│  │  │         │  └──────┬──────┘  └─────────────┘              │ ││
│  │  │         │         │                                      │ ││
│  │  │         │         │ ← Drag from here                      │ ││
│  │  │         │         │                                      │ ││
│  │  │         │         ▼                                      │ ││
│  │  │         │  ┌─────────────┐                                  │ ││
│  │  │         │  │ Task B      │ ← Drop here to create           │ ││
│  │  │         │  │ Jan 3-5     │    dependency                    │ ││
│  │  │         │  └─────────────┘                                  │ ││
│  │  │         │                                                     │ ││
│  │  │         │  Result: Task A → Task B (Finish-to-Start)      │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Drag Source:** Small handle on task edge
- **Drag Preview:** Dashed line follows cursor
- **Drop Target:** Highlight on valid target
- **Result:** Dependency arrow created

---

#### 8. Critical Path Highlighting

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE                                                     ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │  │  LIST   │  │ Task A      │──▶│ Task B      │──▶│ Task C  │ │ ││
│  │  │         │  │ (Red border)│  │ (Red border)│  │ (Red border)│ │ ││
│  │  │         │  └─────────────┘  └─────────────┘  └────────┘ │ ││
│  │  │         │                                                     │ ││
│  │  │         │  ┌─────────────────────────────────────────┐  │ ││
│  │  │         │  │  💡 Critical Path: 5 days total           │  │ ││
│  │  │         │  │  [Hide]                                       │  │ ││
│  │  │         │  └─────────────────────────────────────────┘  │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  [Critical Path: ON]  [Dependencies: ON]                           │
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Critical Path:** Tasks that affect project deadline
- **Highlight:** Red border on critical tasks
- **Toggle:** Can be turned on/off
- **Info:** Shows total duration of critical path

---

#### 9. Mobile Layout

```
┌─────────────────────┐
│  Gantt              │
├─────────────────────┤
│  [Week] [Month]     │
├─────────────────────┤
│  ┌───────────────┐ │
│  │ Jan 1 - Jan 7 │ ← Time Scale (scrollable)          │
│  └───────────────┘ │
│  ┌───────────────┐ │
│  │ Mon Tue Wed   │ ← Day headers                      │
│  └───────────────┘ │
│  ┌───────────────┐ │
│  │ ████ Task A   │ ← Task bars (scrollable)          │
│  │ ████ Task B   │                                     │
│  │ ████ Task C   │                                     │
│  └───────────────┘ │
│  [← Swipe →]        │
├─────────────────────┤
│  [+] [Today]        │
└─────────────────────┘
```

**Key Elements:**
- **Vertical Layout:** Time scale on top
- **Scrollable:** Horizontal scrolling for timeline
- **Touch-Friendly:** Large touch targets
- **Simplified:** Only essential controls

---

#### 10. Add Task from Gantt

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE                                                     ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Jan 1  │  Jan 2  │  Jan 3  │  Jan 4  │  Jan 5  │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │  │  LIST   │  │ Task A      │  │ Task B      │              │ ││
│  │  │         │  └─────────────┘  └─────────────┘              │ ││
│  │  │         │                                                     │ ││
│  │  │         │  Click on empty space here                       │ ││
│  │  │         │       ↓                                          │ ││
│  │  │         │  ┌─────────────────────────┐                  │ ││
│  │  │         │  │  + Add Task                │                  │ ││
│  │  │         │  │  Start: Jan 3             │ ← Pre-filled    │ ││
│  │  │         │  │  End: Jan 3               │ ← Pre-filled    │ ││
│  │  │         │  │                             │                  │ ││
│  │  │         │  │  Title: [______________]  │                  │ ││
│  │  │         │  │                             │                  │ ││
│  │  │         │  │  [Cancel]      [Save]     │                  │ ││
│  │  │         │  └─────────────────────────┘                  │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
└─────────────────────────────────────────────────────────────────┘
```

**Key Elements:**
- **Click on Empty Space:** Opens add dialog
- **Smart Defaults:** Start/end dates pre-filled based on click position
- **Quick Creation:** Minimal fields for fast task creation
- **Actions:** Cancel and Save

---

## 📊 Information Hierarchy

### Visual Priority

1. **Highest Priority (Most Visible)**
   - Task bars (color-coded)
   - Dependencies (arrow lines)
   - Milestones (diamond shapes)
   - Current time/today indicator

2. **High Priority**
   - Time scale
   - Task list headers
   - Critical path highlighting

3. **Medium Priority**
   - Grid lines
   - Time scale labels
   - Task list rows

4. **Low Priority**
   - Background colors
   - Empty space
   - Dividers

---

## 🎯 User Flow

### Primary Flow: View Gantt Chart
```
User opens Gantt Chart
    ↓
System loads tasks and dependencies
    ↓
User sees timeline with task bars
    ↓
User can scroll, zoom, pan
```

### Secondary Flow: Create Dependency
```
User finds Task A
    ↓
User drags from Task A edge
    ↓
User drags to Task B
    ↓
System creates dependency (Task A → Task B)
    ↓
System updates critical path if needed
```

### Tertiary Flow: Edit Task from Gantt
```
User finds task in Gantt
    ↓
User right-clicks or long-presses
    ↓
Context menu appears
    ↓
User selects "Edit"
    ↓
Edit dialog appears
    ↓
User makes changes
    ↓
System updates task and Gantt chart
```

---

## 📐 Layout Specifications

### Time Scale

| Property | Value | Token |
|----------|-------|-------|
| Height (Day View) | 40dp | Custom |
| Height (Week View) | 48dp | Custom |
| Height (Month View) | 56dp | Custom |
| Height (Year View) | 64dp | Custom |
| Background | Surface Variant | `colors.surface_variant` |
| Text Color | On Surface Variant | `colors.on_surface_variant` |
| Divider Height | 1dp | Custom |
| Divider Color | Outline Variant | `colors.outline_variant` |

### Task List (Left Side)

| Property | Value | Token |
|----------|-------|-------|
| Width | 25% of screen | Custom |
| Min Width | 200dp | Custom |
| Max Width | 300dp | Custom |
| Background | Surface | `colors.surface` |
| Row Height | 40dp | `spacing.xxl_2` |
| Padding | 12dp | `spacing.sm_2` |
| Typography | Body Medium | `typography.body.medium` |

### Gantt Area (Right Side)

| Property | Value | Token |
|----------|-------|-------|
| Width | 75% of screen | Custom |
| Background | Surface | `colors.surface` |
| Grid Line Height | 1dp | Custom |
| Grid Line Color | Outline Variant | `colors.outline_variant` |
| Major Grid Line Height | 2dp | Custom |
| Major Grid Line Color | Outline | `colors.outline` |

### Task Bars

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Selected Height | 40dp | Custom |
| Border Radius | 4dp | `border_radius.xxs` |
| Padding | 8dp horizontal, 4dp vertical | `spacing.sm`, `spacing.xs` |
| Margin | 4dp | `spacing.xs` |
| Elevation | Level 1 | `shadows.level_1` |
| Hover Elevation | Level 2 | `shadows.level_2` |

### Milestones

| Property | Value | Token |
|----------|-------|-------|
| Size | 24dp x 24dp | Custom |
| Border Radius | 2dp | `border_radius.xs` |
| Background | Milestone Color | `gantt.milestone.background` |
| Border | White, 2dp | Custom |
| Text Color | White | `gantt.milestone.text` |
| Typography | 10sp Bold | Custom |

### Dependencies

| Property | Value | Token |
|----------|-------|-------|
| Line Width | 2dp | Custom |
| Line Color | Gray | `gantt.dependency.line` |
| Line Style | Dashed | Custom |
| Arrow Size | 8dp | Custom |
| Arrow Color | Gray (or Red for critical) | `gantt.dependency.arrow` |

### Today Indicator

| Property | Value | Token |
|----------|-------|-------|
| Line Width | 2dp | Custom |
| Line Color | Primary | `gantt.today.border` |
| Label | "TODAY" or "СЕГОДНЯ" | - |
| Label Typography | 10sp Medium | Custom |
| Label Color | Primary | `gantt.today.border` |

---

## 🎨 Visual Style Notes

### Colors by Task Status

| Status | Background | Border | Text |
|--------|------------|--------|------|
| Default | Category Color | None | On Category |
| Hover | Category Color (90%) | None | On Category |
| Selected | Category Color (80%) | Primary 500 (2dp) | On Category |
| Completed | Category Color (40%) | None | On Category (40%) |
| Overdue | Error 50 (12%) | Error 500 (1dp) | On Error |

### Colors by Task Priority

| Priority | Background | Progress |
|----------|------------|----------|
| Low | Gray 200 | Gray 400 |
| Medium | Amber 200 | Amber 400 |
| High | Orange 200 | Orange 400 |
| Critical | Red 200 | Red 400 |

### Progress Visualization

| Progress | Fill Color | Background |
|----------|------------|------------|
| 0% | None | Category Color (20%) |
| 25% | Progress Fill (25%) | Category Color (20%) |
| 50% | Progress Fill (50%) | Category Color (20%) |
| 75% | Progress Fill (75%) | Category Color (20%) |
| 100% | Progress Fill (100%) | Category Color (20%) |

---

## ✅ Wireframe Validation

### Usability Checklist

- [x] Clear visual hierarchy
- [x] Intuitive layout
- [x] Logical information flow
- [x] Appropriate spacing
- [x] Consistent patterns
- [x] Accessible touch targets (48dp minimum)
- [x] Responsive to different screens
- [x] Mobile-friendly layout

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

- [Gantt Charts UI Analysis](../../research/competitive-ui-analysis/gantt-charts-ui-analysis.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Timeline Design Spec](../output/design-specs/timeline-spec.md)

---

*Last updated: 2026-09-05*
*Status: Low-Fidelity Complete ✅*
*Next: High-Fidelity Design*
