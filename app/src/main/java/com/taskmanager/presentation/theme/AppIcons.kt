package com.taskmanager.presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Centralized icon management for the app.
 * Provides unique icons for different screens and features.
 */
object AppIcons {
    
    // ==================== TODAY SCREEN ICONS ====================
    
    /** Primary icon for Today screen FAB */
    val TodayFabIcon: ImageVector = Icons.Filled.Bolt
    
    /** Alternative icon for Today screen */
    val TodayIcon: ImageVector = Icons.Rounded.Today
    
    /** Icon for next tasks section */
    val NextTasksIcon: ImageVector = Icons.Outlined.ArrowForward
    
    /** Icon for focus/pomodoro */
    val FocusIcon: ImageVector = Icons.Filled.Bolt
    
    /** Icon for calendar integration */
    val CalendarIcon: ImageVector = Icons.Filled.DateRange
    
    
    // ==================== HABITS SCREEN ICONS ====================
    
    /** Primary icon for Habits screen FAB - unique from Today */
    val HabitsFabIcon: ImageVector = Icons.Filled.LocalFireDepartment
    
    /** Alternative icon for Habits screen */
    val HabitsIcon: ImageVector = Icons.Rounded.Repeat
    
    /** Icon for habit completion */
    val HabitCompleteIcon: ImageVector = Icons.Filled.CheckCircle
    
    /** Icon for streak */
    val StreakIcon: ImageVector = Icons.Filled.Whatshot
    
    
    // ==================== INBOX SCREEN ICONS ====================

    val InboxFabIcon: ImageVector = Icons.Filled.Inbox

    
    // ==================== UPCOMING SCREEN ICONS ====================

    val UpcomingFabIcon: ImageVector = Icons.Filled.Event

    
    // ==================== PROJECTS SCREEN ICONS ====================
    
    val ProjectsFabIcon: ImageVector = Icons.Filled.CreateNewFolder
    val ProjectsIcon: ImageVector = Icons.Rounded.Folder
    val SubprojectIcon: ImageVector = Icons.Rounded.FolderOpen
    
    
    // ==================== PROJECT DETAIL SCREEN ICONS ====================

    val ProjectDetailFabIcon: ImageVector = Icons.Filled.Add

    
    // ==================== EISENHOWER SCREEN ICONS ====================
    
    val EisenhowerFabIcon: ImageVector = Icons.Filled.Add
    val DoNowIcon: ImageVector = Icons.Filled.PriorityHigh
    val ScheduleIcon: ImageVector = Icons.Filled.Schedule
    val DelegateIcon: ImageVector = Icons.Filled.People
    val EliminateIcon: ImageVector = Icons.Filled.Delete
    
    
    // ==================== CALENDAR SCREEN ICONS ====================

    val CalendarFabIcon: ImageVector = Icons.Filled.CalendarMonth

    
    // ==================== KANBAN SCREEN ICONS ====================
    
    val KanbanFabIcon: ImageVector = Icons.Filled.Add
    val TodoIcon: ImageVector = Icons.Filled.RadioButtonUnchecked
    val InProgressIcon: ImageVector = Icons.Filled.HourglassHalf
    val DoneIcon: ImageVector = Icons.Filled.CheckCircle
    
    
    // ==================== NOTES SCREEN ICONS ====================

    val NotesFabIcon: ImageVector = Icons.Filled.Description

    
    // ==================== TAGS SCREEN ICONS ====================

    val TagsFabIcon: ImageVector = Icons.Filled.Label

    
    // ==================== FINANCE SCREEN ICONS ====================

    val FinanceFabIcon: ImageVector = Icons.Filled.AttachMoney

    
    // ==================== VOICE INPUT ICONS ====================
    
    val VoiceIcon: ImageVector = Icons.Filled.Mic
    val VoiceOffIcon: ImageVector = Icons.Filled.MicOff
    
    
    // ==================== SETTINGS ICONS ====================
    
    val SettingsIcon: ImageVector = Icons.Filled.Settings
    val ThemeIcon: ImageVector = Icons.Filled.Brightness6
    val NotificationsIcon: ImageVector = Icons.Filled.Notifications
    val HelpIcon: ImageVector = Icons.Filled.Help
    val AboutIcon: ImageVector = Icons.Filled.Info
    
    
    // ==================== NAVIGATION ICONS ====================
    
    val NavToday: ImageVector = Icons.Rounded.Home
    val NavHabits: ImageVector = Icons.Rounded.Repeat
    val NavProjects: ImageVector = Icons.Rounded.Folder
    val NavKanban: ImageVector = Icons.Rounded.ViewKanban
    val NavEisenhower: ImageVector = Icons.Rounded.GridView
    val NavCalendar: ImageVector = Icons.Rounded.CalendarMonth
    val NavFinance: ImageVector = Icons.Rounded.AttachMoney
    val NavCrm: ImageVector = Icons.Rounded.PeopleAlt
    
    
    // ==================== ACTION ICONS ====================
    
    val AddIcon: ImageVector = Icons.Filled.Add
    val EditIcon: ImageVector = Icons.Filled.Edit
    val DeleteIcon: ImageVector = Icons.Filled.Delete
    val ShareIcon: ImageVector = Icons.Filled.Share
    val SearchIcon: ImageVector = Icons.Filled.Search
    val FilterIcon: ImageVector = Icons.Filled.FilterList
    val SortIcon: ImageVector = Icons.Filled.Sort
    val MoreIcon: ImageVector = Icons.Filled.MoreVert
    
    
    // ==================== STATUS ICONS ====================
    
    val PriorityHigh: ImageVector = Icons.Filled.PriorityHigh
    val PriorityMedium: ImageVector = Icons.Filled.ArrowUpward
    val PriorityLow: ImageVector = Icons.Filled.ArrowDownward
    val PriorityNone: ImageVector = Icons.Filled.HorizontalRule
    
    
    // ==================== FINANCE ICONS ====================
    
    val IncomeIcon: ImageVector = Icons.Filled.ArrowCircleUp
    val ExpenseIcon: ImageVector = Icons.Filled.ArrowCircleDown
    val TransferIcon: ImageVector = Icons.Filled.SwapHoriz
    val BalanceIcon: ImageVector = Icons.Filled.AccountBalance
    
    
    // ==================== CRM ICONS ====================
    
    val ClientIcon: ImageVector = Icons.Filled.Person
    val DealIcon: ImageVector = Icons.Filled.Business
    val PipelineIcon: ImageVector = Icons.Filled.ViewStream
    val AnalyticsIcon: ImageVector = Icons.Filled.Analytics
}
